package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 专项活动付款申请保存 Request VO")
@Data
public class ActivityPaymentRequestSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "专项活动ID")
    private Long activityId;

    @Schema(description = "活动实例ID（兼容单选；多选时取 instanceIds 首项）")
    private Long instanceId;

    @Schema(description = "活动实例ID列表（多选）")
    private List<Long> instanceIds;

    @Schema(description = "申请事由")
    private String title;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "费用明细ID列表")
    private List<Long> feeItemIds;

    @Schema(description = "费用明细实际金额（与所选明细对应，必填）")
    private List<ActivityPaymentFeeActualReqVO> feeActualAmounts;

    @Schema(description = "合计金额（后端按实际金额重算）")
    private BigDecimal totalAmount;

    @Schema(description = "币种（后端按明细回填）")
    private String currency;

}
