package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "付款申请 - 费用明细实际金额")
@Data
public class ActivityPaymentFeeActualReqVO {

    @Schema(description = "费用明细ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long feeItemId;

    @Schema(description = "实际金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal actualAmount;

}
