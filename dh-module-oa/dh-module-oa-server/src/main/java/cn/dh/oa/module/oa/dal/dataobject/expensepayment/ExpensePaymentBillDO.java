package cn.dh.oa.module.oa.dal.dataobject.expensepayment;

import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("oa_expense_payment_bill")
@KeySequence("oa_expense_payment_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpensePaymentBillDO extends BaseDO {

    @TableId
    private Long id;
    private String billCode;
    private String processInstanceId;
    private Integer processStatus;
    private Integer paymentType;
    private String projectCategory;
    private LocalDate applyDate;
    private Integer urgencyLevel;
    private String cause;
    private BigDecimal totalAmount;
    private Integer paymentStatus;
    private String payeeCompanyName;
    private String payeePersonName;
    private String payeeBank;
    private String payeeAccount;
    private String contractCode;
    private Integer paymentMethod;
    private Integer isPrepay;
    private Integer isPersonalAdvance;
    private String officialCardNo;
    private String creatorName;
    private Long companyId;
    private String companyName;
    private Long deptId;
    private String deptName;
    private String remark;

}
