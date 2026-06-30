package cn.dh.oa.module.oa.dal.dataobject.correction;

import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 业务单据纠错/冻结状态 DO
 */
@TableName("oa_bill_correction_state")
@KeySequence("oa_bill_correction_state_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillCorrectionStateDO extends BaseDO {

    @TableId
    private Long id;

    /** 单据类型（流程定义 Key） */
    private String billType;

    /** 单据 ID */
    private Long billId;

    /** 冻结状态 0正常 1已冻结 */
    private Integer freezeStatus;

    /** 纠错状态 */
    private Integer correctionStatus;

    /** 纠错前流程实例 ID */
    private String lastProcessInstanceId;

    /** 进行中的纠错单 ID */
    private Long activeCorrectionBillId;

}
