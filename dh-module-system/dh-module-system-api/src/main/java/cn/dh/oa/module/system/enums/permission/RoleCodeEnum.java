package cn.dh.oa.module.system.enums.permission;

import cn.dh.oa.framework.common.util.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色标识枚举
 */
@Getter
@AllArgsConstructor
public enum RoleCodeEnum {

    SUPER_ADMIN("super_admin", "超级管理员"),
    TENANT_ADMIN("tenant_admin", "租户管理员"),
    CRM_ADMIN("crm_admin", "CRM 管理员"), // CRM 系统专用
    OA_COMPREHENSIVE_ADMIN("oa_comprehensive_admin", "综合管理员"), // OA 审批管理可查看全部，不可代审
    ;

    /**
     * 角色编码
     */
    private final String code;
    /**
     * 名字
     */
    private final String name;

    public static boolean isSuperAdmin(String code) {
        return ObjectUtils.equalsAny(code, SUPER_ADMIN.getCode());
    }

    /** 审批管理可查看全部单据的角色（超管 / 综合管理员） */
    public static boolean isOaBillViewAll(String code) {
        return ObjectUtils.equalsAny(code, SUPER_ADMIN.getCode(), OA_COMPREHENSIVE_ADMIN.getCode());
    }

}
