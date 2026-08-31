package cn.dh.edu.module.system.dal.dataobject.home;

import cn.dh.edu.framework.common.enums.CommonStatusEnum;
import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户级应用配置表 DO
 *
 * @author 鼎衡
 */
@TableName("system_home_app_user")
@KeySequence("system_home_app_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeAppUserDO extends TenantBaseDO {

    /**
     * 用户应用ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 关联菜单ID
     */
    private Long menuId;

    /**
     * 自定义应用名称
     */
    private String name;

    /**
     * 自定义图标
     */
    private String icon;

    /**
     * 自定义图标颜色
     */
    private String color;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}

