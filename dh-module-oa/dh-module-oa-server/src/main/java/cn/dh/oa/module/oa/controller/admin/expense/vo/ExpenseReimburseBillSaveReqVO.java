package cn.dh.oa.module.oa.controller.admin.expense.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentSaveReqVO;

@Schema(description = "管理后台 - 费用报销单新增/修改 Request VO")
@Data
public class ExpenseReimburseBillSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "单据编号", example = "BX202412010001")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "1")
    private Integer processStatus;

    @Schema(description = "费用类型（1办公用品 2交通 3餐饮 4通讯 5差旅 6会议 7招待 8其他）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "费用类型不能为空")
    private Integer expenseType;

    @Schema(description = "报销总金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000.00")
    @NotNull(message = "报销总金额不能为空")
    private BigDecimal totalAmount;

    @Schema(description = "费用发生日期")
    private LocalDate expenseDate;

    @Schema(description = "费用说明")
    private String expenseDescription;

    @Schema(description = "支付方式（1银行转账 2现金 3支票）", example = "1")
    private Integer paymentMethod;

    @Schema(description = "银行账号")
    private String bankAccount;

    @Schema(description = "开户行")
    private String bankName;

    @Schema(description = "是否大额（0否 1是）", example = "0")
    private Integer isLargeAmount;

    @Schema(description = "大额备注")
    private String largeAmountRemark;

    @Schema(description = "申请事由", requiredMode = Schema.RequiredMode.REQUIRED, example = "差旅费报销")
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

    @Schema(description = "部门名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "技术部")
    @NotEmpty(message = "部门名称不能为空")
    private String deptName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
