package cn.dh.oa.module.system.dal.mysql.home;

import cn.dh.oa.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.oa.module.system.dal.dataobject.home.UserHomePageDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户首页关联 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface UserHomePageMapper extends BaseMapperX<UserHomePageDO> {

    default UserHomePageDO selectByUserId(Long userId) {
        return selectOne(UserHomePageDO::getUserId, userId);
    }

}
