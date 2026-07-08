package cn.dh.oa.module.oa.controller.admin.expensepayment.vo;

import cn.dh.oa.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 费用支出申请分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExpensePaymentBillPageReqVO extends PageParam {

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "单据状态")
    private Integer processStatus;

    @Schema(description = "支出类型（1对公 2对私）")
    private Integer paymentType;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
