package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.oa.module.oa.api.correction.OaPresidentCorrectionApi;
import cn.dh.oa.module.oa.api.correction.dto.OaCorrectionRevokeNodeDTO;
import cn.dh.oa.module.oa.dal.dataobject.correction.BillCorrectionStateDO;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.module.oa.dal.mysql.correction.BillCorrectionStateMapper;
import cn.dh.oa.module.oa.dal.mysql.correction.CorrectionBillMapper;
import cn.dh.oa.module.oa.enums.correction.OaBillCorrectionStatusEnum;
import cn.dh.oa.module.oa.service.correction.dto.BillCorrectionSourceDTO;
import cn.dh.oa.module.oa.service.correction.freeze.BillFreezeHandlerRegistry;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 会长纠错 API 实现（供 BPM 模块调用，不依赖 BpmProcessInstanceApi，避免循环依赖）
 */
@Slf4j
@Service
public class OaPresidentCorrectionApiImpl implements OaPresidentCorrectionApi {

    @Resource
    private CorrectionBillMapper correctionBillMapper;
    @Resource
    private BillCorrectionStateMapper billCorrectionStateMapper;
    @Resource
    private BillFreezeHandlerRegistry billFreezeHandlerRegistry;
    @Resource
    private BillCorrectionSourceService billCorrectionSourceService;

    @Override
    public OaCorrectionRevokeNodeDTO getRevokeNode(String sourceProcessInstanceId) {
        CorrectionBillDO correction = correctionBillMapper.selectLatestBySourceProcessInstanceId(sourceProcessInstanceId);
        if (correction == null) {
            return null;
        }
        OaCorrectionRevokeNodeDTO dto = new OaCorrectionRevokeNodeDTO();
        dto.setRevokeUserId(correction.getRevokeUserId());
        dto.setRevokeUserName(correction.getRevokeUserName());
        dto.setCorrectionReason(correction.getCorrectionReason());
        dto.setRevokeTime(correction.getRevokeTime());
        return dto;
    }

    @Override
    public void onReApprovalProcessCompleted(String processDefinitionKey, String businessKey,
                                             String processInstanceId, Integer status) {
        if (!BillCorrectionSourceService.SUPPORTED_BILL_TYPES.contains(processDefinitionKey)) {
            return;
        }
        Long billId = Long.parseLong(businessKey);
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(processDefinitionKey, billId);
        CorrectionBillDO correction = resolveCurrentCorrection(state, processDefinitionKey, billId);
        if (correction == null || !OaBillCorrectionStatusEnum.shouldDisplayOverlay(correction.getCorrectionStatus())) {
            return;
        }
        if (!isCorrectionReApprovalProcess(correction, processDefinitionKey, billId, processInstanceId)) {
            return;
        }
        if (BpmProcessInstanceStatusEnum.APPROVE.getStatus().equals(status)) {
            billCorrectionSourceService.applyReApprovalCompleted(processDefinitionKey, billId, processInstanceId, status);
            billFreezeHandlerRegistry.unfreeze(processDefinitionKey, billId);
            correctionBillMapper.updateById(new CorrectionBillDO()
                    .setId(correction.getId())
                    .setCorrectionStatus(OaBillCorrectionStatusEnum.COMPLETED.getStatus())
                    .setFreezeStatus(2));
            updateBillStateCompleted(processDefinitionKey, billId);
            log.info("[onReApprovalProcessCompleted] 纠错重审通过 billId={}", billId);
        }
    }

    @Override
    public boolean isBillFrozen(String billType, Long billId) {
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        return state != null && Objects.equals(state.getFreezeStatus(), 1);
    }

    @Override
    public boolean shouldDisplayCorrectionOverlay(String billType, Long billId) {
        if (!billCorrectionSourceService.exists(billType, billId)) {
            return false;
        }
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        return isAwaitingResubmit(state, billType, billId);
    }

    @Override
    public boolean isAwaitingResubmitAfterCorrection(String billType, Long billId) {
        if (!billCorrectionSourceService.exists(billType, billId)) {
            return false;
        }
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        return isAwaitingResubmit(state, billType, billId);
    }

    @Override
    public void onSourceBillResubmitted(String billType, Long billId, String processInstanceId) {
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        if (state == null || !Objects.equals(state.getFreezeStatus(), 1)
                || !OaBillCorrectionStatusEnum.shouldDisplayOverlay(state.getCorrectionStatus())) {
            return;
        }
        CorrectionBillDO correction = resolveCurrentCorrection(state, billType, billId);
        if (correction == null) {
            return;
        }
        correctionBillMapper.updateById(new CorrectionBillDO()
                .setId(correction.getId())
                .setNewProcessInstanceId(processInstanceId));
        if (!Objects.equals(state.getActiveCorrectionBillId(), correction.getId())) {
            billCorrectionStateMapper.updateById(new BillCorrectionStateDO()
                    .setId(state.getId())
                    .setActiveCorrectionBillId(correction.getId()));
        }
        log.info("[onSourceBillResubmitted] 纠错中单据已重新发起流程 billType={}, billId={}, pi={}",
                billType, billId, processInstanceId);
    }

    @Override
    public boolean shouldSyncFrozenBillProcessStatus(String billType, Long billId, String processInstanceId) {
        if (StrUtil.isBlank(processInstanceId)) {
            return false;
        }
        if (!billCorrectionSourceService.exists(billType, billId)) {
            return false;
        }
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        if (state == null) {
            return false;
        }
        CorrectionBillDO correction = resolveCurrentCorrection(state, billType, billId);
        return correction != null && isCorrectionReApprovalProcess(correction, billType, billId, processInstanceId);
    }

    @Override
    public Set<String> listProtectedSourceProcessInstanceIds(String billType, Long billId) {
        Set<String> ids = new HashSet<>();
        List<CorrectionBillDO> corrections = correctionBillMapper.selectListBySourceBill(billType, billId);
        for (CorrectionBillDO correction : corrections) {
            if (StrUtil.isNotBlank(correction.getSourceProcessInstanceId())) {
                ids.add(correction.getSourceProcessInstanceId());
            }
        }
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        if (state != null && StrUtil.isNotBlank(state.getLastProcessInstanceId())) {
            ids.add(state.getLastProcessInstanceId());
        }
        return ids;
    }

    @Override
    public boolean shouldHideCorrectedSourceProcessInstance(String billType, Long billId, String processInstanceId) {
        if (StrUtil.isBlank(billType) || billId == null || StrUtil.isBlank(processInstanceId)) {
            return false;
        }
        CorrectionBillDO correction = correctionBillMapper.selectLatestBySourceProcessInstanceId(processInstanceId);
        if (correction == null
                || !Objects.equals(correction.getSourceBillType(), billType)
                || !Objects.equals(correction.getSourceBillId(), billId)) {
            return false;
        }
        if (!billCorrectionSourceService.exists(billType, billId)) {
            return true;
        }
        try {
            return hasResubmittedProcess(correction, billType, billId);
        } catch (RuntimeException ex) {
            log.warn("[shouldHideCorrectedSourceProcessInstance] 判断纠错原流程隐藏失败 billType={}, billId={}, pi={}",
                    billType, billId, processInstanceId, ex);
            return false;
        }
    }

    private void updateBillStateCompleted(String billType, Long billId) {
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        if (state == null) {
            return;
        }
        billCorrectionStateMapper.updateById(new BillCorrectionStateDO()
                .setId(state.getId())
                .setFreezeStatus(0)
                .setCorrectionStatus(OaBillCorrectionStatusEnum.COMPLETED.getStatus())
                .setActiveCorrectionBillId(null));
    }

    private boolean isAwaitingResubmit(BillCorrectionStateDO state, String billType, Long billId) {
        if (state == null || !Objects.equals(state.getFreezeStatus(), 1)
                || !OaBillCorrectionStatusEnum.shouldDisplayOverlay(state.getCorrectionStatus())) {
            return false;
        }
        CorrectionBillDO correction = resolveCurrentCorrection(state, billType, billId);
        return correction == null || !hasResubmittedProcess(correction, billType, billId);
    }

    private CorrectionBillDO resolveCurrentCorrection(BillCorrectionStateDO state, String billType, Long billId) {
        if (state == null) {
            return null;
        }
        if (state.getActiveCorrectionBillId() != null) {
            return correctionBillMapper.selectById(state.getActiveCorrectionBillId());
        }
        return correctionBillMapper.selectLatestBySourceBill(billType, billId);
    }

    private boolean isCorrectionReApprovalProcess(CorrectionBillDO correction, String billType,
                                                  Long billId, String processInstanceId) {
        if (Objects.equals(correction.getNewProcessInstanceId(), processInstanceId)) {
            return true;
        }
        if (StrUtil.isNotBlank(correction.getNewProcessInstanceId())) {
            return false;
        }
        BillCorrectionSourceDTO source = billCorrectionSourceService.loadRequired(billType, billId);
        return Objects.equals(source.getProcessInstanceId(), processInstanceId)
                && !Objects.equals(source.getProcessInstanceId(), correction.getSourceProcessInstanceId());
    }

    private boolean hasResubmittedProcess(CorrectionBillDO correction, String billType, Long billId) {
        if (StrUtil.isNotBlank(correction.getNewProcessInstanceId())) {
            return true;
        }
        BillCorrectionSourceDTO source = billCorrectionSourceService.loadRequired(billType, billId);
        return StrUtil.isNotBlank(source.getProcessInstanceId())
                && !Objects.equals(source.getProcessInstanceId(), correction.getSourceProcessInstanceId());
    }

}
