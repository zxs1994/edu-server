package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "专项活动执行实例 - 班务评价保存 Request VO")
@Data
public class ActivityInstanceAdminFeedbackSaveReqVO {

    @Schema(description = "执行实例ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动实例ID不能为空")
    private Long instanceId;

    @Schema(description = "质量评分（1-5）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "质量评分不能为空")
    @Min(value = 1, message = "质量评分最小为1")
    @Max(value = 5, message = "质量评分最大为5")
    private Integer qualityScore;

    @Schema(description = "结项意见")
    @Size(max = 2000, message = "结项意见不能超过2000个字符")
    private String closingOpinion;

}
