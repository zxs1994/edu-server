package cn.dh.oa.module.oa.controller.admin.travel.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    // ========== 业务字段 ==========

    @ExcelProperty("目的地")
    @Schema(description = "目的地")
    private String destination;

    @ExcelProperty("出差开始时间")
    @Schema(description = "出差开始时间")
    private LocalDateTime travelStartDate;

    @ExcelProperty("出差结束时间")
    @Schema(description = "出差结束时间")
    private LocalDateTime travelEndDate;

    @ExcelProperty("出差天数")
    @Schema(description = "出差天数")
    private Integer travelDays;

    @ExcelProperty("交通方式")
    @Schema(description = "交通方式：1火车 2飞机 3自驾 4公务用车 5其他")
    private Integer transportType;

    @ExcelProperty("住宿类型")
    @Schema(description = "住宿类型：1酒店 2招待所 3其他")
    private Integer accommodationType;

    @ExcelProperty("预算金额")
    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @ExcelProperty("预算明细")
    @Schema(description = "预算明细")
    private String budgetDetail;

    @ExcelProperty("出差人员")
    @Schema(description = "出差人员")
    private String travelMembers;

    @ExcelProperty("是否出国")
    @Schema(description = "是否出国：0否 1是")
    private Integer isOverseas;

    @ExcelProperty("出国备注")
    @Schema(description = "出国备注")
    private String overseasRemark;

    @ExcelProperty("事由")
    @Schema(description = "事由")
    private String cause;

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

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}
