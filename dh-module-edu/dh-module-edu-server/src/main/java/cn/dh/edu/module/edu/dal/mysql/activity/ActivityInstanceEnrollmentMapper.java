package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceEnrollmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ActivityInstanceEnrollmentMapper extends BaseMapperX<ActivityInstanceEnrollmentDO> {

    default ActivityInstanceEnrollmentDO selectByInstanceIdAndUserId(Long instanceId, Long userId) {
        return selectOne(ActivityInstanceEnrollmentDO::getInstanceId, instanceId,
                ActivityInstanceEnrollmentDO::getUserId, userId);
    }

    default List<ActivityInstanceEnrollmentDO> selectListByInstanceId(Long instanceId) {
        return selectList(ActivityInstanceEnrollmentDO::getInstanceId, instanceId);
    }

    default Long selectCountByInstanceId(Long instanceId) {
        return selectCount(ActivityInstanceEnrollmentDO::getInstanceId, instanceId);
    }

}
