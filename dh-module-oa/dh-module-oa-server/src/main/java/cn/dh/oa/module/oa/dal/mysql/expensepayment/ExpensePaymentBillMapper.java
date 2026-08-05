package cn.dh.oa.module.oa.dal.mysql.expensepayment;

import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.ExpensePaymentBillPageReqVO;
import cn.dh.oa.module.oa.dal.dataobject.expensepayment.ExpensePaymentBillDO;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleScope;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpensePaymentBillMapper extends BaseMapperX<ExpensePaymentBillDO> {

default PageResult<ExpensePaymentBillDO> selectPageByVisibleScope(ExpensePaymentBillPageReqVO reqVO,
                                                        OaBillApprovalVisibleScope visibleScope) {
        LambdaQueryWrapperX<ExpensePaymentBillDO> wrapper = new LambdaQueryWrapperX<ExpensePaymentBillDO>()
                .likeIfPresent(ExpensePaymentBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(ExpensePaymentBillDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(ExpensePaymentBillDO::getPaymentType, reqVO.getPaymentType())
                .likeIfPresent(ExpensePaymentBillDO::getDeptName, reqVO.getDeptName())
                .eqIfPresent(ExpensePaymentBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(ExpensePaymentBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ExpensePaymentBillDO::getId);
        if (visibleScope != null) {
            visibleScope.apply(wrapper, ExpensePaymentBillDO::getCreator, ExpensePaymentBillDO::getProcessInstanceId);
        }
        return selectPage(reqVO, wrapper);
    }

}
