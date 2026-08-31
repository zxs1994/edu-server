package cn.dh.edu.module.system.dal.mysql.permission;

import cn.dh.edu.framework.common.enums.CommonStatusEnum;
import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import cn.dh.edu.module.system.dal.dataobject.permission.MenuDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapperX<MenuDO> {

    default MenuDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(MenuDO::getParentId, parentId, MenuDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(MenuDO::getParentId, parentId);
    }

    default List<MenuDO> selectList(MenuListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<MenuDO>()
                .eq(MenuDO::getManaged, Boolean.TRUE) // 菜单管理列表不显示虚拟菜单
                .likeIfPresent(MenuDO::getName, reqVO.getName())
                .eqIfPresent(MenuDO::getStatus, reqVO.getStatus()));
    }

    default List<MenuDO> selectListByPermission(String permission) {
        return selectList(MenuDO::getPermission, permission);
    }

    default MenuDO selectByComponentName(String componentName) {
        return selectOne(MenuDO::getComponentName, componentName);
    }

    default List<MenuDO> selectAppCenterList() {
        // 查询「应用中心显示」且「启用」的菜单/目录，按 sort 排序
        return selectList(new LambdaQueryWrapperX<MenuDO>()
                .eq(MenuDO::getAppVisible, Boolean.TRUE)
                .eq(MenuDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .orderByAsc(MenuDO::getSort));
    }

}
