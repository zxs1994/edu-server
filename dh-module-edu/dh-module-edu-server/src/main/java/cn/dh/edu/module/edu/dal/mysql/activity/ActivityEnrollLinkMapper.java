package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityEnrollLinkDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActivityEnrollLinkMapper extends BaseMapperX<ActivityEnrollLinkDO> {

    default ActivityEnrollLinkDO selectByCode(String code) {
        return selectOne(ActivityEnrollLinkDO::getCode, code);
    }

    default ActivityEnrollLinkDO selectByUserAndInstance(Long userId, Long instanceId) {
        return selectOne(new LambdaQueryWrapperX<ActivityEnrollLinkDO>()
                .eq(ActivityEnrollLinkDO::getUserId, userId)
                .eq(ActivityEnrollLinkDO::getInstanceId, instanceId)
                .orderByDesc(ActivityEnrollLinkDO::getId)
                .last("LIMIT 1"));
    }

}
