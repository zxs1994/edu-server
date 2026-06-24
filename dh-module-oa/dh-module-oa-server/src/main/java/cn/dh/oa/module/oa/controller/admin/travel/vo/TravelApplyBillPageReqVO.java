package cn.dh.oa.module.oa.controller.admin.travel.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.dh.oa.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 差旅申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TravelApplyBillPageReqVO extends PageParam {

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "单据状态")
    private Integer processStatus;

    @Schema(description = "申请部门")
    private String deptName;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "报销状态（0未报销 1已报销）")
    private Integer reimbursementStatus;

    @Schema(description = "排除已被其他差旅报销单关联的单据")
    private Boolean excludeLinkedToExpense;

    @Schema(description = "编辑差旅报销单时传入当前报销单ID，其已关联的出差申请仍可展示")
    private Long excludeExpenseBillId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
