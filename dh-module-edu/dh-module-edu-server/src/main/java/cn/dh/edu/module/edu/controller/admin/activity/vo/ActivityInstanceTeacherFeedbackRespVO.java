package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "专项活动执行实例 - 教师反馈 Response VO")
@Data
public class ActivityInstanceTeacherFeedbackRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户姓名")
    private String userName;

    @Schema(description = "执行总结")
    private String summary;

    @Schema(description = "问题")
    private String problem;

    @Schema(description = "建议")
    private String suggestion;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "是否已提交")
    private Boolean submitted;

}
