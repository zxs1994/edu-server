package cn.dh.oa.module.oa.service.expense;

import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseBillDO;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 日常报销单 Service 实现类
 * 继承 ExpenseReimburseBillServiceImpl，复用所有 CRUD 逻辑
 * 仅用于 FlowBillService 工厂注册，让 BPM 回调能正确路由到日常报销单更新 processStatus
 *
 * @author 鼎衡
 */
@Slf4j
@Service
@Validated
public class DailyExpenseReimburseBillServiceImpl extends ExpenseReimburseBillServiceImpl {

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_DAILY_EXPENSE_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新日常报销单流程状态，id: {}, status: {}", id, status);

        ExpenseReimburseBillDO updateObj = new ExpenseReimburseBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        expenseReimburseBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 日常报销单流程状态更新成功，id: {}, status: {}", id, status);
    }

}
