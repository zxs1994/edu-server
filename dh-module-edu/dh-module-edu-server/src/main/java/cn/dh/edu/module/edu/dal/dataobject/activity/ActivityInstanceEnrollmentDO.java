package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 专项活动执行实例 - 报名记录 DO
 */
@TableName("edu_activity_instance_enrollment")
@KeySequence("edu_activity_instance_enrollment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInstanceEnrollmentDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 执行实例 ID */
    private Long instanceId;
    /** 报名用户 ID（system_users.id） */
    private Long userId;
    /** 报名时间 */
    private LocalDateTime enrollTime;

}
