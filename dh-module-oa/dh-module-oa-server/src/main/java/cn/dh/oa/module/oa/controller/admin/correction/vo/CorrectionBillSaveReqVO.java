package cn.dh.oa.module.oa.controller.admin.correction.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentSaveReqVO;

@Schema(description = "管理后台 - 纠错申请单新增/修改 Request VO")
@Data
public class CorrectionBillSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "单据编号", example = "JC202412010001")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "原单据类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "103")
    @NotEmpty(message = "原单据类型不能为空")
    private String sourceBillType;

    @Schema(description = "原单据ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "原单据ID不能为空")
    private Long sourceBillId;

    @Schema(description = "原单据编号", example = "YZ202412010001")
    private String sourceBillCode;

    @Schema(description = "原流程实例ID")
    private String sourceProcessInstanceId;

    @Schema(description = "原单据标题", example = "用印申请单")
    private String sourceBillTitle;

    @Schema(description = "纠错理由", requiredMode = Schema.RequiredMode.REQUIRED, example = "审批流程有误，需要重新审核")
    @NotEmpty(message = "纠错理由不能为空")
    private String correctionReason;

    @Schema(description = "冻结状态 0未冻结 1已冻结 2已解冻", example = "0")
    private Integer freezeStatus;

    @Schema(description = "纠错状态 0待处理 1重审中 2已完成 3已撤销", example = "0")
    private Integer correctionStatus;

    @Schema(description = "是否理事会决议 0否 1是", example = "0")
    private Integer councilDecision;

    @Schema(description = "理事会决议文件")
    private String councilDecisionFile;

    @Schema(description = "重审流程实例ID")
    private String newProcessInstanceId;

    @Schema(description = "纠错处理结果")
    private String correctionResult;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建者姓名", example = "张三")
    private String creatorName;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "鼎衡科技")
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @Schema(description = "部门ID", example = "1")
    private Long deptId;

    @Schema(description = "部门名称", example = "技术部")
    private String deptName;

    @Schema(description = "备注", example = "需要紧急处理")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
