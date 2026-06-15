package cn.dh.oa.module.oa.controller.admin.incoming.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentSaveReqVO;

@Schema(description = "管理后台 - 收文办理单新增/修改 Request VO")
@Data
public class IncomingDocumentBillSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态")
    private Integer processStatus;

    // ========== 业务字段 ==========

    @Schema(description = "公文标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "公文标题不能为空")
    private String docTitle;

    @Schema(description = "来文字号")
    private String docNumber;

    @Schema(description = "密级：0公开 1内部 2机密 3绝密")
    private Integer secrecyLevel;

    @Schema(description = "收文日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "收文日期不能为空")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate receiveDate;

    @Schema(description = "收文类型：1上级文件 2平级文件 3下级文件 4群众来信 5其他", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "收文类型不能为空")
    private Integer docType;

    @Schema(description = "紧急程度：0普通 1紧急 2特急")
    private Integer urgencyLevel;

    @Schema(description = "收文部门编号")
    private Long handlingDeptId;

    @Schema(description = "收文部门名称")
    private String handlingDeptName;

    @Schema(description = "主办人")
    private String hostPerson;

    @Schema(description = "领导批示")
    private String leaderInstruction;

    @Schema(description = "办理结果")
    private String handlingResult;

    @Schema(description = "办理期限")
    private String handlingDeadline;

    @Schema(description = "内容摘要")
    private String contentSummary;

    @Schema(description = "办理状态：0待办理 1办理中 2已办结")
    private Integer handlingStatus;

    // ========== 公共字段 ==========

    @Schema(description = "创建者姓名")
    private String creatorName;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "公司编号")
    private Long companyId;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "部门编号")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
