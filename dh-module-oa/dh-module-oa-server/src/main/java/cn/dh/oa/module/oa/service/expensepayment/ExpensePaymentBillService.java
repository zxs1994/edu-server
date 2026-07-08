package cn.dh.oa.module.oa.service.expensepayment;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.ExpensePaymentBillPageReqVO;
import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.ExpensePaymentBillRespVO;
import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.ExpensePaymentBillSaveReqVO;
import cn.dh.oa.module.oa.dal.dataobject.expensepayment.ExpensePaymentBillDO;
import jakarta.validation.Valid;

import java.util.List;

public interface ExpensePaymentBillService {

    Long saveExpensePaymentBill(@Valid ExpensePaymentBillSaveReqVO saveReqVO);

    Long submitExpensePaymentBill(@Valid ExpensePaymentBillSaveReqVO saveReqVO);

    void deleteExpensePaymentBill(Long id);

    void deleteExpensePaymentBillListByIds(List<Long> ids);

    ExpensePaymentBillDO getExpensePaymentBill(Long id);

    ExpensePaymentBillRespVO getExpensePaymentBillInfo(Long id);

    PageResult<ExpensePaymentBillDO> getExpensePaymentBillPage(ExpensePaymentBillPageReqVO pageReqVO);

}
