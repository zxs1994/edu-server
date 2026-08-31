package cn.dh.edu.module.system.controller.app.auth.vo;

import jakarta.validation.constraints.NotEmpty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "小程序登录 Request VO")
@Data
public class WxMiniLoginReqVO {

    @Schema(description = "微信登录code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "微信登录code不能为空")
    private String code;

}
