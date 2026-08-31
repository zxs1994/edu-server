package cn.dh.edu.module.system.dal.mysql.home;

import cn.dh.edu.framework.mybatis.core.mapper.BaseMapperX;
import cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.dh.edu.module.system.dal.dataobject.home.HomeAppUserDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户级应用配置 Mapper
 *
 * @author 鼎衡
 */
@Mapper
public interface HomeAppUserMapper extends BaseMapperX<HomeAppUserDO> {

    /**
     * 查询用户的应用配置列表
     *
     * @param userId 用户ID
     * @return 应用配置列表
     */
    default List<HomeAppUserDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<HomeAppUserDO>()
                .eq(HomeAppUserDO::getUserId, userId)
                .orderByAsc(HomeAppUserDO::getSort)
                .orderByDesc(HomeAppUserDO::getId));
    }

    /**
     * 查询用户的应用配置列表（仅显示状态）
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 应用配置列表
     */
    default List<HomeAppUserDO> selectListByUserIdAndStatus(Long userId, Integer status) {
        return selectList(new LambdaQueryWrapperX<HomeAppUserDO>()
                .eq(HomeAppUserDO::getUserId, userId)
                .eq(HomeAppUserDO::getStatus, status)
                .orderByAsc(HomeAppUserDO::getSort)
                .orderByDesc(HomeAppUserDO::getId));
    }

    /**
     * 查询用户的某个菜单应用配置
     *
     * @param userId 用户ID
     * @param menuId 菜单ID
     * @return 应用配置
     */
    default HomeAppUserDO selectByUserIdAndMenuId(Long userId, Long menuId) {
        return selectOne(new LambdaQueryWrapperX<HomeAppUserDO>()
                .eq(HomeAppUserDO::getUserId, userId)
                .eq(HomeAppUserDO::getMenuId, menuId));
    }

    /**
     * 删除用户的所有应用配置
     *
     * @param userId 用户ID
     * @return 删除数量
     */
    default int deleteByUserId(Long userId) {
        return delete(new LambdaQueryWrapperX<HomeAppUserDO>()
                .eq(HomeAppUserDO::getUserId, userId));
    }

    // ========== 物理删除方法（绕过 MyBatis-Plus 逻辑删除，真删） ==========
    // 应用中心场景：用户移除应用就是真删，无需保留软删除历史。
    // 表 system_home_app_user 有唯一键 uk_user_menu(user_id, menu_id, deleted)，
    // 若使用软删除，同一 (user_id, menu_id) 反复添加/移除会累积 deleted=1 残留，
    // 再次移除有效记录时 deleted 0→1 撞唯一键导致 DuplicateKeyException。
    // 故应用中心统一改用物理删除，从根上杜绝冲突。

    /**
     * 根据 ID 物理删除（绕过逻辑删除）
     *
     * @param id 主键
     * @return 影响行数
     */
    @Delete("DELETE FROM system_home_app_user WHERE id = #{id}")
    int physicalDeleteById(@Param("id") Long id);

    /**
     * 根据 用户ID 物理删除该用户所有应用配置（绕过逻辑删除）
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM system_home_app_user WHERE user_id = #{userId}")
    int physicalDeleteByUserId(@Param("userId") Long userId);

    /**
     * 根据 用户ID + 菜单ID 物理删除（绕过逻辑删除），用于清理历史残留
     *
     * @param userId 用户ID
     * @param menuId 菜单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM system_home_app_user WHERE user_id = #{userId} AND menu_id = #{menuId}")
    int physicalDeleteByUserIdAndMenuId(@Param("userId") Long userId, @Param("menuId") Long menuId);

    /**
     * 查询用户某菜单是否存在历史软删除残留（不过滤 deleted，含已删除记录）
     * 用于 createUserApp 时判断是否需要清理
     *
     * @param userId 用户ID
     * @param menuId 菜单ID
     * @return 残留记录数
     */
    @Select("SELECT COUNT(*) FROM system_home_app_user WHERE user_id = #{userId} AND menu_id = #{menuId} AND deleted = 1")
    int countDeletedByUserIdAndMenuId(@Param("userId") Long userId, @Param("menuId") Long menuId);

}

