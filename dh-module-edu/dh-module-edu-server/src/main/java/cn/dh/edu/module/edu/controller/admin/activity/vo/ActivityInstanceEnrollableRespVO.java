package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "专项活动执行实例 - 可报名实例 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityInstanceEnrollableRespVO extends ActivityInstanceRespVO {

    @Schema(description = "活动类型")
    private String activityType;

    @Schema(description = "是否已报名")
    private Boolean enrolled;

    @Schema(description = "是否可报名")
    private Boolean canEnroll;

    @Schema(description = "是否可取消报名")
    private Boolean canCancel;

    @Schema(description = "报名开始时间")
    private LocalDateTime enrollStartTime;

    @Schema(description = "报名结束时间")
    private LocalDateTime enrollEndTime;

    @Schema(description = "是否可提交学生反馈（待反馈且已报名未缺席）")
    private Boolean canSubmitStudentFeedback;

    @Schema(description = "当前用户是否已提交学生反馈")
    private Boolean studentFeedbackSubmitted;

    @Schema(description = "我的反馈状态：pending/submitted/waiting/none")
    private String myFeedbackStatus;

    @Schema(description = "学生反馈人数")
    private Integer feedbackCount;

}
