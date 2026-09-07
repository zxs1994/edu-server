package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceStudentFeedbackDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ActivityInstanceStudentFeedbackMapper extends BaseMapperX<ActivityInstanceStudentFeedbackDO> {

    default List<ActivityInstanceStudentFeedbackDO> selectListByInstanceId(Long instanceId) {
        return selectList(ActivityInstanceStudentFeedbackDO::getInstanceId, instanceId);
    }

    default List<ActivityInstanceStudentFeedbackDO> selectListByInstanceIds(Collection<Long> instanceIds) {
        return selectList(ActivityInstanceStudentFeedbackDO::getInstanceId, instanceIds);
    }

    default ActivityInstanceStudentFeedbackDO selectByInstanceIdAndUserId(Long instanceId, Long userId) {
        return selectOne(ActivityInstanceStudentFeedbackDO::getInstanceId, instanceId,
                ActivityInstanceStudentFeedbackDO::getUserId, userId);
    }

}
