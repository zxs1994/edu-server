package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 专项活动执行实例 DO
 *
 * @author 鼎衡
 */
@TableName("edu_activity_instance")
@KeySequence("edu_activity_instance_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityInstanceDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String instanceCode;
    private Long activityId;
    private Integer periodNo;
    private LocalDateTime plannedDate;
    private LocalDateTime actualDate;
    private String status;
    private Integer enrollCount;
    private Integer enrollLimit;
    private Integer attendanceCount;

}
