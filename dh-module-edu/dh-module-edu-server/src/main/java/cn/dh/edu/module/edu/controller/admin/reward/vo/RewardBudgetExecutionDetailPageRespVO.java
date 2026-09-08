package cn.dh.edu.module.edu.controller.admin.reward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 预算执行明细分页 Response VO")
@Data
public class RewardBudgetExecutionDetailPageRespVO {

    @Schema(description = "预算年度")
    private Integer budgetYear;

    @Schema(description = "时段ID")
    private Long periodId;

    @Schema(description = "时段名称")
    private String periodName;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "本查询范围内已执行合计（折人民币，全量非仅本页）")
    private BigDecimal executedAmountCny;

    @Schema(description = "总条数")
    private Long total;

    @Schema(description = "当前页明细")
    private List<Item> list;

    @Data
    public static class Item {

        @Schema(description = "付款申请ID")
        private Long paymentRequestId;

        @Schema(description = "单据编号")
        private String billCode;

        @Schema(description = "申请事由")
        private String title;

        @Schema(description = "活动ID")
        private Long activityId;

        @Schema(description = "活动名称")
        private String activityName;

        @Schema(description = "申请人用户ID")
        private Long applicantUserId;

        @Schema(description = "申请人姓名")
        private String applicantUserName;

        @Schema(description = "审批通过时间")
        private LocalDateTime approveTime;

        @Schema(description = "原币金额")
        private BigDecimal totalAmount;

        @Schema(description = "币种")
        private String currency;

        @Schema(description = "锁定汇率（1 外币 = X 人民币）")
        private BigDecimal exchangeRate;

        @Schema(description = "折合人民币金额（审批锁定）")
        private BigDecimal amountCny;
    }

}
