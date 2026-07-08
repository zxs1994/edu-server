package cn.dh.oa.module.oa.controller.admin.expensepayment.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentSaveReqVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 费用支出申请 Save VO")
@Data
public class ExpensePaymentBillSaveReqVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态")
    private Integer processStatus;

    @Schema(description = "支出类型（1对公 2对私）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "支出类型不能为空")
    private Integer paymentType;

    @Schema(description = "费用归属项目")
    private String projectCategory;

    @Schema(description = "申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate applyDate;

    @Schema(description = "紧急程度")
    private Integer urgencyLevel;

    @Schema(description = "申请说明")
    private String cause;

    @Schema(description = "合计金额")
    private BigDecimal totalAmount;

    @Schema(description = "支付状态")
    private Integer paymentStatus;

    @Schema(description = "收款单位名称（对公）")
    private String payeeCompanyName;

    @Schema(description = "报销人姓名（对私）")
    private String payeePersonName;

    @Schema(description = "收款开户行")
    private String payeeBank;

    @Schema(description = "收款账号")
    private String payeeAccount;

    @Schema(description = "合同编号")
    private String contractCode;

    @Schema(description = "付款方式")
    private Integer paymentMethod;

    @Schema(description = "是否预付（0否 1是）")
    private Integer isPrepay;

    @Schema(description = "是否个人垫付（0否 1是）")
    private Integer isPersonalAdvance;

    @Schema(description = "公务卡号")
    private String officialCardNo;

    @Schema(description = "申请人姓名")
    private String creatorName;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "公司名称不能为空")
    private String companyName;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "费用明细列表")
    private List<ExpensePaymentDetailSaveReqVO> details;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
