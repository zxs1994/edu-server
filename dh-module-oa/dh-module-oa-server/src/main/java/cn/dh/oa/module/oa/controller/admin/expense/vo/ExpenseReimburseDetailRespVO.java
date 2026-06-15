package cn.dh.oa.module.oa.controller.admin.expense.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;

@Schema(description = "管理后台 - 差旅报销明细 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExpenseReimburseDetailRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "报销单ID", example = "1")
    @ExcelProperty("报销单ID")
    private Long billId;

    @Schema(description = "费用类型（交通费/住宿费等）")
    @ExcelProperty("费用类型")
    private String expenseType;

    @Schema(description = "发生日期")
    @ExcelProperty("发生日期")
    private LocalDate expenseDate;

    @Schema(description = "出发地")
    @ExcelProperty("出发地")
    private String departure;

    @Schema(description = "到达地")
    @ExcelProperty("到达地")
    private String destination;

    @Schema(description = "金额", example = "0.00")
    @ExcelProperty("金额")
    private BigDecimal amount;

    @Schema(description = "费用说明")
    @ExcelProperty("费用说明")
    private String description;

    @Schema(description = "排序", example = "1")
    @ExcelProperty("排序")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
