package cn.dh.oa.module.oa.controller.admin.reception.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 接待申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ReceptionApplyBillRespVO {

    @Schema(description = "ID")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "单据编号")
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态")
    @ExcelProperty("单据状态")
    private Integer processStatus;

    @Schema(description = "展示层叠加：会长异议/纠错")
    private Boolean presidentCorrectionDisplay;

    @Schema(description = "列表状态列：仅展示会长异议/纠错（未重新发起）")
    private Boolean presidentCorrectionAwaitingResubmit;

    @Schema(description = "申请事由")
    @ExcelProperty("申请事由")
    private String cause;

    @Schema(description = "就餐时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty("就餐时间")
    private LocalDateTime diningTime;

    @Schema(description = "就餐标准")
    @ExcelProperty("就餐标准")
    private String diningStandard;

    @Schema(description = "来宾人数")
    @ExcelProperty("来宾人数")
    private Integer guestCount;

    @Schema(description = "陪同人数")
    @ExcelProperty("陪同人数")
    private Integer accompanyCount;

    @Schema(description = "预估费用")
    @ExcelProperty("预估费用")
    private BigDecimal estimatedCost;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "申请人姓名")
    @ExcelProperty("申请人")
    private String creatorName;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门名称")
    @ExcelProperty("申请部门")
    private String deptName;

    @Schema(description = "公司ID")
    private Long companyId;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}
