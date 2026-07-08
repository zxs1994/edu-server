package cn.dh.oa.module.oa.controller.admin.expensepayment.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 费用支出申请 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExpensePaymentBillRespVO {

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

    @Schema(description = "支出类型（1对公 2对私）")
    @ExcelProperty("支出类型")
    private Integer paymentType;

    @Schema(description = "费用归属项目")
    private String projectCategory;

    @Schema(description = "申请日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate applyDate;

    @Schema(description = "紧急程度")
    private Integer urgencyLevel;

    @Schema(description = "申请说明")
    @ExcelProperty("申请说明")
    private String cause;

    @Schema(description = "合计金额")
    @ExcelProperty("合计金额")
    private BigDecimal totalAmount;

    @Schema(description = "支付状态")
    private Integer paymentStatus;

    @Schema(description = "收款单位名称")
    private String payeeCompanyName;

    @Schema(description = "报销人姓名")
    private String payeePersonName;

    @Schema(description = "收款开户行")
    private String payeeBank;

    @Schema(description = "收款账号")
    private String payeeAccount;

    @Schema(description = "合同编号")
    private String contractCode;

    @Schema(description = "付款方式")
    private Integer paymentMethod;

    @Schema(description = "是否预付")
    private Integer isPrepay;

    @Schema(description = "是否个人垫付")
    private Integer isPersonalAdvance;

    @Schema(description = "公务卡号")
    private String officialCardNo;

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

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "费用明细列表")
    private List<ExpensePaymentDetailRespVO> details;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}
