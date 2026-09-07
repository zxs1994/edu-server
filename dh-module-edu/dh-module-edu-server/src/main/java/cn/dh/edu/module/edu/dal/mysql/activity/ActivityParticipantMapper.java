package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityParticipantDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActivityParticipantMapper extends BaseMapperX<ActivityParticipantDO> {

    default List<ActivityParticipantDO> selectListByActivityId(Long activityId) {
        return selectList(ActivityParticipantDO::getActivityId, activityId);
    }

    default void deleteByActivityId(Long activityId) {
        delete(ActivityParticipantDO::getActivityId, activityId);
    }

    default List<ActivityParticipantDO> selectListByUserId(Long userId) {
        return selectList(ActivityParticipantDO::getUserId, userId);
    }

}
