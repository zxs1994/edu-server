package cn.dh.oa.module.system.convert.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import cn.dh.oa.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cn.dh.oa.module.system.api.social.dto.SocialUserBindReqDTO;
import cn.dh.oa.module.system.controller.admin.auth.vo.*;
import cn.dh.oa.module.system.dal.dataobject.dept.DeptDO;
import cn.dh.oa.module.system.dal.dataobject.permission.MenuDO;
import cn.dh.oa.module.system.dal.dataobject.permission.RoleDO;
import cn.dh.oa.module.system.dal.dataobject.user.AdminUserDO;
import cn.dh.oa.module.system.enums.permission.MenuTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.LoggerFactory;

import java.util.*;

import static cn.dh.oa.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.dh.oa.framework.common.util.collection.CollectionUtils.filterList;
import static cn.dh.oa.module.system.dal.dataobject.permission.MenuDO.ID_ROOT;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    default AuthPermissionInfoRespVO convert(AdminUserDO user, List<RoleDO> roleList, List<MenuDO> menuList) {
        return convert(user, roleList, menuList, convertSet(menuList, MenuDO::getId), null, null);
    }

    default AuthPermissionInfoRespVO convert(AdminUserDO user, List<RoleDO> roleList, List<MenuDO> menuList, DeptDO company) {
        // 未提供 assignedMenuIds 时，默认所有菜单都是显式分配的（无 phantom）
        return convert(user, roleList, menuList, convertSet(menuList, MenuDO::getId), company, null);
    }

    default AuthPermissionInfoRespVO convert(AdminUserDO user, List<RoleDO> roleList, List<MenuDO> menuList, DeptDO company, DeptDO dept) {
        return convert(user, roleList, menuList, convertSet(menuList, MenuDO::getId), company, dept);
    }

    default AuthPermissionInfoRespVO convert(AdminUserDO user, List<RoleDO> roleList, List<MenuDO> menuList,
                                              Set<Long> assignedMenuIds, DeptDO company, DeptDO dept) {
        AuthPermissionInfoRespVO.UserVO userVO = BeanUtils.toBean(user, AuthPermissionInfoRespVO.UserVO.class);
        // 设置公司信息
        if (company != null) {
            userVO.setCompanyId(company.getId());
            userVO.setCompanyName(company.getName());
        }
        // 设置部门信息
        if (dept != null) {
            userVO.setDeptName(dept.getName());
        }

        return AuthPermissionInfoRespVO.builder()
                .user(userVO)
                .roles(convertSet(roleList, RoleDO::getCode))
                // 权限标识信息
                .permissions(convertSet(menuList, MenuDO::getPermission))
                // 菜单树
                .menus(buildMenuTree(menuList, assignedMenuIds))
                .build();
    }


    AuthPermissionInfoRespVO.MenuVO convertTreeNode(MenuDO menu);

    /**
     * 将菜单列表，构建成菜单树（兼容旧调用，所有菜单视为显式分配）
     */
    default List<AuthPermissionInfoRespVO.MenuVO> buildMenuTree(List<MenuDO> menuList) {
        return buildMenuTree(menuList, null);
    }

    /**
     * 将菜单列表，构建成菜单树
     *
     * @param menuList         菜单列表（含自动补全的 phantom 父级）
     * @param assignedMenuIds  角色显式分配的菜单 ID 集合。为 null 时不做 phantom 标记处理。
     * @return 菜单树
     */
    default List<AuthPermissionInfoRespVO.MenuVO> buildMenuTree(List<MenuDO> menuList, Set<Long> assignedMenuIds) {
        if (CollUtil.isEmpty(menuList)) {
            return Collections.emptyList();
        }
        // 移除按钮
        menuList.removeIf(menu -> menu.getType().equals(MenuTypeEnum.BUTTON.getType()));
        // 排序，保证菜单的有序性
        menuList.sort(Comparator.comparing(MenuDO::getSort));

        // 构建菜单树
        // 使用 LinkedHashMap 的原因，是为了排序 。实际也可以用 Stream API ，就是太丑了。
        Map<Long, AuthPermissionInfoRespVO.MenuVO> treeNodeMap = new LinkedHashMap<>();
        menuList.forEach(menu -> treeNodeMap.put(menu.getId(),
                BeanUtils.toBean(menu, AuthPermissionInfoRespVO.MenuVO.class)));

        // 标记 phantom 节点及其所有后代为不可见（侧边栏不显示，但路由仍正常注册）
        // phantom 节点 = 自动补全的父级目录，不在角色显式分配的菜单中
        if (assignedMenuIds != null) {
            treeNodeMap.values().forEach(node -> {
                if (!assignedMenuIds.contains(node.getId())) {
                    node.setVisible(false);
                    hideAllDescendants(node, treeNodeMap);
                }
            });
        }

        // 处理父子关系
        treeNodeMap.values().stream().filter(node -> ObjUtil.notEqual(node.getParentId(), ID_ROOT)).forEach(childNode -> {
            // 获得父节点
            AuthPermissionInfoRespVO.MenuVO parentNode = treeNodeMap.get(childNode.getParentId());
            if (parentNode == null) {
                LoggerFactory.getLogger(getClass()).error("[buildRouterTree][resource({}) 找不到父资源({})]",
                        childNode.getId(), childNode.getParentId());
                return;
            }
            // 将自己添加到父节点中
            if (parentNode.getChildren() == null) {
                parentNode.setChildren(new ArrayList<>());
            }
            parentNode.getChildren().add(childNode);
        });
        // 获得到所有的根节点
        return filterList(treeNodeMap.values(), node -> ID_ROOT.equals(node.getParentId()));
    }

    /**
     * 递归隐藏 phantom 节点的所有后代（已在子节点列表中或稍后通过父子关系挂载）
     */
    default void hideAllDescendants(AuthPermissionInfoRespVO.MenuVO phantomNode,
                                     Map<Long, AuthPermissionInfoRespVO.MenuVO> treeNodeMap) {
        // 通过遍历所有节点找到直接子节点（此时 children 尚未填充）
        treeNodeMap.values().forEach(node -> {
            if (ObjUtil.equal(node.getParentId(), phantomNode.getId())) {
                node.setVisible(false);
                hideAllDescendants(node, treeNodeMap);
            }
        });
    }

    SocialUserBindReqDTO convert(Long userId, Integer userType, AuthSocialLoginReqVO reqVO);

    SmsCodeSendReqDTO convert(AuthSmsSendReqVO reqVO);

    SmsCodeUseReqDTO convert(AuthSmsLoginReqVO reqVO, Integer scene, String usedIp);

}
