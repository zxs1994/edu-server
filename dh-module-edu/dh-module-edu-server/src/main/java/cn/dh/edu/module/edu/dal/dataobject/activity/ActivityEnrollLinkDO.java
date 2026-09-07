package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 专项活动 H5 报名短链 DO
 */
@TableName("edu_activity_enroll_link")
@KeySequence("edu_activity_enroll_link_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEnrollLinkDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 短码（约 6 位） */
    private String code;
    /** 学生用户 ID */
    private Long userId;
    /** 活动实例 ID */
    private Long instanceId;
    /**
     * 历史字段：短码不过期，新建不再写入；保留兼容旧数据
     */
    private LocalDateTime expireTime;

}
