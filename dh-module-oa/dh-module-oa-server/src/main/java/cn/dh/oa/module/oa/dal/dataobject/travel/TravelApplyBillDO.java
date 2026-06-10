package cn.dh.oa.module.oa.dal.dataobject.travel;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
     * 目的地
     */
    private String destination;
    /**
     * 出差开始时间
     */
    private LocalDateTime travelStartDate;
    /**
     * 出差结束时间
     */
    private LocalDateTime travelEndDate;
    /**
     * 出差天数
     */
    private Integer travelDays;
    /**
     * 交通方式：1火车 2飞机 3自驾 4公务用车 5其他
     */
    private Integer transportType;
    /**
     * 住宿类型：1酒店 2招待所 3其他
     */
    private Integer accommodationType;
    /**
     * 预算金额
     */
    private BigDecimal budgetAmount;
    /**
     * 预算明细
     */
    private String budgetDetail;
    /**
     * 出差人员
     */
    private String travelMembers;
    /**
     * 是否出国：0否 1是
     */
    private Integer isOverseas;
    /**
     * 出国备注
     */
    private String overseasRemark;
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
