package cn.dh.oa.module.system.controller.app.auth;

import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import cn.dh.oa.module.system.controller.app.auth.vo.WxMiniLoginReqVO;
import cn.dh.oa.module.system.controller.app.auth.vo.WxMiniBindLoginReqVO;
import cn.dh.oa.module.system.service.auth.AppAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.dh.oa.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
public class AppAuthController {

    @Resource
    private AppAuthService appAuthService;

    @PostMapping("/wx-mini-login")
    @Operation(summary = "微信小程序登录")
    @PermitAll
    public CommonResult<AuthLoginRespVO> wxMiniLogin(@Valid @RequestBody WxMiniLoginReqVO reqVO) {
        return success(appAuthService.wxMiniLogin(reqVO.getCode()));
    }

    @PostMapping("/wx-mini-bind-login")
    @Operation(summary = "微信小程序绑定登录（账号密码验证后自动绑定微信）")
    @PermitAll
    public CommonResult<AuthLoginRespVO> wxMiniBindLogin(@Valid @RequestBody WxMiniBindLoginReqVO reqVO) {
        return success(appAuthService.wxMiniBindLogin(reqVO.getCode(), reqVO.getUsername(), reqVO.getPassword()));
    }

}
