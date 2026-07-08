package cn.dh.oa.module.oa.dal.dataobject.expensepayment;

import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("oa_expense_payment_detail")
@KeySequence("oa_expense_payment_detail_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpensePaymentDetailDO extends BaseDO {

    @TableId
    private Long id;
    private Long billId;
    private String expenseType;
    private LocalDate expenseDate;
    private String cause;
    private BigDecimal amount;
    private String remark;
    private Integer sortOrder;

}
