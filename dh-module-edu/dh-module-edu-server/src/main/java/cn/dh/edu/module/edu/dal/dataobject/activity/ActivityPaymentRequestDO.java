package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 专项活动付款申请 DO
 */
@TableName(value = "edu_activity_payment_request", autoResultMap = true)
@KeySequence("edu_activity_payment_request_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityPaymentRequestDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String billCode;
    private Long activityId;
    /** 活动实例 ID（一单对应一个实例） */
    private Long instanceId;
    private String title;
    private BigDecimal totalAmount;
    private String currency;
    private String processInstanceId;
    private Integer processStatus;
    private Long applicantUserId;
    private String remark;
    /**
     * 费用明细 ID 快照（保存/提交时写入；驳回后再申请改挂明细时，历史单仍靠此回显）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> feeItemIds;

}
