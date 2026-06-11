package cn.dh.oa.module.oa.controller.admin.contract.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 收付款计划 Response VO")
@Data
public class ContractPaymentPlanRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "合同审批单ID", example = "1")
    private Long billId;

    @Schema(description = "期次", example = "1")
    private Integer period;

    @Schema(description = "计划金额", example = "0.00")
    private BigDecimal planAmount;

    @Schema(description = "计划日期")
    private LocalDate planDate;

    @Schema(description = "实际金额")
    private BigDecimal actualAmount;

    @Schema(description = "实际日期")
    private LocalDate actualDate;

    @Schema(description = "状态（0待收付 1已收付 2已逾期）", example = "0")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
