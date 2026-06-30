package cn.dh.oa.module.oa.service.correction.freeze;

import cn.dh.oa.module.oa.dal.dataobject.contract.ContractBillDO;
import cn.dh.oa.module.oa.dal.mysql.contract.ContractBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum.REJECT;

/**
 * 合同审批单冻结：暂停履约/付款相关执行（标记流程状态辅助拦截）
 */
@Slf4j
@Component
public class ContractBillFreezeHandler implements BillFreezeHandler {

    @Resource
    private ContractBillMapper contractBillMapper;

    @Override
    public String getBillType() {
        return OaBillTypeEnum.OA_CONTRACT_BILL.getProcessDefinitionKey();
    }

    @Override
    public void freeze(Long billId) {
        log.info("[ContractBillFreezeHandler] 冻结合同 billId={}", billId);
    }

    @Override
    public void unfreeze(Long billId) {
        log.info("[ContractBillFreezeHandler] 解冻合同 billId={}", billId);
    }

    @Override
    public void applyCouncilOverride(Long billId, String correctionResult) {
        contractBillMapper.updateById(new ContractBillDO().setId(billId).setProcessStatus(REJECT.getStatus()));
        log.info("[ContractBillFreezeHandler] 理事会决议推翻合同审批 billId={}, result={}", billId, correctionResult);
    }

}
