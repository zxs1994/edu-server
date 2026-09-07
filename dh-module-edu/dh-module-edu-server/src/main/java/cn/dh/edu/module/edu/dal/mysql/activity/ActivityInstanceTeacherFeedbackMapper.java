package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceTeacherFeedbackDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActivityInstanceTeacherFeedbackMapper extends BaseMapperX<ActivityInstanceTeacherFeedbackDO> {

    default List<ActivityInstanceTeacherFeedbackDO> selectListByInstanceId(Long instanceId) {
        return selectList(ActivityInstanceTeacherFeedbackDO::getInstanceId, instanceId);
    }

    default ActivityInstanceTeacherFeedbackDO selectByInstanceIdAndUserId(Long instanceId, Long userId) {
        return selectOne(ActivityInstanceTeacherFeedbackDO::getInstanceId, instanceId,
                ActivityInstanceTeacherFeedbackDO::getUserId, userId);
    }

}
