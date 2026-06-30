package cn.dh.oa.module.oa.controller.admin.incoming.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import java.util.List;

@Schema(description = "管理后台 - 收文办理单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class IncomingDocumentBillRespVO {

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

    @ExcelProperty("公文标题")
    @Schema(description = "公文标题")
    private String docTitle;

    @ExcelProperty("来文字号")
    @Schema(description = "来文字号")
    private String docNumber;

    @ExcelProperty("密级")
    @Schema(description = "密级：0公开 1内部 2机密 3绝密")
    private Integer secrecyLevel;

    @ExcelProperty("收文日期")
    @Schema(description = "收文日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate receiveDate;

    @ExcelProperty("收文类型")
    @Schema(description = "收文类型：1上级文件 2平级文件 3下级文件 4群众来信 5其他")
    private Integer docType;

    @ExcelProperty("紧急程度")
    @Schema(description = "紧急程度：0普通 1紧急 2特急")
    private Integer urgencyLevel;

    @Schema(description = "收文部门编号")
    private Long handlingDeptId;

    @ExcelProperty("收文部门")
    @Schema(description = "收文部门名称")
    private String handlingDeptName;

    @ExcelProperty("主办人")
    @Schema(description = "主办人")
    private String hostPerson;

    @ExcelProperty("领导批示")
    @Schema(description = "领导批示")
    private String leaderInstruction;

    @ExcelProperty("办理结果")
    @Schema(description = "办理结果")
    private String handlingResult;

    @ExcelProperty("办理期限")
    @Schema(description = "办理期限")
    private String handlingDeadline;

    @ExcelProperty("内容摘要")
    @Schema(description = "内容摘要")
    private String contentSummary;

    @ExcelProperty("办理状态")
    @Schema(description = "办理状态：0待办理 1办理中 2已办结")
    private Integer handlingStatus;

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
