package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "专项活动执行实例 - 活动反馈聚合 Response VO")
@Data
public class ActivityInstanceFeedbackRespVO {

    @Schema(description = "是否可提交/编辑反馈（待反馈态）")
    private Boolean feedbackEditable;

    @Schema(description = "当前用户是否可提交学生反馈")
    private Boolean canSubmitStudentFeedback;

    @Schema(description = "当前用户是否可提交教师反馈")
    private Boolean canSubmitTeacherFeedback;

    @Schema(description = "当前用户是否可提交班务评价")
    private Boolean canSubmitAdminFeedback;

    @Schema(description = "应反馈学生数")
    private Integer expectedStudentCount;

    @Schema(description = "已提交学生反馈数")
    private Integer submittedStudentCount;

    @Schema(description = "应反馈教培数")
    private Integer expectedTeacherCount;

    @Schema(description = "已提交教师反馈数")
    private Integer submittedTeacherCount;

    @Schema(description = "班务评价是否已提交")
    private Boolean adminFeedbackSubmitted;

    @Schema(description = "是否全部反馈完成")
    private Boolean allFeedbackCompleted;

    @Schema(description = "学生反馈列表")
    private List<ActivityInstanceStudentFeedbackRespVO> studentFeedbacks;

    @Schema(description = "教师反馈列表")
    private List<ActivityInstanceTeacherFeedbackRespVO> teacherFeedbacks;

    @Schema(description = "班务评价")
    private ActivityInstanceAdminFeedbackRespVO adminFeedback;

}
