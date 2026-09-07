package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 专项活动执行实例 - 班务评价 DO
 */
@TableName("edu_activity_instance_admin_feedback")
@KeySequence("edu_activity_instance_admin_feedback_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInstanceAdminFeedbackDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long instanceId;
    private Long submitterUserId;
    private Integer qualityScore;
    private String closingOpinion;
    private LocalDateTime submitTime;

}
