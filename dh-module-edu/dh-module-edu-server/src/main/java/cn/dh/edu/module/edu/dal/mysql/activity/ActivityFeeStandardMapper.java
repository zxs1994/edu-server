package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityFeeStandardDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 专项活动费用标准 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface ActivityFeeStandardMapper extends BaseMapperX<ActivityFeeStandardDO> {

    default List<ActivityFeeStandardDO> selectListByActivityId(Long activityId) {
        return selectList(ActivityFeeStandardDO::getActivityId, activityId);
    }

    default void deleteByActivityId(Long activityId) {
        delete(ActivityFeeStandardDO::getActivityId, activityId);
    }

}
