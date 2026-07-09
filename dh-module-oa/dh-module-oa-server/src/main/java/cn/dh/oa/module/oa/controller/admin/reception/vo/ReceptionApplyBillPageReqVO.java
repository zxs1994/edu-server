package cn.dh.oa.module.oa.controller.admin.reception.vo;

import cn.dh.oa.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.dh.oa.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 接待申请单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReceptionApplyBillPageReqVO extends PageParam {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态")
    private Integer processStatus;

    @Schema(description = "申请事由")
    private String cause;

    @Schema(description = "就餐标准")
    private String diningStandard;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "申请人姓名")
    private String creatorName;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
