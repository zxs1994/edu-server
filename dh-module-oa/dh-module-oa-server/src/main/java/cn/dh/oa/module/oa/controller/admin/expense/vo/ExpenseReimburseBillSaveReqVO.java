package cn.dh.oa.module.oa.controller.admin.expense.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentSaveReqVO;

@Schema(description = "管理后台 - 差旅报销单新增/修改 Request VO")
@Data
public class ExpenseReimburseBillSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "单据编号", example = "CLBX202412010001")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "单据类型（1-日常报销 2-差旅报销）", example = "2")
    private Integer billType;

    @Schema(description = "关联出差单号（多个用逗号分隔）", example = "CLCC202412010001,CLCC202412010002")
    private String travelBillCode;

    @Schema(description = "报销事由（日常报销必填）", example = "办公用品采购")
    private String cause;

    @Schema(description = "出差事由（自动拼接，仅用于回显）", example = "项目现场实施")
    private String travelCause;

    @Schema(description = "报销总金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    @NotNull(message = "报销总金额不能为空")
    private BigDecimal totalAmount;

    @Schema(description = "支付状态（0未支付 1已支付）", example = "0")
    private Integer paymentStatus;

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

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "技术部")
    @NotEmpty(message = "部门名称不能为空")
    private String deptName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "费用明细列表")
    private List<ExpenseReimburseDetailSaveReqVO> details;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
