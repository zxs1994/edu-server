package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "专项活动执行实例 - 班务评价 Response VO")
@Data
public class ActivityInstanceAdminFeedbackRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "提交人用户ID")
    private Long submitterUserId;

    @Schema(description = "提交人姓名")
    private String submitterUserName;

    @Schema(description = "质量评分（1-5）")
    private Integer qualityScore;

    @Schema(description = "结项意见")
    private String closingOpinion;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "是否已提交")
    private Boolean submitted;

}
