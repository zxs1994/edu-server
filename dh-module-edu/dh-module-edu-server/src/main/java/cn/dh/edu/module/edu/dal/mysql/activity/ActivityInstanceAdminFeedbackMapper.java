package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceAdminFeedbackDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActivityInstanceAdminFeedbackMapper extends BaseMapperX<ActivityInstanceAdminFeedbackDO> {

    default ActivityInstanceAdminFeedbackDO selectByInstanceId(Long instanceId) {
        return selectOne(ActivityInstanceAdminFeedbackDO::getInstanceId, instanceId);
    }

}
