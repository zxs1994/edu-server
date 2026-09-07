package cn.dh.edu.module.edu.dal.dataobject.reward;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 专项活动奖金池流水 DO
 */
@TableName("edu_reward_pool_txn")
@KeySequence("edu_reward_pool_txn_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardPoolTxnDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long poolId;
    /** 流水类型：RECHARGE / ADJUST_DOWN / FREEZE / UNFREEZE / PAY */
    private String txnType;
    /** 变动金额（绝对值） */
    private BigDecimal amount;
    private BigDecimal totalAfter;
    private BigDecimal frozenAfter;
    private BigDecimal paidAfter;
    private String bizType;
    private Long bizId;
    private String remark;

}
