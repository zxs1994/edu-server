package cn.dh.oa.module.oa.controller.admin.expense.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import cn.dh.oa.module.oa.controller.admin.travel.vo.TravelApplyBillRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import java.util.List;

@Schema(description = "管理后台 - 差旅报销单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ExpenseReimburseBillRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1882")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    @ExcelProperty("流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    @ExcelProperty("单据状态")
    private Integer processStatus;

    @Schema(description = "展示层叠加：会长异议/纠错")
    private Boolean presidentCorrectionDisplay;

    @Schema(description = "列表状态列：仅展示会长异议/纠错（未重新发起）")
    private Boolean presidentCorrectionAwaitingResubmit;

    @Schema(description = "单据类型（1-日常报销 2-差旅报销）", example = "2")
    @ExcelProperty("单据类型")
    private Integer billType;

    @Schema(description = "关联出差单号（多个用逗号分隔）", example = "CLCC202412010001")
    @ExcelProperty("关联出差单号")
    private String travelBillCode;

    @Schema(description = "报销事由（日常报销）", example = "办公用品采购")
    @ExcelProperty("报销事由")
    private String cause;

    @Schema(description = "出差事由（拼接展示）", example = "项目现场实施")
    @ExcelProperty("出差事由")
    private String travelCause;

    @Schema(description = "报销总金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    @ExcelProperty("报销总金额")
    private BigDecimal totalAmount;

    @Schema(description = "支付状态（0未支付 1已支付）", example = "0")
    @ExcelProperty("支付状态")
    private Integer paymentStatus;

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

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

    @Schema(description = "费用明细列表")
    private List<ExpenseReimburseDetailRespVO> details;

    @Schema(description = "关联差旅申请单列表（根据 travelBillCode 查询）")
    private List<TravelApplyBillRespVO> travelBills;

}
