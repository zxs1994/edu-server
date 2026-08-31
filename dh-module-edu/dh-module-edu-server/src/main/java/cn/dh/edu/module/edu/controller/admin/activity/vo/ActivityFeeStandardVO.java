package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 专项活动费用标准")
@Data
public class ActivityFeeStandardVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "费用类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String feeType;

    @Schema(description = "计费模式", requiredMode = Schema.RequiredMode.REQUIRED)
    private String feeMode;

    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "收款人用户ID")
    private Long payeeUserId;

    @Schema(description = "收款人姓名")
    private String payeeUserName;

    @Schema(description = "费用侧", requiredMode = Schema.RequiredMode.REQUIRED)
    private String feeSide;

    @Schema(description = "费用项说明")
    private String remark;

}
