package cn.dh.oa.module.oa.dal.dataobject.document;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

/**
 * 公文发文单 DO
 *
 * @author 鼎衡
 */
@TableName("oa_document_dispatch_bill")
@KeySequence("oa_document_dispatch_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDispatchBillDO extends BaseDO {

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

    // ========== 公文信息 ==========
    /**
     * 公文标题
     */
    private String docTitle;
    /**
     * 公文编号
     */
    private String docNumber;
    /**
     * 公文类型（1通知 2公告 3报告 4请示 5批复 6函 7纪要 8其他）
     */
    private Integer docType;
    /**
     * 紧急程度（0普通 1紧急 2特急）
     */
    private Integer urgencyLevel;
    /**
     * 公文内容
     */
    private String docContent;
    /**
     * 收文人
     */
    private String recipients;
    /**
     * 抄送人
     */
    private String ccList;
    /**
     * 是否重要（0否 1是）
     */
    private Integer isImportant;
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
