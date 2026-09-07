package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "专项活动执行实例 - 报名记录 Response VO")
@Data
public class ActivityInstanceEnrollmentRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "执行实例ID")
    private Long instanceId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户姓名（展示）")
    private String userName;

    @Schema(description = "报名时间")
    private LocalDateTime enrollTime;

}
