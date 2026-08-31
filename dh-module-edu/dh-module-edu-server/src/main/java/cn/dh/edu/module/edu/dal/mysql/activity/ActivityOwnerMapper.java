package cn.dh.edu.module.edu.dal.mysql.activity;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityOwnerDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 专项活动负责人 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface ActivityOwnerMapper extends BaseMapperX<ActivityOwnerDO> {

    default List<ActivityOwnerDO> selectListByActivityId(Long activityId) {
        return selectList(ActivityOwnerDO::getActivityId, activityId);
    }

    default void deleteByActivityId(Long activityId) {
        delete(ActivityOwnerDO::getActivityId, activityId);
    }

}
