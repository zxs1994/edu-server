package cn.dh.edu.module.edu.dal.dataobject.reward;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 专项活动奖金池 DO（系统单池）
 */
@TableName("edu_reward_pool")
@KeySequence("edu_reward_pool_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardPoolDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 池名称 */
    private String name;
    /** 总预算 */
    private BigDecimal totalBudget;
    /** 已冻结 */
    private BigDecimal frozenAmount;
    /** 已实发 */
    private BigDecimal paidAmount;
    /** 状态：0 启用 / 1 停用 */
    private Integer status;
    /** 备注 */
    private String remark;

}
