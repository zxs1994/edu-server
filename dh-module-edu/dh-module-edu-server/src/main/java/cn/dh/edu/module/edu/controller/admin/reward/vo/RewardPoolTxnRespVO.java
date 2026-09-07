package cn.dh.edu.module.edu.controller.admin.reward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 奖金池流水 Response VO")
@Data
public class RewardPoolTxnRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "奖金池编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long poolId;

    @Schema(description = "流水类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "RECHARGE")
    private String txnType;

    @Schema(description = "变动金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000.00")
    private BigDecimal amount;

    @Schema(description = "变动后总预算", example = "10000.00")
    private BigDecimal totalAfter;

    @Schema(description = "变动后已冻结", example = "0.00")
    private BigDecimal frozenAfter;

    @Schema(description = "变动后已实发", example = "0.00")
    private BigDecimal paidAfter;

    @Schema(description = "业务类型", example = "MANUAL")
    private String bizType;

    @Schema(description = "业务ID")
    private Long bizId;

    @Schema(description = "说明", example = "期初注资")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建者用户编号")
    private String creator;

    @Schema(description = "操作人", example = "张三")
    private String creatorName;

}
