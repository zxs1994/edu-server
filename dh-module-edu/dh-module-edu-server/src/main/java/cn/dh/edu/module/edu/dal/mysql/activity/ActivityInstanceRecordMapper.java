package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActivityInstanceRecordMapper extends BaseMapperX<ActivityInstanceRecordDO> {

    default ActivityInstanceRecordDO selectByInstanceId(Long instanceId) {
        return selectOne(ActivityInstanceRecordDO::getInstanceId, instanceId);
    }

}
