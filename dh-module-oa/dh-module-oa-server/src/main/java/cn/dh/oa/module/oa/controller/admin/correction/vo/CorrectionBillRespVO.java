package cn.dh.oa.module.oa.controller.admin.correction.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import java.util.List;

@Schema(description = "管理后台 - 纠错申请单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CorrectionBillRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "流程实例编号", example = "16629")
    @ExcelProperty("流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    @ExcelProperty("单据状态")
    private Integer processStatus;

    @Schema(description = "原单据类型", example = "103")
    @ExcelProperty("原单据类型")
    private String sourceBillType;

    @Schema(description = "原单据ID", example = "1")
    @ExcelProperty("原单据ID")
    private Long sourceBillId;

    @Schema(description = "原单据编号", example = "YZ202412010001")
    @ExcelProperty("原单据编号")
    private String sourceBillCode;

    @Schema(description = "原流程实例ID")
    @ExcelProperty("原流程实例ID")
    private String sourceProcessInstanceId;

    @Schema(description = "原单据标题", example = "用印申请单")
    @ExcelProperty("原单据标题")
    private String sourceBillTitle;

    @Schema(description = "纠错理由", example = "审批流程有误，需要重新审核")
    @ExcelProperty("纠错理由")
    private String correctionReason;

    @Schema(description = "冻结状态 0未冻结 1已冻结 2已解冻", example = "0")
    @ExcelProperty("冻结状态")
    private Integer freezeStatus;

    @Schema(description = "纠错状态 0待处理 1重审中 2已完成 3已撤销", example = "0")
    @ExcelProperty("纠错状态")
    private Integer correctionStatus;

    @Schema(description = "是否理事会决议 0否 1是", example = "0")
    @ExcelProperty("是否理事会决议")
    private Integer councilDecision;

    @Schema(description = "理事会决议文件")
    @ExcelProperty("理事会决议文件")
    private String councilDecisionFile;

    @Schema(description = "重审流程实例ID")
    @ExcelProperty("重审流程实例ID")
    private String newProcessInstanceId;

    @Schema(description = "纠错处理结果")
    @ExcelProperty("纠错处理结果")
    private String correctionResult;

    @Schema(description = "创建者", example = "1")
    @ExcelProperty("创建者")
    private String creator;

    @Schema(description = "创建者姓名", example = "张三")
    @ExcelProperty("创建者姓名")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "部门ID", example = "1")
    @ExcelProperty("部门ID")
    private Long deptId;

    @Schema(description = "部门名称", example = "技术部")
    @ExcelProperty("部门名称")
    private String deptName;

    @Schema(description = "公司ID", example = "1")
    @ExcelProperty("公司ID")
    private Long companyId;

    @Schema(description = "公司名称", example = "鼎衡科技")
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "备注", example = "需要紧急处理")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}
