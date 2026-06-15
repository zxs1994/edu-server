package cn.dh.oa.module.oa.dal.mysql.expense;

import java.util.*;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseDetailDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 差旅报销明细 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface ExpenseReimburseDetailMapper extends BaseMapperX<ExpenseReimburseDetailDO> {

    default List<ExpenseReimburseDetailDO> selectListByBillId(Long billId) {
        return selectList(new LambdaQueryWrapperX<ExpenseReimburseDetailDO>()
                .eq(ExpenseReimburseDetailDO::getBillId, billId)
                .orderByAsc(ExpenseReimburseDetailDO::getSortOrder));
    }

    default void deleteByBillId(Long billId) {
        delete(new LambdaQueryWrapperX<ExpenseReimburseDetailDO>()
                .eq(ExpenseReimburseDetailDO::getBillId, billId));
    }

}
