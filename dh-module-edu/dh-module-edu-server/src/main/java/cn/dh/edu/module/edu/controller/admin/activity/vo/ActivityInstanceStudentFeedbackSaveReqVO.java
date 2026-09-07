package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "专项活动执行实例 - 学生反馈保存 Request VO")
@Data
public class ActivityInstanceStudentFeedbackSaveReqVO {

    @Schema(description = "执行实例ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动实例ID不能为空")
    private Long instanceId;

    @Schema(description = "满意度（1-5）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "满意度不能为空")
    @Min(value = 1, message = "满意度最小为1")
    @Max(value = 5, message = "满意度最大为5")
    private Integer satisfaction;

    @Schema(description = "收获")
    @Size(max = 500, message = "收获不能超过500个字符")
    private String harvest;

    @Schema(description = "文字评价")
    @Size(max = 2000, message = "文字评价不能超过2000个字符")
    private String content;

}
