package cn.dh.edu.module.edu.controller.admin.activity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 专项活动执行实例 Response VO")
@Data
public class ActivityInstanceRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "实例编号")
    private String instanceCode;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "活动单据编号")
    private String activityBillCode;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "期次编号")
    private Integer periodNo;

    @Schema(description = "计划执行时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime plannedDate;

    @Schema(description = "实际执行时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime actualDate;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "报名人数")
    private Integer enrollCount;

    @Schema(description = "报名上限")
    private Integer enrollLimit;

    @Schema(description = "出勤人数")
    private Integer attendanceCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
