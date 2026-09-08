package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 专项活动付款申请 Response VO")
@Data
public class ActivityPaymentRequestRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "专项活动ID")
    private Long activityId;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "活动编号")
    private String activityBillCode;

    @Schema(description = "活动实例ID")
    private Long instanceId;

    @Schema(description = "实例编号")
    private String instanceCode;

    @Schema(description = "申请事由")
    private String title;

    @Schema(description = "合计金额")
    private BigDecimal totalAmount;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "流程状态")
    private Integer processStatus;

    @Schema(description = "审批通过时间")
    private LocalDateTime approveTime;

    @Schema(description = "审批通过时锁定汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "审批通过时锁定折合人民币金额")
    private BigDecimal amountCny;

    @Schema(description = "申请人用户ID")
    private Long applicantUserId;

    @Schema(description = "申请人姓名")
    private String applicantUserName;

    @Schema(description = "申请人姓名（表头 creatorName）")
    private String creatorName;

    @Schema(description = "所属部门名称")
    private String deptName;

    @Schema(description = "所属单位名称")
    private String companyName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "关联费用明细")
    private List<ActivityPaymentFeeItemRespVO> feeItems;

    @Schema(description = "费用明细ID列表")
    private List<Long> feeItemIds;

}
