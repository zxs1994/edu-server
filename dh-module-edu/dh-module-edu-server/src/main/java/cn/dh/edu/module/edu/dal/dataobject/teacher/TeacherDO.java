package cn.dh.edu.module.edu.dal.dataobject.teacher;

import cn.dh.edu.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 教培档案 DO
 *
 * @author 鼎衡
 */
@TableName("edu_teacher")
@KeySequence("edu_teacher_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDO extends BaseDO {

    @TableId
    private Long id;
    /**
     * 用户账号
     */
    private String username;
    /**
     * 姓名
     */
    private String name;
    /**
     * 性别（1:男 2:女）
     */
    private Integer sex;
    /**
     * 职称/职级（字典 edu_teacher_title）
     */
    private String title;
    /**
     * 联系方式
     */
    private String mobile;
    /**
     * 报酬/奖励标准（金额；选项来自字典 edu_teacher_reward）
     * ALWAYS：允许更新为 null（清空）
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private BigDecimal rewardStandard;
    /**
     * 关联用户 ID
     */
    private Long userId;
    /**
     * 是否已生成用户
     */
    private Boolean userGenerated;
    /**
     * 备注
     */
    private String remark;

}
