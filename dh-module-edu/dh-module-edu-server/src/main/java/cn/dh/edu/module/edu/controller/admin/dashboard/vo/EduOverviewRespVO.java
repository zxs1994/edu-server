package cn.dh.edu.module.edu.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 信息总览 Response VO")
@Data
public class EduOverviewRespVO {

    @Schema(description = "学员数", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long studentCount;

    @Schema(description = "师资人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Long teacherCount;

    @Schema(description = "专项活动数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Long activityCount;

    @Schema(description = "预算执行率（暂未接入）", example = "0")
    private Long budgetExecRate;

}
