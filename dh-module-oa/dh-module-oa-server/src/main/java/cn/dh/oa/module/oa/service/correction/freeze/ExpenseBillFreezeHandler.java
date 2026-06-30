package cn.dh.oa.module.oa.service.correction.freeze;

import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseBillDO;
import cn.dh.oa.module.oa.dal.mysql.expense.ExpenseReimburseBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum.REJECT;

/**
 * 费用报销单冻结：阻断付款
 */
@Slf4j
@Component
public class ExpenseBillFreezeHandler implements BillFreezeHandler {

    @Resource
    private ExpenseReimburseBillMapper expenseReimburseBillMapper;

    @Override
    public String getBillType() {
        return OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getProcessDefinitionKey();
    }

    @Override
    public void freeze(Long billId) {
        expenseReimburseBillMapper.updateById(new ExpenseReimburseBillDO().setId(billId).setPaymentStatus(0));
        log.info("[ExpenseBillFreezeHandler] 冻结报销付款 billId={}", billId);
    }

    @Override
    public void unfreeze(Long billId) {
        log.info("[ExpenseBillFreezeHandler] 解冻报销 billId={}", billId);
    }

    @Override
    public void applyCouncilOverride(Long billId, String correctionResult) {
        expenseReimburseBillMapper.updateById(new ExpenseReimburseBillDO()
                .setId(billId)
                .setProcessStatus(REJECT.getStatus())
                .setPaymentStatus(0));
        log.info("[ExpenseBillFreezeHandler] 理事会决议推翻报销 billId={}", billId);
    }

}
