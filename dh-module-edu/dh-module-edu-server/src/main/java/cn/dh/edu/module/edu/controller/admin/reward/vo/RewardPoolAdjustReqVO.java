package cn.dh.edu.module.edu.controller.admin.reward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 奖金池调账 Request VO")
@Data
public class RewardPoolAdjustReqVO {

    @Schema(description = "调账方向：UP 充值 / DOWN 调减", requiredMode = Schema.RequiredMode.REQUIRED, example = "UP")
    @NotBlank(message = "调账方向不能为空")
    private String direction;

    @Schema(description = "调账金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000.00")
    @NotNull(message = "调账金额不能为空")
    @DecimalMin(value = "0.01", message = "调账金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "调账说明", requiredMode = Schema.RequiredMode.REQUIRED, example = "期初注资")
    @NotBlank(message = "调账说明不能为空")
    @Size(max = 500, message = "调账说明长度不能超过500")
    private String remark;

}
