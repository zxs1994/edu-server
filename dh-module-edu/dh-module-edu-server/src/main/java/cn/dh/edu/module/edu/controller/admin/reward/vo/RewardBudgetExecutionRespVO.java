package cn.dh.edu.module.edu.controller.admin.reward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 预算配置执行率 Response VO")
@Data
public class RewardBudgetExecutionRespVO {

    @Schema(description = "年度预算ID")
    private Long yearId;

    @Schema(description = "预算年度")
    private Integer budgetYear;

    @Schema(description = "时段模式")
    private String periodMode;

    @Schema(description = "全年预算")
    private BigDecimal totalBudget;

    @Schema(description = "全年已执行（审批通过付款合计，折人民币）")
    private BigDecimal totalExecuted;

    @Schema(description = "全年执行率（0~1；预算为0时为空）")
    private BigDecimal executionRate;

    @Schema(description = "各时段执行情况")
    private List<PeriodExecution> periods;

    @Data
    public static class PeriodExecution {
        private Long id;
        private String name;
        private Integer periodNo;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal budgetAmount;
        private BigDecimal executedAmount;
        private BigDecimal executionRate;
    }

}
