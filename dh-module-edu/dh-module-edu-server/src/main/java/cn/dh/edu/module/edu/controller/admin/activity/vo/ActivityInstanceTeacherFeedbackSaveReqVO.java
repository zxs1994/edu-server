package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "专项活动执行实例 - 教师反馈保存 Request VO")
@Data
public class ActivityInstanceTeacherFeedbackSaveReqVO {

    @Schema(description = "执行实例ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动实例ID不能为空")
    private Long instanceId;

    @Schema(description = "执行总结", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "执行总结不能为空")
    @Size(max = 2000, message = "执行总结不能超过2000个字符")
    private String summary;

    @Schema(description = "问题")
    @Size(max = 2000, message = "问题不能超过2000个字符")
    private String problem;

    @Schema(description = "建议")
    @Size(max = 2000, message = "建议不能超过2000个字符")
    private String suggestion;

}
