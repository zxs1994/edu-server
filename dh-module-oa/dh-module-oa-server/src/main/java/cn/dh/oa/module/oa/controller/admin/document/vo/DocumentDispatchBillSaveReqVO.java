package cn.dh.oa.module.oa.controller.admin.document.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentSaveReqVO;

@Schema(description = "管理后台 - 公文发文单新增/修改 Request VO")
@Data
public class DocumentDispatchBillSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "单据编号", example = "GW202412010001")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "公文标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "关于XXX的通知")
    @NotEmpty(message = "公文标题不能为空")
    private String docTitle;

    @Schema(description = "公文编号", example = "DH-2024-001")
    private String docNumber;

    @Schema(description = "密级（0公开 1内部 2机密 3绝密）", example = "0")
    private Integer secrecyLevel;

    @Schema(description = "套红模板ID", example = "1")
    private Long templateId;

    @Schema(description = "发文字号前缀（如：无办发）", example = "无办发")
    private String docNumberPrefix;

    @Schema(description = "发文字号年份（如：2026）", example = "2026")
    private Integer docNumberYear;

    @Schema(description = "发文字号序号", example = "1")
    private Integer docNumberSerial;

    @Schema(description = "紧急程度（0普通 1急件 2特急）", example = "0")
    private Integer urgencyLevel;

    @Schema(description = "公开类别（0主动公开 1依申请公开 2不公开）", example = "0")
    private Integer disclosureCategory;

    @Schema(description = "发文日期")
    private java.time.LocalDate issueDate;

    @Schema(description = "主送部门（逗号分隔）")
    private String mainRecipients;

    @Schema(description = "抄送部门（逗号分隔）")
    private String ccDepartments;

    @Schema(description = "签发人")
    private String signer;

    @Schema(description = "公文内容")
    private String docContent;

    @Schema(description = "申请人姓名", example = "张三")
    private String creatorName;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "鼎衡科技")
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "部门ID不能为空")
    private Long deptId;

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "行政部")
    @NotEmpty(message = "部门名称不能为空")
    private String deptName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
