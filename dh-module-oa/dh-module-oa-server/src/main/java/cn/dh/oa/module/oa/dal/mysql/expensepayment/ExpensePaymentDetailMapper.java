package cn.dh.oa.module.oa.dal.mysql.expensepayment;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.oa.dal.dataobject.expensepayment.ExpensePaymentDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExpensePaymentDetailMapper extends BaseMapperX<ExpensePaymentDetailDO> {

    default List<ExpensePaymentDetailDO> selectListByBillId(Long billId) {
        return selectList(new LambdaQueryWrapperX<ExpensePaymentDetailDO>()
                .eq(ExpensePaymentDetailDO::getBillId, billId)
                .orderByAsc(ExpensePaymentDetailDO::getSortOrder));
    }

    default void deleteByBillId(Long billId) {
        delete(new LambdaQueryWrapperX<ExpensePaymentDetailDO>()
                .eq(ExpensePaymentDetailDO::getBillId, billId));
    }

}
