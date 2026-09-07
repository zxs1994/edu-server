package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "专项活动执行实例 - 参与人报名状态 Response VO")
@Data
public class ActivityInstanceEnrollmentParticipantRespVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户姓名（展示）")
    private String userName;

    @Schema(description = "参与人角色类型：teacher-教培，student-学生")
    private String roleType;

    @Schema(description = "参与人角色名称")
    private String roleLabel;

    @Schema(description = "是否已报名（教培默认 true）")
    private Boolean enrolled;

    @Schema(description = "报名时间")
    private LocalDateTime enrollTime;

}
