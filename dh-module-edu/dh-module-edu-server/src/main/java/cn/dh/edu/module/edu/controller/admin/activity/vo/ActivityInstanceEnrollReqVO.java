package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "专项活动执行实例 - 报名/取消 Request VO")
@Data
public class ActivityInstanceEnrollReqVO {

    @Schema(description = "执行实例ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动实例ID不能为空")
    private Long instanceId;

}
