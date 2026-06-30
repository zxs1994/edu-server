package cn.dh.oa.module.oa.service.correction.freeze;

import cn.dh.oa.module.oa.dal.dataobject.seal.SealApplyBillDO;
import cn.dh.oa.module.oa.dal.mysql.seal.SealApplyBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum.REJECT;

@Slf4j
@Component
public class SealBillFreezeHandler implements BillFreezeHandler {

    @Resource
    private SealApplyBillMapper sealApplyBillMapper;

    @Override
    public String getBillType() {
        return OaBillTypeEnum.OA_SEAL_APPLY_BILL.getProcessDefinitionKey();
    }

    @Override
    public void freeze(Long billId) {
        log.info("[SealBillFreezeHandler] 冻结用印 billId={}", billId);
    }

    @Override
    public void unfreeze(Long billId) {
        log.info("[SealBillFreezeHandler] 解冻用印 billId={}", billId);
    }

    @Override
    public void applyCouncilOverride(Long billId, String correctionResult) {
        sealApplyBillMapper.updateById(new SealApplyBillDO().setId(billId).setProcessStatus(REJECT.getStatus()));
        log.info("[SealBillFreezeHandler] 理事会决议推翻用印 billId={}", billId);
    }

}
