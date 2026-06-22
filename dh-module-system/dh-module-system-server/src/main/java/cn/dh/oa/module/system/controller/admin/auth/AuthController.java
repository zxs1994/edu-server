package cn.dh.oa.module.system.controller.admin.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.dh.oa.framework.common.enums.CommonStatusEnum;
import cn.dh.oa.framework.common.enums.UserTypeEnum;
import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.framework.datapermission.core.annotation.DataPermission;
import cn.dh.oa.framework.security.config.SecurityProperties;
import cn.dh.oa.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.oa.module.system.controller.admin.auth.vo.*;
import cn.dh.oa.module.system.convert.auth.AuthConvert;
import cn.dh.oa.module.system.dal.dataobject.dept.DeptDO;
import cn.dh.oa.module.system.dal.dataobject.permission.MenuDO;
import cn.dh.oa.module.system.dal.dataobject.permission.RoleDO;
import cn.dh.oa.module.system.dal.dataobject.user.AdminUserDO;
import cn.dh.oa.module.system.enums.logger.LoginLogTypeEnum;
import cn.dh.oa.module.system.service.auth.AdminAuthService;
import cn.dh.oa.module.system.service.dept.DeptService;
import cn.dh.oa.module.system.service.permission.MenuService;
import cn.dh.oa.module.system.service.permission.PermissionService;
import cn.dh.oa.module.system.service.permission.RoleService;
import cn.dh.oa.module.system.service.social.SocialClientService;
import cn.dh.oa.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.dh.oa.framework.common.pojo.CommonResult.success;
import static cn.dh.oa.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.dh.oa.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
public class AuthController {

    @Resource
    private AdminAuthService authService;
    @Resource
    private AdminUserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private SocialClientService socialClientService;
    @Resource
    private DeptService deptService;

    @Resource
    private SecurityProperties securityProperties;

    @PostMapping("/login")
    @PermitAll
    @Operation(summary = "使用账号密码登录")
    public CommonResult<AuthLoginRespVO> login(@RequestBody @Valid AuthLoginReqVO reqVO) {
        return success(authService.login(reqVO));
    }

    @PostMapping("/logout")
    @PermitAll
    @Operation(summary = "登出系统")
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotBlank(token)) {
            authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
        return success(true);
    }

    @PostMapping("/refresh-token")
    @PermitAll
    @Operation(summary = "刷新令牌")
    @Parameter(name = "refreshToken", description = "刷新令牌", required = true)
    public CommonResult<AuthLoginRespVO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return success(authService.refreshToken(refreshToken));
    }

    @GetMapping("/get-permission-info")
    @Operation(summary = "获取登录用户的权限信息")
    @DataPermission(enable = false) // 忽略数据权限，避免因为过滤，导致无法查询用户。类似：https://ruoyioffice.com/LHnrp
    public CommonResult<AuthPermissionInfoRespVO> getPermissionInfo() {
        // 1.1 获得用户信息
        AdminUserDO user = userService.getUser(getLoginUserId());
        if (user == null) {
            return success(null);
        }

        // 1.2 获得角色列表
        Set<Long> roleIds = permissionService.getUserRoleIdListByUserId(getLoginUserId());
        if (CollUtil.isEmpty(roleIds)) {
            // 获取用户公司信息和部门信息
            DeptDO company = deptService.getUserCompany(user.getDeptId());
            DeptDO dept = deptService.getDept(user.getDeptId());
            return success(AuthConvert.INSTANCE.convert(user, Collections.emptyList(), Collections.emptyList(), Collections.emptySet(), company, dept));
        }
        List<RoleDO> roles = roleService.getRoleList(roleIds);
        roles.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())); // 移除禁用的角色

        // 1.3 获得菜单列表
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(convertSet(roles, RoleDO::getId));
        List<MenuDO> menuList = menuService.getMenuList(menuIds);

        // 1.3.1 自动补全缺失的父级菜单（结构节点）
        // 必须在 filterDisableMenus 之前执行！因为 filterDisableMenus 会检查父链完整性，
        // 缺失父级的子菜单会被误判为"禁用"而被移除。
        // 场景：角色分配了子菜单但未分配父目录（如 OA 页面已分配但审批管理目录未分配），
        // 此时从 DB 加载缺失的父目录，确保菜单树结构完整、路由可正常注册。
        // 父目录自身的 visible 属性保持不变，管理员角色因直接拥有该菜单而可见，
        // 普通用户角色因未拥有该菜单而在侧边栏中不显示（但路由通过子菜单仍可用）。
        for (int i = 0; i < 5; i++) {
            Set<Long> existingIds = convertSet(menuList, MenuDO::getId);
            Set<Long> allParentIds = new HashSet<>();
            menuList.forEach(m -> {
                if (m.getParentId() != null && m.getParentId() != MenuDO.ID_ROOT) {
                    allParentIds.add(m.getParentId());
                }
            });
            allParentIds.removeAll(existingIds);
            if (allParentIds.isEmpty()) {
                break;
            }
            menuList.addAll(menuService.getMenuList(allParentIds));
        }

        // 1.3.2 过滤禁用的菜单（含祖先链检查，需要父级已补全才能正确判断）
        menuList = menuService.filterDisableMenus(menuList);

        // 1.4 获取用户公司信息和部门信息
        DeptDO company = deptService.getUserCompany(user.getDeptId());
        DeptDO dept = deptService.getDept(user.getDeptId());

        // 2. 拼接结果返回
        return success(AuthConvert.INSTANCE.convert(user, roles, menuList, menuIds, company, dept));
    }

    @PostMapping("/register")
    @PermitAll
    @Operation(summary = "注册用户")
    public CommonResult<AuthLoginRespVO> register(@RequestBody @Valid AuthRegisterReqVO registerReqVO) {
        return success(authService.register(registerReqVO));
    }

    // ========== 短信登录相关 ==========

    @PostMapping("/sms-login")
    @PermitAll
    @Operation(summary = "使用短信验证码登录")
    // 可按需开启限流：https://gitcode.com/zhouzhongyan/ruoyi-office.git/issues/851
    // @RateLimiter(time = 60, count = 6, keyResolver = ExpressionRateLimiterKeyResolver.class, keyArg = "#reqVO.mobile")
    public CommonResult<AuthLoginRespVO> smsLogin(@RequestBody @Valid AuthSmsLoginReqVO reqVO) {
        return success(authService.smsLogin(reqVO));
    }

    @PostMapping("/send-sms-code")
    @PermitAll
    @Operation(summary = "发送手机验证码")
    public CommonResult<Boolean> sendLoginSmsCode(@RequestBody @Valid AuthSmsSendReqVO reqVO) {
        authService.sendSmsCode(reqVO);
        return success(true);
    }

    @PostMapping("/reset-password")
    @PermitAll
    @Operation(summary = "重置密码")
    public CommonResult<Boolean> resetPassword(@RequestBody @Valid AuthResetPasswordReqVO reqVO) {
        authService.resetPassword(reqVO);
        return success(true);
    }

    // ========== 社交登录相关 ==========

    @GetMapping("/social-auth-redirect")
    @PermitAll
    @Operation(summary = "社交授权的跳转")
    @Parameters({
            @Parameter(name = "type", description = "社交类型", required = true),
            @Parameter(name = "redirectUri", description = "回调路径")
    })
    public CommonResult<String> socialLogin(@RequestParam("type") Integer type,
                                            @RequestParam("redirectUri") String redirectUri) {
        return success(socialClientService.getAuthorizeUrl(
                type, UserTypeEnum.ADMIN.getValue(), redirectUri));
    }

    @PostMapping("/social-login")
    @PermitAll
    @Operation(summary = "社交快捷登录，使用 code 授权码", description = "适合未登录的用户，但是社交账号已绑定用户")
    public CommonResult<AuthLoginRespVO> socialQuickLogin(@RequestBody @Valid AuthSocialLoginReqVO reqVO) {
        return success(authService.socialLogin(reqVO));
    }

}
