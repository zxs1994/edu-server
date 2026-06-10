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

    @Schema(description = "公文类型（1通知 2公告 3报告 4请示 5批复 6函 7纪要 8其他）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "公文类型不能为空")
    private Integer docType;

    @Schema(description = "紧急程度（0普通 1紧急 2特急）", example = "0")
    private Integer urgencyLevel;

    @Schema(description = "公文内容")
    private String docContent;

    @Schema(description = "收文人")
    private String recipients;

    @Schema(description = "抄送人")
    private String ccList;

    @Schema(description = "是否重要（0否 1是）", example = "0")
    private Integer isImportant;

    @Schema(description = "申请事由", requiredMode = Schema.RequiredMode.REQUIRED, example = "发布通知")
    @NotEmpty(message = "申请事由不能为空")
    private String cause;

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
