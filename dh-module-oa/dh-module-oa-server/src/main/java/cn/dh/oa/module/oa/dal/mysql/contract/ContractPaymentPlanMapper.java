package cn.dh.oa.module.oa.dal.mysql.contract;

import java.util.*;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractPaymentPlanDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收付款计划 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface ContractPaymentPlanMapper extends BaseMapperX<ContractPaymentPlanDO> {

    default List<ContractPaymentPlanDO> selectListByBillId(Long billId) {
        return selectList(new LambdaQueryWrapperX<ContractPaymentPlanDO>()
                .eq(ContractPaymentPlanDO::getBillId, billId)
                .orderByAsc(ContractPaymentPlanDO::getSortOrder));
    }

    default void deleteByBillId(Long billId) {
        delete(new LambdaQueryWrapperX<ContractPaymentPlanDO>()
                .eq(ContractPaymentPlanDO::getBillId, billId));
    }

}
