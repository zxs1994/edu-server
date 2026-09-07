package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "专项活动执行实例 - 学生反馈 Response VO")
@Data
public class ActivityInstanceStudentFeedbackRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户姓名")
    private String userName;

    @Schema(description = "满意度（1-5）")
    private Integer satisfaction;

    @Schema(description = "收获")
    private String harvest;

    @Schema(description = "文字评价")
    private String content;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "是否已提交")
    private Boolean submitted;

}
