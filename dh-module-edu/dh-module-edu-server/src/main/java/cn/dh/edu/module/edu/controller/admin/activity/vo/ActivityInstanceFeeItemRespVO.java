package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 专项活动执行实例费用明细")
@Data
public class ActivityInstanceFeeItemRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "明细编号")
    private String feeCode;

    @Schema(description = "执行实例ID")
    private Long instanceId;

    @Schema(description = "实例编号")
    private String instanceCode;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "费用类型")
    private String feeType;

    @Schema(description = "计费模式")
    private String feeMode;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "单价")
    private BigDecimal unitPrice;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "预算金额")
    private BigDecimal amount;

    @Schema(description = "实际金额")
    private BigDecimal actualAmount;

    @Schema(description = "费用侧")
    private String feeSide;

    @Schema(description = "收款人用户ID")
    private Long payeeUserId;

    @Schema(description = "收款人姓名")
    private String payeeUserName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "关联付款申请ID")
    private Long paymentRequestId;

    @Schema(description = "关联付款申请单号")
    private String paymentBillCode;

    @Schema(description = "生成时间")
    private LocalDateTime generateTime;

    @Schema(description = "付款时间")
    private LocalDateTime payTime;

    @Schema(description = "费用项说明")
    private String remark;

}
