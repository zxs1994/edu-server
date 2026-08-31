package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 专项活动费用标准 DO
 *
 * @author 鼎衡
 */
@TableName("edu_activity_fee_standard")
@KeySequence("edu_activity_fee_standard_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFeeStandardDO extends BaseDO {

    @TableId
    private Long id;
    private Long activityId;
    private String feeType;
    private String feeMode;
    private String currency;
    private BigDecimal amount;
    private Long payeeUserId;
    private String feeSide;
    private String remark;

}
