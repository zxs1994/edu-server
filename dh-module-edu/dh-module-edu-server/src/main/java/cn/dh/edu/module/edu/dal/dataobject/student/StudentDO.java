package cn.dh.edu.module.edu.dal.dataobject.student;

import cn.dh.edu.framework.mybatis.core.dataobject.BaseDO;
import cn.dh.edu.module.edu.enums.StudentSchoolStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 学生档案 DO
 *
 * @author 鼎衡
 */
@TableName("edu_student")
@KeySequence("edu_student_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 性别（1:男 2:女）
     */
    private Integer sex;
    /**
     * 出生日期
     */
    private LocalDate birthday;
    /**
     * 学号
     */
    private String studentNo;
    /**
     * 用户账号（登录用户名）
     */
    private String username;
    /**
     * 入学年份
     */
    private Integer enrollYear;
    /**
     * 所属院系
     */
    private String college;
    /**
     * 专业
     */
    private String major;
    /**
     * 班级
     */
    private String className;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 在校状态
     *
     * 枚举 {@link StudentSchoolStatusEnum}
     */
    private Integer schoolStatus;
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
