package cn.dh.oa.module.system.service.auth;

import cn.dh.oa.module.system.controller.admin.auth.vo.AuthLoginRespVO;

/**
 * App 认证 Service 接口
 *
 * 提供小程序登录等 App 端认证能力
 */
public interface AppAuthService {

    /**
     * 微信小程序登录（已绑定微信自动登录）
     *
     * @param code 微信登录 code
     * @return 登录结果
     */
    AuthLoginRespVO wxMiniLogin(String code);

    /**
     * 微信小程序绑定登录（未绑定微信时，通过账号密码验证后自动绑定）
     *
     * @param code     微信登录 code
     * @param username 账号
     * @param password 密码
     * @return 登录结果
     */
    AuthLoginRespVO wxMiniBindLogin(String code, String username, String password);

    /**
     * 微信小程序退出登录（解绑微信并注销 token）
     *
     * @param code   微信登录 code
     * @param userId 当前登录用户 ID
     * @param token  访问令牌
     */
    void wxMiniLogout(String code, Long userId, String token);

}
