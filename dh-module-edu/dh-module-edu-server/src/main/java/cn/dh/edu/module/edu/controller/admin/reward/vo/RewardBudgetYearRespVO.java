package cn.dh.edu.module.edu.controller.admin.reward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 预算配置年度预算 Response VO")
@Data
public class RewardBudgetYearRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "预算年度")
    private Integer budgetYear;

    @Schema(description = "时段模式")
    private String periodMode;

    @Schema(description = "年度预算合计")
    private BigDecimal totalBudget;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "时段列表")
    private List<PeriodItem> periods;

    @Data
    public static class PeriodItem {
        private Long id;
        private String name;
        private Integer periodNo;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal budgetAmount;
    }

}
