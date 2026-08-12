package cn.dh.oa.module.oa.dal.dataobject.reception;

import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 接待申请单 DO
 */
@TableName("oa_reception_apply_bill")
@KeySequence("oa_reception_apply_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceptionApplyBillDO extends BaseDO {

    @TableId
    private Long id;

    private String billCode;

    private String processInstanceId;

    private Integer processStatus;

    /** 申请事由 */
    private String cause;

    /** 就餐时间 */
    private LocalDate diningTime;

    /** 就餐标准 */
    private String diningStandard;

    /** 来宾人数 */
    private Integer guestCount;

    /** 陪同人数 */
    private Integer accompanyCount;

    /** 预估费用 */
    private BigDecimal estimatedCost;

    private String creatorName;

    private Long companyId;

    private String companyName;

    private Long deptId;

    private String deptName;

}
