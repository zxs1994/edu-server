package cn.dh.oa.module.oa.service.expense;

import java.util.*;
import jakarta.validation.*;
import cn.dh.oa.module.oa.controller.admin.expense.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;

/**
 * 费用报销单 Service 接口
 *
 * @author 鼎衡
 */
public interface ExpenseReimburseBillService {

    /**
     * 保存费用报销单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveExpenseReimburseBill(@Valid ExpenseReimburseBillSaveReqVO saveReqVO);

    /**
     * 提交费用报销单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitExpenseReimburseBill(@Valid ExpenseReimburseBillSaveReqVO saveReqVO);

    /**
     * 创建费用报销单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createExpenseReimburseBill(@Valid ExpenseReimburseBillSaveReqVO createReqVO);

    /**
     * 更新费用报销单
     *
     * @param updateReqVO 更新信息
     */
    void updateExpenseReimburseBill(@Valid ExpenseReimburseBillSaveReqVO updateReqVO);

    /**
     * 删除费用报销单
     *
     * @param id 编号
     */
    void deleteExpenseReimburseBill(Long id);

    /**
     * 批量删除费用报销单
     *
     * @param ids 编号列表
     */
    void deleteExpenseReimburseBillListByIds(List<Long> ids);

    /**
     * 获得费用报销单
     *
     * @param id 编号
     * @return 费用报销单
     */
    ExpenseReimburseBillDO getExpenseReimburseBill(Long id);

    /**
     * 获得费用报销单（包含附件信息）
     *
     * @param id 编号
     * @return 费用报销单响应VO
     */
    ExpenseReimburseBillRespVO getExpenseReimburseBillInfo(Long id);

    /**
     * 获得费用报销单分页
     *
     * @param pageReqVO 分页查询
     * @return 费用报销单分页
     */
    PageResult<ExpenseReimburseBillDO> getExpenseReimburseBillPage(ExpenseReimburseBillPageReqVO pageReqVO);

}
