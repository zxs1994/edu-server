package cn.dh.oa.module.oa.controller.admin.expense.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import java.util.List;

@Schema(description = "管理后台 - 费用报销单 Response VO")
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

    @Schema(description = "费用类型（1办公用品 2交通 3餐饮 4通讯 5差旅 6会议 7招待 8其他）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("费用类型")
    private Integer expenseType;

    @Schema(description = "报销总金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    @ExcelProperty("报销总金额")
    private BigDecimal totalAmount;

    @Schema(description = "费用发生日期")
    @ExcelProperty("费用发生日期")
    private LocalDate expenseDate;

    @Schema(description = "费用说明")
    @ExcelProperty("费用说明")
    private String expenseDescription;

    @Schema(description = "支付方式（1银行转账 2现金 3支票）", example = "1")
    @ExcelProperty("支付方式")
    private Integer paymentMethod;

    @Schema(description = "银行账号")
    @ExcelProperty("银行账号")
    private String bankAccount;

    @Schema(description = "开户行")
    @ExcelProperty("开户行")
    private String bankName;

    @Schema(description = "是否大额（0否 1是）", example = "0")
    @ExcelProperty("是否大额")
    private Integer isLargeAmount;

    @Schema(description = "大额备注")
    @ExcelProperty("大额备注")
    private String largeAmountRemark;

    @Schema(description = "申请事由", requiredMode = Schema.RequiredMode.REQUIRED, example = "差旅费报销")
    @ExcelProperty("申请事由")
    private String cause;

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

}
