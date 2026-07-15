package cn.dh.oa.module.oa.controller.admin.travel.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import java.util.List;

@Schema(description = "管理后台 - 差旅申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TravelApplyBillRespVO {

    @Schema(description = "主键")
    private Long id;

    @ExcelProperty("单据编号")
    @Schema(description = "单据编号")
    private String billCode;

    @ExcelProperty("流程实例编号")
    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @ExcelProperty("单据状态")
    @Schema(description = "单据状态")
    private Integer processStatus;

    @Schema(description = "展示层叠加：会长异议/纠错")
    private Boolean presidentCorrectionDisplay;

    @Schema(description = "列表状态列：仅展示会长异议/纠错（未重新发起）")
    private Boolean presidentCorrectionAwaitingResubmit;

    // ========== 业务字段 ==========

    @ExcelProperty("出差事由")
    @Schema(description = "出差事由")
    private String cause;

    @ExcelProperty("开始日期")
    @Schema(description = "出差开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime travelStartDate;

    @ExcelProperty("结束日期")
    @Schema(description = "出差结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime travelEndDate;

    @ExcelProperty("天数")
    @Schema(description = "出差天数（支持1位小数）", example = "3.5")
    private BigDecimal travelDays;

    @ExcelProperty("同行人")
    @Schema(description = "同行人")
    private String companion;

    @ExcelProperty("出行人数")
    @Schema(description = "出行人数（含本人）", example = "2")
    private Integer travelerCount;

    @ExcelProperty("预计费用")
    @Schema(description = "预计费用")
    private BigDecimal estimatedCost;

    @ExcelProperty("报销状态")
    @Schema(description = "报销状态（0未报销 1已报销）")
    private Integer reimbursementStatus;

    @ExcelProperty("申请类型")
    @Schema(description = "申请类型（1国内差旅 2出境差旅）")
    private Integer travelType;

    // ========== 公共字段 ==========

    @ExcelProperty("创建者姓名")
    @Schema(description = "创建者姓名")
    private String creatorName;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "公司编号")
    private Long companyId;

    @ExcelProperty("公司名称")
    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "部门编号")
    private Long deptId;

    @ExcelProperty("部门名称")
    @Schema(description = "部门名称")
    private String deptName;

    @ExcelProperty("创建时间")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "行程明细列表")
    private List<TravelItineraryRespVO> itineraries;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}
