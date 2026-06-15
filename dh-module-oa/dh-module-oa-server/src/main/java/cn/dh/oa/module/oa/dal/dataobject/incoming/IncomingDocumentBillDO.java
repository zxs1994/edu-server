package cn.dh.oa.module.oa.dal.dataobject.incoming;

import lombok.*;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.dh.oa.framework.mybatis.core.dataobject.BaseDO;

@TableName("oa_incoming_document_bill")
@KeySequence("oa_incoming_document_bill_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomingDocumentBillDO extends BaseDO {

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
     * 公文标题
     */
    private String docTitle;
    /**
     * 来文字号
     */
    private String docNumber;
    /**
     * 密级：0公开 1内部 2机密 3绝密
     */
    private Integer secrecyLevel;
    /**
     * 收文日期
     */
    private LocalDate receiveDate;
    /**
     * 收文类型：1上级文件 2平级文件 3下级文件 4群众来信 5其他
     */
    private Integer docType;
    /**
     * 紧急程度：0普通 1紧急 2特急
     */
    private Integer urgencyLevel;
    /**
     * 收文部门编号
     */
    private Long handlingDeptId;
    /**
     * 收文部门名称
     */
    private String handlingDeptName;
    /**
     * 主办人
     */
    private String hostPerson;
    /**
     * 领导批示
     */
    private String leaderInstruction;
    /**
     * 办理结果
     */
    private String handlingResult;
    /**
     * 办理期限
     */
    private String handlingDeadline;
    /**
     * 内容摘要
     */
    private String contentSummary;
    /**
     * 办理状态：0待办理 1办理中 2已办结
     */
    private Integer handlingStatus;

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
