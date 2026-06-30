package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.oa.module.oa.api.correction.OaPresidentCorrectionApi;
import cn.dh.oa.module.oa.api.correction.dto.OaCorrectionRevokeNodeDTO;
import cn.dh.oa.module.oa.dal.dataobject.correction.BillCorrectionStateDO;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.module.oa.dal.mysql.correction.BillCorrectionStateMapper;
import cn.dh.oa.module.oa.dal.mysql.correction.CorrectionBillMapper;
import cn.dh.oa.module.oa.enums.correction.OaBillCorrectionStatusEnum;
import cn.dh.oa.module.oa.service.correction.freeze.BillFreezeHandlerRegistry;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
