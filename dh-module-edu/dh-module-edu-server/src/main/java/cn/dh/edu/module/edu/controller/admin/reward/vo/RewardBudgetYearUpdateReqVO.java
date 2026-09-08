package cn.dh.edu.module.edu.controller.admin.reward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 预算配置年度预算更新 Request VO")
@Data
public class RewardBudgetYearUpdateReqVO {

    @Schema(description = "年度预算编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "年度预算编号不能为空")
    private Long id;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "时段模式：QUARTER/MONTH/CUSTOM；变更时按新模式重建/保留时段")
    private String periodMode;

    @Schema(description = "全年预算（传入则按当前时段均分重置各段预算）")
    private BigDecimal totalBudget;

    @Schema(description = "自定义时段全集（仅 CUSTOM；传入则整体替换）")
    private List<PeriodItem> periods;

    @Data
    public static class PeriodItem {

        @Schema(description = "时段编号（已有可传，新建可不传）")
        private Long id;

        @Schema(description = "时段名称")
        private String name;

        @Schema(description = "开始日期")
        private LocalDate startDate;

        @Schema(description = "结束日期")
        private LocalDate endDate;

        @Schema(description = "预算金额")
        private BigDecimal budgetAmount;
    }

}
