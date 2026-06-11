package cn.dh.oa.module.oa.dal.dataobject.contract;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 收付款计划 DO
 *
 * @author 鼎衡
 */
@TableName("oa_contract_payment_plan")
@KeySequence("oa_contract_payment_plan_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractPaymentPlanDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 合同审批单ID
     */
    private Long billId;
    /**
     * 期次
     */
    private Integer period;
    /**
     * 计划金额
     */
    private BigDecimal planAmount;
    /**
     * 计划日期
     */
    private LocalDate planDate;
    /**
     * 实际金额
     */
    private BigDecimal actualAmount;
    /**
     * 实际日期
     */
    private LocalDate actualDate;
    /**
     * 状态（0待收付 1已收付 2已逾期）
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer sortOrder;

}
