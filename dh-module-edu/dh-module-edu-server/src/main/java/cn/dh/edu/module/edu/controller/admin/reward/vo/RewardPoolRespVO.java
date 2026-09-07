package cn.dh.edu.module.edu.controller.admin.reward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 奖金池 Response VO")
@Data
public class RewardPoolRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "池名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "专项活动奖金池")
    private String name;

    @Schema(description = "总预算", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
    private BigDecimal totalBudget;

    @Schema(description = "已冻结", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.00")
    private BigDecimal frozenAmount;

    @Schema(description = "已实发", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.00")
    private BigDecimal paidAmount;

    @Schema(description = "可用余额 = 总预算 - 已冻结 - 已实发", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
    private BigDecimal availableAmount;

    @Schema(description = "状态（0启用 1停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    @Schema(description = "备注", example = "系统默认奖金池")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
