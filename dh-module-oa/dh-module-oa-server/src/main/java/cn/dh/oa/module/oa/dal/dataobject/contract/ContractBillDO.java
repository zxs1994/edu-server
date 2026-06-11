package cn.dh.oa.module.oa.dal.dataobject.contract;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 合同审批单 DO
 *
 * @author 鼎衡
 */
@TableName("oa_contract_bill")
@KeySequence("oa_contract_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractBillDO extends BaseDO {

    /**
     * ID
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

    // ========== 合同信息 ==========
    /**
     * 合同标题
     */
    private String contractTitle;
    /**
     * 合同编号
     */
    private String contractCode;
    /**
     * 合同类型（1采购 2销售 3服务 4合作 5其他）
     */
    private Integer contractType;
    /**
     * 合同性质
     */
    private String contractNature;
    /**
     * 合同分类
     */
    private String contractCategory;
    /**
     * 我方主体
     */
    private String ourParty;
    /**
     * 我方角色（1甲方 2乙方）
     */
    private Integer ourRole;
    /**
     * 对方类型（1CRM客户 2ERP供应商）
     */
    private Integer counterpartyType;
    /**
     * 合同对方（对方单位）
     */
    private String contractParty;
    /**
     * 对方联系人
     */
    private String counterpartyContact;
    /**
     * 对方电话
     */
    private String counterpartyPhone;
    /**
     * 合同金额
     */
    private BigDecimal contractAmount;
    /**
     * 币种（默认CNY）
     */
    private String currency;
    /**
     * 签订日期
     */
    private LocalDate signDate;
    /**
     * 生效日期
     */
    private LocalDate effectiveDate;
    /**
     * 截止日期
     */
    private LocalDate expiryDate;
    /**
     * 负责人
     */
    private String responsiblePerson;
    /**
     * 合同开始日期
     */
    private LocalDate contractStartDate;
    /**
     * 合同结束日期
     */
    private LocalDate contractEndDate;
    /**
     * 合同内容
     */
    private String contractContent;
    /**
     * 是否重大合同（0否 1是）
     */
    private Integer isMajor;
    /**
     * 重大合同备注
     */
    private String majorRemark;
    /**
     * 申请事由
     */
    private String cause;

    // ========== 基础字段 ==========
    /**
     * 申请人姓名
     */
    private String creatorName;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 部门ID
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
