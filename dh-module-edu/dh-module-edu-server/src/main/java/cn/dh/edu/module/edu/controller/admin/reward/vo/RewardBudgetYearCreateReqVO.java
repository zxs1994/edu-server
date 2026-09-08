package cn.dh.edu.module.edu.controller.admin.reward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 预算配置年度预算创建 Request VO")
@Data
public class RewardBudgetYearCreateReqVO {

    @Schema(description = "预算年度", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026")
    @NotNull(message = "预算年度不能为空")
    private Integer budgetYear;

    @Schema(description = "时段模式：QUARTER/MONTH/CUSTOM", requiredMode = Schema.RequiredMode.REQUIRED, example = "QUARTER")
    @NotBlank(message = "时段模式不能为空")
    private String periodMode;

    @Schema(description = "全年预算（QUARTER/MONTH 时均分到各时段）", example = "120000.00")
    private BigDecimal totalBudget;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "自定义时段（仅 CUSTOM 模式可传）")
    private List<PeriodItem> periods;

    @Data
    public static class PeriodItem {

        @Schema(description = "时段名称", example = "上半年")
        private String name;

        @Schema(description = "开始日期", example = "2026-01-01")
        private LocalDate startDate;

        @Schema(description = "结束日期", example = "2026-06-30")
        private LocalDate endDate;

        @Schema(description = "预算金额", example = "100000.00")
        private BigDecimal budgetAmount;
    }

}
