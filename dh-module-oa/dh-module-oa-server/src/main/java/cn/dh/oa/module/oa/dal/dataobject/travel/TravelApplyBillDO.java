package cn.dh.oa.module.oa.dal.dataobject.travel;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

@TableName("oa_travel_apply_bill")
@KeySequence("oa_travel_apply_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelApplyBillDO extends BaseDO {

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
     * 出差事由
     */
    private String cause;
    /**
     * 出差开始时间
     */
    private LocalDate travelStartDate;
    /**
     * 出差结束时间
     */
    private LocalDate travelEndDate;
    /**
     * 出差天数（支持1位小数）
     */
    private BigDecimal travelDays;
    /**
     * 同行人
     */
    private String companion;
    /**
     * 出行人数（含本人）
     */
    private Integer travelerCount;
    /**
     * 预计费用
     */
    private BigDecimal estimatedCost;
    /**
     * 报销状态（0未报销 1已报销）
     */
    private Integer reimbursementStatus;

    /**
     * 关联的差旅报销单ID（被报销单保存时锁定，删除报销单或解除关联时释放）
     */
    private Long linkedExpenseBillId;

    /**
     * 申请类型（1国内差旅 2出境差旅）
     */
    private Integer travelType;

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
