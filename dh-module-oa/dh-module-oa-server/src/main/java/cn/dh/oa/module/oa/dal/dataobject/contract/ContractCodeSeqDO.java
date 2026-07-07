package cn.dh.oa.module.oa.dal.dataobject.contract;

import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 合同编号序列 DO
 */
@TableName("oa_contract_code_seq")
@KeySequence("oa_contract_code_seq_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractCodeSeqDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 业务年度（yyyy）
     */
    private Integer bizYear;

    /**
     * 合同类型码（X/C/F/H/Z/Q）
     */
    private String typeCode;

    /**
     * 当前已分配最大流水号
     */
    private Integer currentSeq;
}
