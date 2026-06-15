package cn.dh.oa.module.oa.dal.dataobject.expense;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 差旅报销明细 DO
 *
 * @author 鼎衡
 */
@TableName("oa_expense_reimburse_detail")
@KeySequence("oa_expense_reimburse_detail_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseReimburseDetailDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 报销单ID
     */
    private Long billId;
    /**
     * 费用类型（交通费/住宿费等）
     */
    private String expenseType;
    /**
     * 发生日期
     */
    private LocalDate expenseDate;
    /**
     * 出发地
     */
    private String departure;
    /**
     * 到达地
     */
    private String destination;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 费用说明
     */
    private String description;
    /**
     * 排序
     */
    private Integer sortOrder;

}
