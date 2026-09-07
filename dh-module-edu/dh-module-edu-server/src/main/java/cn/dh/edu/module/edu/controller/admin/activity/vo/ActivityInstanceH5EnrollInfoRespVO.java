package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "专项活动 H5 报名信息 Response VO")
@Data
public class ActivityInstanceH5EnrollInfoRespVO {

    @Schema(description = "租户编号（前端可写入请求头）")
    private Long tenantId;

    @Schema(description = "实例编号")
    private Long instanceId;

    @Schema(description = "实例编码")
    private String instanceCode;

    @Schema(description = "期次")
    private Integer periodNo;

    @Schema(description = "计划执行时间")
    private LocalDateTime plannedDate;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "活动内容")
    private String content;

    @Schema(description = "活动类型")
    private String activityType;

    @Schema(description = "报名开始时间")
    private LocalDateTime enrollStartTime;

    @Schema(description = "报名结束时间")
    private LocalDateTime enrollEndTime;

    @Schema(description = "学生昵称")
    private String userName;

    @Schema(description = "是否已报名")
    private Boolean enrolled;

    @Schema(description = "是否可报名")
    private Boolean canEnroll;

    @Schema(description = "不可报名原因提示")
    private String message;

    @Schema(description = "报名人数")
    private Integer enrollCount;

    @Schema(description = "报名上限，0 表示不限")
    private Integer enrollLimit;

}
