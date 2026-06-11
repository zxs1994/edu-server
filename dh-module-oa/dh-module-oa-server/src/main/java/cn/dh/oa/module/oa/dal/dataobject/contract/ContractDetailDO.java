package cn.dh.oa.module.oa.dal.dataobject.contract;

import lombok.*;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 合同明细 DO
 *
 * @author 鼎衡
 */
@TableName("oa_contract_detail")
@KeySequence("oa_contract_detail_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDetailDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 合同审批单ID
     */
    private Long billId;
    /**
     * 项目/产品名称
     */
    private String productName;
    /**
     * 规格型号
     */
    private String specification;
    /**
     * 单位
     */
    private String unit;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 单价
     */
    private BigDecimal unitPrice;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer sortOrder;

}
