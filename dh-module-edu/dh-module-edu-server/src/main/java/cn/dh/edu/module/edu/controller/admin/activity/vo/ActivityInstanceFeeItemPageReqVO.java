package cn.dh.edu.module.edu.controller.admin.activity.vo;

import cn.dh.edu.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 专项活动费用明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ActivityInstanceFeeItemPageReqVO extends PageParam {

    @Schema(description = "明细编号")
    private String feeCode;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "活动名称（模糊）")
    private String activityName;

    @Schema(description = "实例ID")
    private Long instanceId;

    @Schema(description = "实例编号（模糊）")
    private String instanceCode;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "费用侧")
    private String feeSide;

    @Schema(description = "费用类型")
    private String feeType;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "生成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] generateTime;

}
