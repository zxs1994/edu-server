package cn.dh.edu.module.edu.controller.admin.reward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 预算配置时段预算更新 Request VO")
@Data
public class RewardBudgetPeriodUpdateReqVO {

    @Schema(description = "时段编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "时段编号不能为空")
    private Long id;

    @Schema(description = "时段名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "时段名称不能为空")
    private String name;

    @Schema(description = "预算金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预算金额不能为空")
    private BigDecimal budgetAmount;

    @Schema(description = "开始日期（CUSTOM 可改）")
    private LocalDate startDate;

    @Schema(description = "结束日期（CUSTOM 可改）")
    private LocalDate endDate;

}
