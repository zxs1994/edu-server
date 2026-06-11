package cn.dh.oa.module.oa.dal.mysql.contract;

import java.util.*;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractDetailDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 合同明细 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface ContractDetailMapper extends BaseMapperX<ContractDetailDO> {

    default List<ContractDetailDO> selectListByBillId(Long billId) {
        return selectList(new LambdaQueryWrapperX<ContractDetailDO>()
                .eq(ContractDetailDO::getBillId, billId)
                .orderByAsc(ContractDetailDO::getSortOrder));
    }

    default void deleteByBillId(Long billId) {
        delete(new LambdaQueryWrapperX<ContractDetailDO>()
                .eq(ContractDetailDO::getBillId, billId));
    }

}
