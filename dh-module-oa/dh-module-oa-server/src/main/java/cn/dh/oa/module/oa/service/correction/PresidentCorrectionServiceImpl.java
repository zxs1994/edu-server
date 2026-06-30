package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.framework.common.util.bill.BillCodeUtils;
import cn.dh.oa.module.bpm.api.task.BpmProcessInstanceApi;
import cn.dh.oa.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.oa.module.bpm.enums.task.BpmReasonEnum;
import cn.dh.oa.module.oa.controller.admin.correction.vo.PresidentCorrectionInitiateReqVO;
import cn.dh.oa.module.oa.controller.admin.correction.vo.BillCorrectionHistoryItemVO;
import cn.dh.oa.module.oa.controller.admin.correction.vo.BillCorrectionHistoryRespVO;
import cn.dh.oa.module.oa.dal.dataobject.correction.BillCorrectionStateDO;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.module.oa.dal.mysql.correction.BillCorrectionStateMapper;
import cn.dh.oa.module.oa.dal.mysql.correction.CorrectionBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.enums.correction.OaBillCorrectionStatusEnum;
import cn.dh.oa.module.oa.enums.correction.OaCorrectionTypeEnum;
import cn.dh.oa.module.oa.service.correction.dto.BillCorrectionSourceDTO;
import cn.dh.oa.module.oa.service.correction.freeze.BillFreezeHandlerRegistry;
import cn.dh.oa.module.system.api.user.AdminUserApi;
import cn.dh.oa.module.system.api.user.dto.AdminUserRespDTO;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;

@Slf4j
@Service
@Validated
public class PresidentCorrectionServiceImpl implements PresidentCorrectionService {

    private static final String VAR_IS_RE_APPROVAL = "isReApproval";
    private static final String VAR_CORRECTION_BILL_ID = "correctionBillId";
    private static final String VAR_PREVIOUS_PROCESS_INSTANCE_ID = "previousProcessInstanceId";

    @Resource
    private CorrectionBillMapper correctionBillMapper;
    @Resource
    private BillCorrectionStateMapper billCorrectionStateMapper;
    @Resource
    private BillCorrectionSourceService billCorrectionSourceService;
    @Resource
    private BillFreezeHandlerRegistry billFreezeHandlerRegistry;
    @Resource
    private BpmProcessInstanceApi processInstanceApi;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long initiate(Long presidentUserId, PresidentCorrectionInitiateReqVO reqVO) {
        BillCorrectionSourceDTO source = billCorrectionSourceService.loadRequired(
                reqVO.getSourceBillType(), reqVO.getSourceBillId());
        validateSourceBill(source);
        validateCouncilFields(reqVO);

        AdminUserRespDTO president = adminUserApi.getUser(presidentUserId).getCheckedData();
        long version = correctionBillMapper.countBySourceBill(reqVO.getSourceBillType(), reqVO.getSourceBillId()) + 1;

        CorrectionBillDO correction = buildCorrectionBill(reqVO, source, president, version);
        correctionBillMapper.insert(correction);

        billFreezeHandlerRegistry.freeze(source.getBillType(), source.getBillId());
        upsertBillState(source, correction.getId(), 1,
                OaCorrectionTypeEnum.isCouncil(reqVO.getCorrectionType())
                        ? OaBillCorrectionStatusEnum.COUNCIL_OVERRIDE.getStatus()
                        : OaBillCorrectionStatusEnum.IN_PROGRESS.getStatus());

        if (OaCorrectionTypeEnum.isCouncil(reqVO.getCorrectionType())) {
            handleCouncilPath(correction, source, reqVO);
            return correction.getId();
        }
        handleObjectionPath(correction, source, president);
        return correction.getId();
    }

    private void handleObjectionPath(CorrectionBillDO correction, BillCorrectionSourceDTO source,
                                     AdminUserRespDTO president) {
        Map<String, Object> variables = new HashMap<>(
                processInstanceApi.getHistoricProcessVariables(source.getProcessInstanceId()).getCheckedData());
        variables.put(VAR_IS_RE_APPROVAL, true);
        variables.put(VAR_CORRECTION_BILL_ID, correction.getId());
        variables.put(VAR_PREVIOUS_PROCESS_INSTANCE_ID, source.getProcessInstanceId());

        String newProcessInstanceId = processInstanceApi.createProcessInstance(source.getCreatorUserId(),
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(source.getBillType())
                        .setBusinessKey(String.valueOf(source.getBillId()))
                        .setVariables(variables)
                        .setPreserveHistory(true)
        ).getCheckedData();

        correctionBillMapper.updateById(new CorrectionBillDO()
                .setId(correction.getId())
                .setNewProcessInstanceId(newProcessInstanceId)
                .setCorrectionStatus(OaBillCorrectionStatusEnum.IN_PROGRESS.getStatus())
                .setFreezeStatus(1));

        billCorrectionSourceService.updateForReApproval(source, newProcessInstanceId);

        processInstanceApi.cancelProcessInstanceByReason(source.getProcessInstanceId(),
                BpmReasonEnum.PRESIDENT_CORRECTION_REVOKE.format(president.getNickname(), correction.getCorrectionReason()))
                .checkError();

        log.info("[handleObjectionPath] 会长纠错重审已发起 correctionId={}, newPi={}", correction.getId(), newProcessInstanceId);
    }

    private void handleCouncilPath(CorrectionBillDO correction, BillCorrectionSourceDTO source,
                                   PresidentCorrectionInitiateReqVO reqVO) {
        billFreezeHandlerRegistry.applyCouncilOverride(source.getBillType(), source.getBillId(), reqVO.getCorrectionResult());
        correctionBillMapper.updateById(new CorrectionBillDO()
                .setId(correction.getId())
                .setCorrectionResult(reqVO.getCorrectionResult())
                .setCorrectionStatus(OaBillCorrectionStatusEnum.COUNCIL_OVERRIDE.getStatus())
                .setFreezeStatus(1));
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(source.getBillType(), source.getBillId());
        if (state != null) {
            billCorrectionStateMapper.updateById(new BillCorrectionStateDO()
                    .setId(state.getId())
                    .setCorrectionStatus(OaBillCorrectionStatusEnum.COUNCIL_OVERRIDE.getStatus())
                    .setActiveCorrectionBillId(null));
        }
        log.info("[handleCouncilPath] 理事会决议纠错完成 correctionId={}, billId={}", correction.getId(), source.getBillId());
    }

    private CorrectionBillDO buildCorrectionBill(PresidentCorrectionInitiateReqVO reqVO, BillCorrectionSourceDTO source,
                                                 AdminUserRespDTO president, long version) {
        return CorrectionBillDO.builder()
                .billCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CORRECTION_BILL))
                .sourceBillType(reqVO.getSourceBillType())
                .sourceBillId(reqVO.getSourceBillId())
                .sourceBillCode(source.getBillCode())
                .sourceBillTitle(source.getBillTitle())
                .sourceProcessInstanceId(source.getProcessInstanceId())
                .correctionReason(reqVO.getCorrectionReason())
                .correctionType(reqVO.getCorrectionType())
                .councilDecision(reqVO.getCouncilDecision() != null ? reqVO.getCouncilDecision()
                        : (OaCorrectionTypeEnum.isCouncil(reqVO.getCorrectionType()) ? 1 : 0))
                .councilDecisionFile(reqVO.getCouncilDecisionFile())
                .correctionResult(reqVO.getCorrectionResult())
                .revokeTime(LocalDateTime.now())
                .revokeUserId(president.getId())
                .revokeUserName(president.getNickname())
                .approvalVersion((int) version)
                .freezeStatus(1)
                .correctionStatus(OaBillCorrectionStatusEnum.IN_PROGRESS.getStatus())
                .creatorName(president.getNickname())
                .companyId(source.getCompanyId())
                .companyName(source.getCompanyName())
                .deptId(source.getDeptId())
                .deptName(source.getDeptName())
                .build();
    }

    private void validateSourceBill(BillCorrectionSourceDTO source) {
        Integer processStatus = source.getProcessStatus();
        if (processStatus == null
                || BpmProcessInstanceStatusEnum.NOT_START.getStatus().equals(processStatus)) {
            throw exception(CORRECTION_SOURCE_BILL_NOT_APPROVED);
        }
        if (StrUtil.isBlank(source.getProcessInstanceId())) {
            throw exception(CORRECTION_SOURCE_BILL_NOT_EXISTS);
        }
        if (billCorrectionStateMapper.existsActiveCorrection(source.getBillType(), source.getBillId())) {
            throw exception(CORRECTION_BILL_ALREADY_IN_PROGRESS);
        }
        if (source.getCreatorUserId() == null) {
            throw exception(CORRECTION_SOURCE_BILL_NOT_EXISTS);
        }
    }

    private void validateCouncilFields(PresidentCorrectionInitiateReqVO reqVO) {
        if (!OaCorrectionTypeEnum.isCouncil(reqVO.getCorrectionType())) {
            return;
        }
        if (StrUtil.isBlank(reqVO.getCorrectionResult())) {
            throw exception(CORRECTION_COUNCIL_RESULT_REQUIRED);
        }
        if (StrUtil.isBlank(reqVO.getCouncilDecisionFile())) {
            throw exception(CORRECTION_COUNCIL_FILE_REQUIRED);
        }
    }

    private void upsertBillState(BillCorrectionSourceDTO source, Long correctionId, int freezeStatus, int correctionStatus) {
        BillCorrectionStateDO existing = billCorrectionStateMapper.selectByBill(source.getBillType(), source.getBillId());
        if (existing == null) {
            billCorrectionStateMapper.insert(BillCorrectionStateDO.builder()
                    .billType(source.getBillType())
                    .billId(source.getBillId())
                    .freezeStatus(freezeStatus)
                    .correctionStatus(correctionStatus)
                    .lastProcessInstanceId(source.getProcessInstanceId())
                    .activeCorrectionBillId(correctionId)
                    .build());
            return;
        }
        billCorrectionStateMapper.updateById(new BillCorrectionStateDO()
                .setId(existing.getId())
                .setFreezeStatus(freezeStatus)
                .setCorrectionStatus(correctionStatus)
                .setLastProcessInstanceId(source.getProcessInstanceId())
                .setActiveCorrectionBillId(correctionId));
    }

    @Override
    public BillCorrectionHistoryRespVO getBillHistory(String sourceBillType, Long sourceBillId) {
        BillCorrectionHistoryRespVO resp = new BillCorrectionHistoryRespVO();
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(sourceBillType, sourceBillId);
        if (state != null) {
            resp.setFreezeStatus(state.getFreezeStatus());
            resp.setCorrectionStatus(state.getCorrectionStatus());
        }
        List<CorrectionBillDO> list = correctionBillMapper.selectListBySourceBill(sourceBillType, sourceBillId);
        resp.setItems(BeanUtils.toBean(list, BillCorrectionHistoryItemVO.class));
        return resp;
    }

}
