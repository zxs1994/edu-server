package cn.dh.oa.module.oa.dal.mysql.expense;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseBillDO;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleScope;
import org.apache.ibatis.annotations.Mapper;
import cn.dh.oa.module.oa.controller.admin.expense.vo.*;

/**
 * 差旅报销单 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface ExpenseReimburseBillMapper extends BaseMapperX<ExpenseReimburseBillDO> {

default PageResult<ExpenseReimburseBillDO> selectPageByVisibleScope(ExpenseReimburseBillPageReqVO reqVO,
                                                        OaBillApprovalVisibleScope visibleScope) {
        LambdaQueryWrapperX<ExpenseReimburseBillDO> wrapper = new LambdaQueryWrapperX<ExpenseReimburseBillDO>()
                .eqIfPresent(ExpenseReimburseBillDO::getBillType, reqVO.getBillType())
                .likeIfPresent(ExpenseReimburseBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(ExpenseReimburseBillDO::getProcessStatus, reqVO.getProcessStatus())
                .likeIfPresent(ExpenseReimburseBillDO::getDeptName, reqVO.getDeptName())
                .eqIfPresent(ExpenseReimburseBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(ExpenseReimburseBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ExpenseReimburseBillDO::getId);
        if (visibleScope != null) {
            visibleScope.apply(wrapper, ExpenseReimburseBillDO::getCreator, ExpenseReimburseBillDO::getProcessInstanceId);
        }
        return selectPage(reqVO, wrapper);
    }

}
