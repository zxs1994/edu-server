package cn.dh.oa.module.oa.dal.mysql.correction;

import cn.hutool.core.collection.CollUtil;
import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.oa.module.oa.dal.dataobject.correction.BillCorrectionStateDO;
import cn.dh.oa.module.oa.enums.correction.OaBillCorrectionStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface BillCorrectionStateMapper extends BaseMapperX<BillCorrectionStateDO> {

    default BillCorrectionStateDO selectByBill(String billType, Long billId) {
        return selectOne(new LambdaQueryWrapperX<BillCorrectionStateDO>()
                .eq(BillCorrectionStateDO::getBillType, billType)
                .eq(BillCorrectionStateDO::getBillId, billId));
    }

    default boolean existsActiveCorrection(String billType, Long billId) {
        BillCorrectionStateDO state = selectByBill(billType, billId);
        return state != null && OaBillCorrectionStatusEnum.isActive(state.getCorrectionStatus());
    }

    default List<BillCorrectionStateDO> selectListByBillIds(String billType, Collection<Long> billIds) {
        if (CollUtil.isEmpty(billIds)) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<BillCorrectionStateDO>()
                .eq(BillCorrectionStateDO::getBillType, billType)
                .in(BillCorrectionStateDO::getBillId, billIds));
    }

}
