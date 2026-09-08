package cn.dh.edu.module.edu.dal.dataobject.reward;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 预算配置年度时段预算 DO
 */
@TableName("edu_reward_budget_period")
@KeySequence("edu_reward_budget_period_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardBudgetPeriodDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 年度预算 ID */
    private Long yearId;
    /** 时段名称 */
    private String name;
    /** 排序号 */
    private Integer periodNo;
    /** 开始日期（含） */
    private LocalDate startDate;
    /** 结束日期（含） */
    private LocalDate endDate;
    /** 时段预算金额 */
    private BigDecimal budgetAmount;

}
