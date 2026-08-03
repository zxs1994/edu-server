package cn.dh.oa.module.oa.dal.dataobject.expense;

import lombok.*;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 差旅报销单 DO
 *
 * @author 鼎衡
 */
@TableName("oa_expense_reimburse_bill")
@KeySequence("oa_expense_reimburse_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseReimburseBillDO extends BaseDO {

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
    /**
     * 单据类型（1-日常报销 2-差旅报销）
     */
    private Integer billType;

    // ========== 差旅关联信息 ==========
    /**
     * 关联出差单号（多个用逗号分隔）
     */
    private String travelBillCode;
    /**
     * 报销事由（日常报销必填）
     */
    private String cause;

    // ========== 报销信息 ==========
    /**
     * 报销总金额
     */
    private BigDecimal totalAmount;
    /**
     * 人数（含本人），补贴领取人数（差旅报销）
     */
    private Integer travelerCount;
    /**
     * 交通补贴领取天数
     */
    private BigDecimal trafficSubsidyDays;
    /**
     * 交通补贴领取人数
     */
    private Integer trafficSubsidyPeople;
    /**
     * 伙食补贴领取天数
     */
    private BigDecimal mealSubsidyDays;
    /**
     * 伙食补贴领取人数
     */
    private Integer mealSubsidyPeople;
    /**
     * 支付状态（0未支付 1已支付）
     */
    private Integer paymentStatus;

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
