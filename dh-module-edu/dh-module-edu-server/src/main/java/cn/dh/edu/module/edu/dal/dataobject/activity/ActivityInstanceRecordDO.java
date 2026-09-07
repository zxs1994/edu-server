package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 专项活动执行实例 - 活动记录 DO
 */
@TableName("edu_activity_instance_record")
@KeySequence("edu_activity_instance_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInstanceRecordDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long instanceId;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    /** 缺席用户 ID JSON 数组 */
    private String absentUserIds;
    private String summary;
    /** 附件 JSON */
    private String attachments;

}
