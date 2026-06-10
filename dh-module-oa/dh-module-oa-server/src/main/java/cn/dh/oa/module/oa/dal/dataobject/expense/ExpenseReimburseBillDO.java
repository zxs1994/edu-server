package cn.dh.oa.module.oa.dal.dataobject.expense;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 费用报销单 DO
 *
 * @author 鼎衡
 */
@TableName("oa_expense_reimburse_bill")
@KeySequence("oa_expense_reimburse_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseReimburseBillDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 单据编号
     */
    private String billCode;
    /**
     * 流程实例编号
     */
    private String processInstanceId;
    /**
     * 单据状态
     */
    private Integer processStatus;

    // ========== 费用信息 ==========
    /**
     * 费用类型（1办公用品 2交通 3餐饮 4通讯 5差旅 6会议 7招待 8其他）
     */
    private Integer expenseType;
    /**
     * 报销总金额
     */
    private BigDecimal totalAmount;
    /**
     * 费用发生日期
     */
    private LocalDate expenseDate;
    /**
     * 费用说明
     */
    private String expenseDescription;
    /**
     * 支付方式（1银行转账 2现金 3支票）
     */
    private Integer paymentMethod;
    /**
     * 银行账号
     */
    private String bankAccount;
    /**
     * 开户行
     */
    private String bankName;
    /**
     * 是否大额（0否 1是）
     */
    private Integer isLargeAmount;
    /**
     * 大额备注
     */
    private String largeAmountRemark;
    /**
     * 申请事由
     */
    private String cause;

    // ========== 基础字段 ==========
    /**
     * 申请人姓名
     */
    private String creatorName;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 备注
     */
    private String remark;

}
