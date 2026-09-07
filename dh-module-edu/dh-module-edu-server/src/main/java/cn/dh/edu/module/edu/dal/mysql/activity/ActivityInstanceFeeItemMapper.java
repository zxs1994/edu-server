package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceFeeItemPageReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceFeeItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ActivityInstanceFeeItemMapper extends BaseMapperX<ActivityInstanceFeeItemDO> {

    default PageResult<ActivityInstanceFeeItemDO> selectPage(ActivityInstanceFeeItemPageReqVO reqVO,
                                                             Collection<Long> activityIds,
                                                             Collection<Long> instanceIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ActivityInstanceFeeItemDO>()
                .likeIfPresent(ActivityInstanceFeeItemDO::getFeeCode, reqVO.getFeeCode())
                .eqIfPresent(ActivityInstanceFeeItemDO::getActivityId, reqVO.getActivityId())
                .eqIfPresent(ActivityInstanceFeeItemDO::getInstanceId, reqVO.getInstanceId())
                .eqIfPresent(ActivityInstanceFeeItemDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ActivityInstanceFeeItemDO::getFeeSide, reqVO.getFeeSide())
                .eqIfPresent(ActivityInstanceFeeItemDO::getFeeType, reqVO.getFeeType())
                .eqIfPresent(ActivityInstanceFeeItemDO::getCurrency, reqVO.getCurrency())
                .betweenIfPresent(ActivityInstanceFeeItemDO::getGenerateTime, reqVO.getGenerateTime())
                .inIfPresent(ActivityInstanceFeeItemDO::getActivityId, activityIds)
                .inIfPresent(ActivityInstanceFeeItemDO::getInstanceId, instanceIds)
                .orderByDesc(ActivityInstanceFeeItemDO::getId));
    }

    default List<ActivityInstanceFeeItemDO> selectListByInstanceId(Long instanceId) {
        return selectList(ActivityInstanceFeeItemDO::getInstanceId, instanceId);
    }

    default List<ActivityInstanceFeeItemDO> selectListByPaymentRequestId(Long paymentRequestId) {
        return selectList(ActivityInstanceFeeItemDO::getPaymentRequestId, paymentRequestId);
    }

    default List<ActivityInstanceFeeItemDO> selectListByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<ActivityInstanceFeeItemDO>()
                .in(ActivityInstanceFeeItemDO::getId, ids));
    }

    /**
     * 可选入付款申请的费用明细：
     * <ul>
     *   <li>待付款申请：未挂单，或挂在本单</li>
     *   <li>已驳回：仅挂在本单的可选（别的单不可选）</li>
     * </ul>
     */
    default List<ActivityInstanceFeeItemDO> selectSelectableForPayment(Collection<Long> instanceIds,
                                                                      Long currentRequestId) {
        if (instanceIds == null || instanceIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<ActivityInstanceFeeItemDO> pending = selectList(new LambdaQueryWrapperX<ActivityInstanceFeeItemDO>()
                .in(ActivityInstanceFeeItemDO::getInstanceId, instanceIds)
                .eq(ActivityInstanceFeeItemDO::getStatus, "pending_request")
                .and(w -> w.isNull(ActivityInstanceFeeItemDO::getPaymentRequestId)
                        .or()
                        .eq(currentRequestId != null, ActivityInstanceFeeItemDO::getPaymentRequestId, currentRequestId))
                .orderByAsc(ActivityInstanceFeeItemDO::getId));
        if (currentRequestId == null) {
            return pending;
        }
        List<ActivityInstanceFeeItemDO> rejectedOnThisBill = selectList(
                new LambdaQueryWrapperX<ActivityInstanceFeeItemDO>()
                        .in(ActivityInstanceFeeItemDO::getInstanceId, instanceIds)
                        .eq(ActivityInstanceFeeItemDO::getStatus, "rejected")
                        .eq(ActivityInstanceFeeItemDO::getPaymentRequestId, currentRequestId)
                        .orderByAsc(ActivityInstanceFeeItemDO::getId));
        if (rejectedOnThisBill.isEmpty()) {
            return pending;
        }
        java.util.LinkedHashMap<Long, ActivityInstanceFeeItemDO> merged = new java.util.LinkedHashMap<>();
        for (ActivityInstanceFeeItemDO item : pending) {
            merged.put(item.getId(), item);
        }
        for (ActivityInstanceFeeItemDO item : rejectedOnThisBill) {
            merged.putIfAbsent(item.getId(), item);
        }
        return new java.util.ArrayList<>(merged.values());
    }

    default Long selectCountByFeeCodePrefix(String prefix) {
        return selectCount(new LambdaQueryWrapperX<ActivityInstanceFeeItemDO>()
                .likeRight(ActivityInstanceFeeItemDO::getFeeCode, prefix));
    }

}
