package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 专项活动执行实例 - 费用明细 DO
 */
@TableName("edu_activity_instance_fee_item")
@KeySequence("edu_activity_instance_fee_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInstanceFeeItemDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String feeCode;
    private Long instanceId;
    private Long activityId;
    private Long feeStandardId;
    private String feeType;
    private String feeMode;
    private String currency;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal amount;
    /** 实际金额（付款申请填写；合计按此汇总） */
    private BigDecimal actualAmount;
    private String feeSide;
    private Long payeeUserId;
    private String status;
    private Long paymentRequestId;
    private LocalDateTime generateTime;
    private LocalDateTime payTime;
    private String remark;

}
