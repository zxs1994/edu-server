package cn.dh.oa.module.oa.controller.admin.project.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import java.util.List;

@Schema(description = "管理后台 - 项目立项单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProjectInitiationBillRespVO {

    @Schema(description = "主键")
    private Long id;

    @ExcelProperty("单据编号")
    @Schema(description = "单据编号")
    private String billCode;

    @ExcelProperty("流程实例编号")
    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @ExcelProperty("单据状态")
    @Schema(description = "单据状态")
    private Integer processStatus;

    @Schema(description = "展示层叠加：会长异议/纠错")
    private Boolean presidentCorrectionDisplay;

    @Schema(description = "列表状态列：仅展示会长异议/纠错（未重新发起）")
    private Boolean presidentCorrectionAwaitingResubmit;

    // ========== 业务字段 ==========

    @ExcelProperty("项目名称")
    @Schema(description = "项目名称")
    private String projectName;

    @ExcelProperty("项目类型")
    @Schema(description = "项目类型：1研发型 2交付实施型 3工程建造型")
    private Integer projectType;

    @ExcelProperty("优先级")
    @Schema(description = "优先级：1高 2中 3低")
    private Integer priority;

    @ExcelProperty("项目分类")
    @Schema(description = "项目分类：1研发项目 2交付项目 3运维项目")
    private Integer projectCategory;

    @Schema(description = "所属项目集ID")
    private Long projectSetId;

    @ExcelProperty("所属项目集")
    @Schema(description = "所属项目集名称")
    private String projectSetName;

    @ExcelProperty("项目描述")
    @Schema(description = "项目描述")
    private String projectDescription;

    @ExcelProperty("预算金额")
    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @ExcelProperty("开始日期")
    @Schema(description = "开始日期")
    private LocalDate startDate;

    @ExcelProperty("结束日期")
    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "关联合同ID")
    private Long relatedContractId;

    @ExcelProperty("合同编号")
    @Schema(description = "合同编号")
    private String contractCode;

    @ExcelProperty("合同名称")
    @Schema(description = "合同名称")
    private String contractName;

    @Schema(description = "项目经理ID")
    private Long projectManagerId;

    @ExcelProperty("项目经理")
    @Schema(description = "项目经理名称")
    private String projectManagerName;

    @ExcelProperty("对方类型")
    @Schema(description = "对方类型：1CRM客户 2ERP供应商")
    private Integer counterpartyType;

    @Schema(description = "对方单位ID")
    private Long counterpartyId;

    @ExcelProperty("对方单位")
    @Schema(description = "对方单位名称")
    private String counterpartyName;

    @ExcelProperty("对方联系人")
    @Schema(description = "对方联系人")
    private String counterpartyContact;

    @ExcelProperty("对方电话")
    @Schema(description = "对方联系电话")
    private String counterpartyPhone;

    // ========== 公共字段 ==========

    @ExcelProperty("创建者姓名")
    @Schema(description = "创建者姓名")
    private String creatorName;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "公司编号")
    private Long companyId;

    @ExcelProperty("公司名称")
    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "部门编号")
    private Long deptId;

    @ExcelProperty("部门名称")
    @Schema(description = "部门名称")
    private String deptName;

    @ExcelProperty("创建时间")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}
