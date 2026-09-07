package cn.dh.edu.module.edu.controller.admin.activity.vo;

import cn.dh.edu.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 专项活动执行实例分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ActivityInstancePageReqVO extends PageParam {

    @Schema(description = "实例编号")
    private String instanceCode;

    @Schema(description = "活动名称（模糊）")
    private String activityName;

    @Schema(description = "关键字（模糊匹配实例编号或活动名称）")
    private String keyword;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "计划执行时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] plannedDate;

}
