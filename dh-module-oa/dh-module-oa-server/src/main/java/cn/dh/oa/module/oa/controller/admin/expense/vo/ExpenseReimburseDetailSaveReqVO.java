package cn.dh.oa.module.oa.controller.admin.expense.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 差旅报销明细新增/修改 Request VO")
@Data
public class ExpenseReimburseDetailSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "报销单ID", example = "1")
    private Long billId;

    @Schema(description = "费用类型（交通费/住宿费等）")
    private String expenseType;

    @Schema(description = "发生日期")
    private LocalDate expenseDate;

    @Schema(description = "出发地")
    private String departure;

    @Schema(description = "到达地")
    private String destination;

    @Schema(description = "金额", example = "0.00")
    private BigDecimal amount;

    @Schema(description = "费用说明")
    private String description;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

}
