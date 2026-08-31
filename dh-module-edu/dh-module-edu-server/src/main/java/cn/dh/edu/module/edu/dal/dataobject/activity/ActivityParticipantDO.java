package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 专项活动参与人 DO
 */
@TableName("edu_activity_participant")
@KeySequence("edu_activity_participant_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityParticipantDO extends BaseDO {

    @TableId
    private Long id;
    /** 活动 ID */
    private Long activityId;
    /** 参与人用户 ID（system_users.id） */
    private Long userId;

}
