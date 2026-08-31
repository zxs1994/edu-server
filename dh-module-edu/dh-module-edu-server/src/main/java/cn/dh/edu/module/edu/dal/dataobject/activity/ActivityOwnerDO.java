package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 专项活动负责人 DO
 *
 * @author 鼎衡
 */
@TableName("edu_activity_owner")
@KeySequence("edu_activity_owner_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOwnerDO extends BaseDO {

    @TableId
    private Long id;
    private Long activityId;
    private Long userId;

}
