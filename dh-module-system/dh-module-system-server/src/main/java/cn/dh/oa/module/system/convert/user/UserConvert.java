package cn.dh.oa.module.system.convert.user;

import cn.dh.oa.framework.common.util.collection.CollectionUtils;
import cn.dh.oa.framework.common.util.collection.MapUtils;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import cn.dh.oa.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import cn.dh.oa.module.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import cn.dh.oa.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import cn.dh.oa.module.system.controller.admin.user.vo.user.UserRespVO;
import cn.dh.oa.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import cn.dh.oa.module.system.dal.dataobject.dept.DeptDO;
import cn.dh.oa.module.system.dal.dataobject.dept.PostDO;
import cn.dh.oa.module.system.dal.dataobject.permission.RoleDO;
import cn.dh.oa.module.system.dal.dataobject.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    default List<UserRespVO> convertList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap,
                                         Map<Long, Set<Long>> userRoleMap) {
        return CollectionUtils.convertList(list, user -> convert(user, deptMap.get(user.getDeptId()),
                userRoleMap != null ? userRoleMap.getOrDefault(user.getId(), Collections.emptySet()) : Collections.emptySet()));
    }

    default List<UserRespVO> convertList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return convertList(list, deptMap, null);
    }

    default UserRespVO convert(AdminUserDO user, DeptDO dept, Set<Long> roleIds) {
        UserRespVO userVO = BeanUtils.toBean(user, UserRespVO.class);
        if (dept != null) {
            userVO.setDeptName(dept.getName());
        }
        userVO.setRoleIds(roleIds);
        return userVO;
    }

    default UserRespVO convert(AdminUserDO user, DeptDO dept) {
        return convert(user, dept, Collections.emptySet());
    }

    default List<UserSimpleRespVO> convertSimpleList(List<AdminUserDO> list, Map<Long, DeptDO> deptMap) {
        return CollectionUtils.convertList(list, user -> {
            UserSimpleRespVO userVO = BeanUtils.toBean(user, UserSimpleRespVO.class);
            MapUtils.findAndThen(deptMap, user.getDeptId(), dept -> userVO.setDeptName(dept.getName()));
            return userVO;
        });
    }

    default UserProfileRespVO convert(AdminUserDO user, List<RoleDO> userRoles,
                                      DeptDO dept, List<PostDO> posts) {
        UserProfileRespVO userVO = BeanUtils.toBean(user, UserProfileRespVO.class);
        userVO.setRoles(BeanUtils.toBean(userRoles, RoleSimpleRespVO.class));
        userVO.setDept(BeanUtils.toBean(dept, DeptSimpleRespVO.class));
        userVO.setPosts(BeanUtils.toBean(posts, PostSimpleRespVO.class));
        return userVO;
    }

}
