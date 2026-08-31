package cn.dh.edu.module.edu.dal.dataobject.activity;

import cn.dh.edu.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 专项活动 DO
 *
 * @author 鼎衡
 */
@TableName("edu_activity")
@KeySequence("edu_activity_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDO extends BaseDO {

    @TableId
    private Long id;
    private String billCode;
    private String processInstanceId;
    private Integer processStatus;
    private String name;
    private String activityType;
    private String activitySubtype;
    private String content;
    private String cycleType;
    private LocalDate startDate;
    private LocalDateTime enrollStartTime;
    private LocalDateTime enrollEndTime;
    private BigDecimal budgetAmount;
    private Long deptId;
    private String deptName;
    private Long companyId;
    private String companyName;
    private String creatorName;
    private String remark;

}
