package cn.dh.edu.module.edu.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 信息总览 Response VO")
@Data
public class EduOverviewRespVO {

    @Schema(description = "学员数", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long studentCount;

    @Schema(description = "师资人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Long teacherCount;

    @Schema(description = "专项活动数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Long activityCount;

    @Schema(description = "预算年度（当年有配置时返回）", example = "2026")
    private Integer budgetYear;

    @Schema(description = "时段模式：QUARTER/MONTH/CUSTOM", example = "QUARTER")
    private String budgetPeriodMode;

    @Schema(description = "当前展示时段名称，如 Q1 / 3月", example = "Q1")
    private String budgetPeriodName;

    @Schema(description = "当前时段预算执行率（0~1；无预算或预算为0时为空）", example = "0.452")
    private BigDecimal budgetExecRate;

}
