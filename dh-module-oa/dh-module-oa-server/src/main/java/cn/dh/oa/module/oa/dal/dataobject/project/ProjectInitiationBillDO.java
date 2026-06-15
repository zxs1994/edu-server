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
     * 项目类型：1研发型 2交付实施型 3工程建造型
     */
    private Integer projectType;
    /**
     * 优先级：1高 2中 3低
     */
    private Integer priority;
    /**
     * 项目分类：1研发项目 2交付项目 3运维项目
     */
    private Integer projectCategory;
    /**
     * 所属项目集ID
     */
    private Long projectSetId;
    /**
     * 所属项目集名称
     */
    private String projectSetName;
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
     * 关联合同ID
     */
    private Long relatedContractId;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 合同名称
     */
    private String contractName;
    /**
     * 项目经理ID
     */
    private Long projectManagerId;
    /**
     * 项目经理名称
     */
    private String projectManagerName;
    /**
     * 对方类型：1CRM客户 2ERP供应商
     */
    private Integer counterpartyType;
    /**
     * 对方单位ID
     */
    private Long counterpartyId;
    /**
     * 对方单位名称
     */
    private String counterpartyName;
    /**
     * 对方联系人
     */
    private String counterpartyContact;
    /**
     * 对方联系电话
     */
    private String counterpartyPhone;

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
