package cn.dh.oa.module.system.service.auth;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.dh.oa.framework.common.enums.UserTypeEnum;
import cn.dh.oa.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import cn.dh.oa.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import cn.dh.oa.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.dh.oa.module.system.dal.dataobject.social.SocialUserDO;
import cn.dh.oa.module.system.dal.dataobject.user.AdminUserDO;
import cn.dh.oa.module.system.dal.mysql.social.SocialUserBindMapper;
import cn.dh.oa.module.system.dal.mysql.social.SocialUserMapper;
import cn.dh.oa.module.system.enums.social.SocialTypeEnum;
import cn.dh.oa.module.system.enums.logger.LoginLogTypeEnum;
import cn.dh.oa.module.system.service.oauth2.OAuth2TokenService;
import cn.dh.oa.module.system.service.social.SocialUserService;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.system.enums.ErrorCodeConstants.AUTH_THIRD_LOGIN_NOT_BIND;

/**
 * App 认证 Service 实现类
 */
@Service
@Validated
@Slf4j
public class AppAuthServiceImpl implements AppAuthService {

    @Resource
    private WxMaService wxMaService;

    @Resource
    private SocialUserMapper socialUserMapper;
    @Resource
    private SocialUserBindMapper socialUserBindMapper;

    @Resource
    private OAuth2TokenService oauth2TokenService;

    @Resource
    private AdminAuthService adminAuthService;

    @Resource
    private SocialUserService socialUserService;

    @Override
    public AuthLoginRespVO wxMiniLogin(String code) {
        // 1. 调用微信接口获取 openid
        String openid;
        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.jsCode2SessionInfo(code);
            openid = sessionInfo.getOpenid();
        } catch (WxErrorException e) {
            log.error("[wxMiniLogin] 微信登录 code 换取 openid 失败, code={}", code, e);
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }

        // 2. 根据 type=微信小程序 + openid 查询社交用户
        SocialUserDO socialUser = socialUserMapper.selectByTypeAndOpenid(
                SocialTypeEnum.WECHAT_MINI_PROGRAM.getType(), openid);
        if (socialUser == null) {
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }

        // 3. 查询社交用户绑定关系，获得 userId
        SocialUserBindDO socialUserBind = socialUserBindMapper.selectByUserTypeAndSocialUserId(
                UserTypeEnum.ADMIN.getValue(), socialUser.getId());
        if (socialUserBind == null) {
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }

        // 4. 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(
                socialUserBind.getUserId(), UserTypeEnum.ADMIN.getValue(), "default", null);

        // 5. 构建返回结果
        return AuthLoginRespVO.builder()
                .userId(accessTokenDO.getUserId())
                .accessToken(accessTokenDO.getAccessToken())
                .refreshToken(accessTokenDO.getRefreshToken())
                .expiresTime(accessTokenDO.getExpiresTime())
                .build();
    }

    @Override
    public AuthLoginRespVO wxMiniBindLogin(String code, String username, String password) {
        // 1. 调用微信接口获取 openid
        String openid;
        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.jsCode2SessionInfo(code);
            openid = sessionInfo.getOpenid();
        } catch (WxErrorException e) {
            log.error("[wxMiniBindLogin] 微信登录 code 换取 openid 失败, code={}", code, e);
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }

        // 2. 通过账号密码校验用户身份（内部已处理用户不存在、密码错误、账号禁用等异常）
        AdminUserDO user = adminAuthService.authenticate(username, password);

        // 3. 创建或更新社交用户记录（system_social_user）
        Integer socialType = SocialTypeEnum.WECHAT_MINI_PROGRAM.getType();
        SocialUserDO socialUser = socialUserMapper.selectByTypeAndOpenid(socialType, openid);
        if (socialUser == null) {
            socialUser = new SocialUserDO();
            socialUser.setType(socialType);
            socialUser.setOpenid(openid);
            socialUser.setNickname(username);
            socialUser.setRawTokenInfo("{}");
            socialUser.setRawUserInfo("{}");
            socialUser.setCode("");
            socialUserMapper.insert(socialUser);
        }

        // 4. 创建或更新绑定关系（system_social_user_bind）
        Integer userType = UserTypeEnum.ADMIN.getValue();
        SocialUserBindDO bind = socialUserBindMapper.selectByUserTypeAndSocialUserId(
                userType, socialUser.getId());
        if (bind == null) {
            bind = new SocialUserBindDO();
            bind.setUserId(user.getId());
            bind.setUserType(userType);
            bind.setSocialUserId(socialUser.getId());
            bind.setSocialType(socialType);
            socialUserBindMapper.insert(bind);
        } else if (!bind.getUserId().equals(user.getId())) {
            // 微信号已绑定其他账号，更新绑定到当前用户
            bind.setUserId(user.getId());
            socialUserBindMapper.updateById(bind);
        }

        // 5. 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(
                user.getId(), userType, "default", null);

        return AuthLoginRespVO.builder()
                .userId(accessTokenDO.getUserId())
                .accessToken(accessTokenDO.getAccessToken())
                .refreshToken(accessTokenDO.getRefreshToken())
                .expiresTime(accessTokenDO.getExpiresTime())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void wxMiniLogout(String code, Long userId, String token) {
        Long actualUserId = userId;
        if (actualUserId == null && StrUtil.isNotBlank(token)) {
            OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.getAccessToken(token);
            if (accessTokenDO != null) {
                actualUserId = accessTokenDO.getUserId();
            }
        }

        // 1. 解绑当前微信 openid 与用户的绑定关系
        if (actualUserId != null) {
            try {
                WxMaJscode2SessionResult sessionInfo = wxMaService.jsCode2SessionInfo(code);
                socialUserService.unbindSocialUser(actualUserId, UserTypeEnum.ADMIN.getValue(),
                        SocialTypeEnum.WECHAT_MINI_PROGRAM.getType(), sessionInfo.getOpenid());
            } catch (WxErrorException e) {
                log.warn("[wxMiniLogout] 微信 code 换取 openid 失败, userId={}", actualUserId, e);
            } catch (Exception e) {
                log.warn("[wxMiniLogout] 解绑微信失败, userId={}", actualUserId, e);
            }
        }

        // 2. 注销 token（解绑失败也继续登出）
        if (StrUtil.isNotBlank(token)) {
            adminAuthService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
    }

}
