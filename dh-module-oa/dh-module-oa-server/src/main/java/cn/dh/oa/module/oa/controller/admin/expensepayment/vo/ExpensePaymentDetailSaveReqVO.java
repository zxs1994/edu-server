package cn.dh.oa.module.oa.controller.admin.expensepayment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 费用支出明细 Save VO")
@Data
public class ExpensePaymentDetailSaveReqVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "费用类型")
    private String expenseType;

    @Schema(description = "费用发生日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate expenseDate;

    @Schema(description = "费用事由/用途说明")
    private String cause;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序")
    private Integer sortOrder;

}
