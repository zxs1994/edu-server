package cn.dh.edu.module.edu.dal.dataobject.reward;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 专项活动年度预算 DO
 */
@TableName("edu_reward_budget_year")
@KeySequence("edu_reward_budget_year_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardBudgetYearDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 预算年度 */
    private Integer budgetYear;
    /** 时段模式：QUARTER / MONTH / CUSTOM */
    private String periodMode;
    /** 年度预算合计 */
    private BigDecimal totalBudget;
    /** 备注 */
    private String remark;

}
