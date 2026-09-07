package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "专项活动 H5 报名 Request VO")
@Data
public class ActivityInstanceH5EnrollReqVO {

    @Schema(description = "短码", requiredMode = Schema.RequiredMode.REQUIRED, example = "Ab3xK9")
    @NotBlank(message = "短码不能为空")
    private String c;

}
