package cn.dh.oa.module.oa.dal.dataobject.project;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

@TableName("oa_project_initiation_bill")
@KeySequence("oa_project_initiation_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInitiationBillDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 单据编号
     */
    private String billCode;
    /**
     * 流程实例编号
     */
    private String processInstanceId;
    /**
     * 单据状态
     */
    private Integer processStatus;

    // ========== 业务字段 ==========

    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目类型：1活动 2项目 3课题 4其他
     */
    private Integer projectType;
    /**
     * 项目描述
     */
    private String projectDescription;
    /**
     * 预算金额
     */
    private BigDecimal budgetAmount;
    /**
     * 开始日期
     */
    private LocalDate startDate;
    /**
     * 结束日期
     */
    private LocalDate endDate;
    /**
     * 预期成果
     */
    private String expectedOutcome;
    /**
     * 是否重大：0否 1是
     */
    private Integer isMajor;
    /**
     * 重大备注
     */
    private String majorRemark;
    /**
     * 事由
     */
    private String cause;

    // ========== 公共字段 ==========

    /**
     * 创建者姓名
     */
    private String creatorName;
    /**
     * 公司编号
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 部门编号
     */
    private Long deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 备注
     */
    private String remark;

}
