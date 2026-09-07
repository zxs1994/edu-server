package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstancePageReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 专项活动执行实例 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface ActivityInstanceMapper extends BaseMapperX<ActivityInstanceDO> {

    default PageResult<ActivityInstanceDO> selectPageByReq(ActivityInstancePageReqVO reqVO,
                                                           Collection<Long> filterActivityIds) {
        return selectPageByReq(reqVO, filterActivityIds, null, false);
    }

    /**
     * @param keywordActivityIds keyword 模式下按活动名匹配到的活动 ID；可为空
     * @param keywordMode        是否启用 keyword（实例编号 OR 活动名）
     */
    default PageResult<ActivityInstanceDO> selectPageByReq(ActivityInstancePageReqVO reqVO,
                                                           Collection<Long> filterActivityIds,
                                                           Collection<Long> keywordActivityIds,
                                                           boolean keywordMode) {
        LambdaQueryWrapperX<ActivityInstanceDO> wrapper = new LambdaQueryWrapperX<ActivityInstanceDO>()
                .likeIfPresent(ActivityInstanceDO::getInstanceCode, reqVO.getInstanceCode())
                .eqIfPresent(ActivityInstanceDO::getActivityId, reqVO.getActivityId())
                .eqIfPresent(ActivityInstanceDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ActivityInstanceDO::getPlannedDate, reqVO.getPlannedDate())
                .orderByDesc(ActivityInstanceDO::getId);
        if (filterActivityIds != null) {
            wrapper.in(ActivityInstanceDO::getActivityId, filterActivityIds);
        }
        if (keywordMode) {
            String keyword = reqVO.getKeyword() != null ? reqVO.getKeyword().trim() : "";
            wrapper.and(w -> {
                w.like(ActivityInstanceDO::getInstanceCode, keyword);
                if (keywordActivityIds != null && !keywordActivityIds.isEmpty()) {
                    w.or().in(ActivityInstanceDO::getActivityId, keywordActivityIds);
                }
            });
        }
        return selectPage(reqVO, wrapper);
    }

    default List<ActivityInstanceDO> selectListByActivityId(Long activityId) {
        return selectList(ActivityInstanceDO::getActivityId, activityId);
    }

    default List<Long> selectIdListByInstanceCodeLike(String instanceCode) {
        return selectList(new LambdaQueryWrapperX<ActivityInstanceDO>()
                .like(ActivityInstanceDO::getInstanceCode, instanceCode)
                .select(ActivityInstanceDO::getId))
                .stream()
                .map(ActivityInstanceDO::getId)
                .toList();
    }

    default Long selectCountByActivityId(Long activityId) {
        return selectCount(ActivityInstanceDO::getActivityId, activityId);
    }

    /**
     * 查询活动下最大期次编号（无实例时返回 0）
     */
    default int selectMaxPeriodNoByActivityId(Long activityId) {
        ActivityInstanceDO latest = selectOne(new LambdaQueryWrapperX<ActivityInstanceDO>()
                .eq(ActivityInstanceDO::getActivityId, activityId)
                .orderByDesc(ActivityInstanceDO::getPeriodNo)
                .last("LIMIT 1"));
        if (latest == null || latest.getPeriodNo() == null) {
            return 0;
        }
        return latest.getPeriodNo();
    }

}
