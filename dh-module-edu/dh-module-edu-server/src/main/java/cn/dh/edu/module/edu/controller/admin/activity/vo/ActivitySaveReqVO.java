package cn.dh.edu.module.edu.controller.admin.activity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;
import static cn.dh.edu.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 专项活动新增/修改 Request VO")
@Data
public class ActivitySaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "流程状态")
    private Integer processStatus;

    @Schema(description = "活动名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "活动类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String activityType;

    @Schema(description = "活动子类型")
    private String activitySubtype;

    @Schema(description = "活动内容")
    private String content;

    @Schema(description = "周期类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cycleType;

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "报名开始时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime enrollStartTime;

    @Schema(description = "报名结束时间")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime enrollEndTime;

    @Schema(description = "预算总额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal budgetAmount;

    @Schema(description = "制单人部门ID")
    private Long deptId;

    @Schema(description = "制单人部门名称")
    private String deptName;

    @Schema(description = "制单人公司ID")
    private Long companyId;

    @Schema(description = "制单人公司名称")
    private String companyName;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "创建者姓名")
    private String creatorName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "负责人用户ID列表")
    private List<Long> ownerUserIds;

    @Schema(description = "参与人用户ID列表")
    private List<Long> participantUserIds;

    @Schema(description = "费用标准列表")
    private List<ActivityFeeStandardVO> feeStandards;

}
