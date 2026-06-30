package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.oa.module.oa.api.correction.OaPresidentCorrectionApi;
import cn.dh.oa.module.oa.api.correction.dto.OaCorrectionRevokeNodeDTO;
import cn.dh.oa.module.oa.dal.dataobject.correction.BillCorrectionStateDO;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.module.oa.dal.mysql.correction.BillCorrectionStateMapper;
import cn.dh.oa.module.oa.dal.mysql.correction.CorrectionBillMapper;
import cn.dh.oa.module.oa.enums.correction.OaBillCorrectionStatusEnum;
import cn.dh.oa.module.oa.service.correction.BillCorrectionSourceService;
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
        if (state == null || state.getActiveCorrectionBillId() == null) {
            return;
        }
        CorrectionBillDO correction = correctionBillMapper.selectById(state.getActiveCorrectionBillId());
        if (correction == null || !OaBillCorrectionStatusEnum.IN_PROGRESS.getStatus().equals(correction.getCorrectionStatus())) {
            return;
        }
        if (!Objects.equals(correction.getNewProcessInstanceId(), processInstanceId)) {
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
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        return state != null
                && Objects.equals(state.getFreezeStatus(), 1)
                && OaBillCorrectionStatusEnum.shouldDisplayOverlay(state.getCorrectionStatus());
    }

    @Override
    public boolean isAwaitingResubmitAfterCorrection(String billType, Long billId) {
        if (!shouldDisplayCorrectionOverlay(billType, billId)) {
            return false;
        }
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        if (state == null) {
            return false;
        }
        if (OaBillCorrectionStatusEnum.COUNCIL_OVERRIDE.getStatus().equals(state.getCorrectionStatus())) {
            return true;
        }
        if (state.getActiveCorrectionBillId() == null) {
            return true;
        }
        CorrectionBillDO correction = correctionBillMapper.selectById(state.getActiveCorrectionBillId());
        return correction == null || StrUtil.isBlank(correction.getNewProcessInstanceId());
    }

    @Override
    public void onSourceBillResubmitted(String billType, Long billId, String processInstanceId) {
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        if (state == null || !Objects.equals(state.getFreezeStatus(), 1)
                || !OaBillCorrectionStatusEnum.IN_PROGRESS.getStatus().equals(state.getCorrectionStatus())
                || state.getActiveCorrectionBillId() == null) {
            return;
        }
        CorrectionBillDO correction = correctionBillMapper.selectById(state.getActiveCorrectionBillId());
        if (correction == null) {
            return;
        }
        correctionBillMapper.updateById(new CorrectionBillDO()
                .setId(correction.getId())
                .setNewProcessInstanceId(processInstanceId));
        log.info("[onSourceBillResubmitted] 纠错中单据已重新发起流程 billType={}, billId={}, pi={}",
                billType, billId, processInstanceId);
    }

    @Override
    public boolean shouldSyncFrozenBillProcessStatus(String billType, Long billId, String processInstanceId) {
        if (StrUtil.isBlank(processInstanceId)) {
            return false;
        }
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        if (state == null || state.getActiveCorrectionBillId() == null) {
            return false;
        }
        CorrectionBillDO correction = correctionBillMapper.selectById(state.getActiveCorrectionBillId());
        return correction != null
                && Objects.equals(correction.getNewProcessInstanceId(), processInstanceId);
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

}
