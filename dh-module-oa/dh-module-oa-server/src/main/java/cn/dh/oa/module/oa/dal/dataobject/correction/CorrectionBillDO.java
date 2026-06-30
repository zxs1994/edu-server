package cn.dh.oa.module.oa.dal.dataobject.correction;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 纠错申请单 DO
 *
 * @author 鼎衡
 */
@TableName("oa_correction_bill")
@KeySequence("oa_correction_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionBillDO extends BaseDO {

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
     * 原单据类型
     */
    private String sourceBillType;
    /**
     * 原单据ID
     */
    private Long sourceBillId;
    /**
     * 原单据编号
     */
    private String sourceBillCode;
    /**
     * 原流程实例ID
     */
    private String sourceProcessInstanceId;
    /**
     * 原单据标题
     */
    private String sourceBillTitle;
    /**
     * 纠错理由
     */
    private String correctionReason;
    /**
     * 冻结状态 0未冻结 1已冻结 2已解冻
     */
    private Integer freezeStatus;
    /**
     * 纠错状态 0待处理 1重审中 2已完成 3已撤销
     */
    private Integer correctionStatus;
    /**
     * 是否理事会决议 0否 1是
     */
    private Integer councilDecision;
    /**
     * 理事会决议文件
     */
    private String councilDecisionFile;
    /**
     * 重审流程实例ID
     */
    private String newProcessInstanceId;
    /**
     * 纠错处理结果
     */
    private String correctionResult;

    /**
     * 纠错类型（1异议纠错 2理事会决议）
     */
    private Integer correctionType;

    /**
     * 撤销时间
     */
    private java.time.LocalDateTime revokeTime;

    /**
     * 撤销人用户ID
     */
    private Long revokeUserId;

    /**
     * 撤销人姓名
     */
    private String revokeUserName;

    /**
     * 纠错版本号
     */
    private Integer approvalVersion;

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
