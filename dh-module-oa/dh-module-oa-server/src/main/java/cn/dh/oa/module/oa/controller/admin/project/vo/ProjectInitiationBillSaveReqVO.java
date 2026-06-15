package cn.dh.oa.module.oa.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentSaveReqVO;

@Schema(description = "管理后台 - 项目立项单新增/修改 Request VO")
@Data
public class ProjectInitiationBillSaveReqVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态")
    private Integer processStatus;

    // ========== 业务字段 ==========

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "项目名称不能为空")
    private String projectName;

    @Schema(description = "项目类型：1研发型 2交付实施型 3工程建造型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "项目类型不能为空")
    private Integer projectType;

    @Schema(description = "优先级：1高 2中 3低")
    private Integer priority;

    @Schema(description = "项目分类：1研发项目 2交付项目 3运维项目")
    private Integer projectCategory;

    @Schema(description = "所属项目集ID")
    private Long projectSetId;

    @Schema(description = "所属项目集名称")
    private String projectSetName;

    @Schema(description = "项目描述")
    private String projectDescription;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "关联合同ID")
    private Long relatedContractId;

    @Schema(description = "合同编号")
    private String contractCode;

    @Schema(description = "合同名称")
    private String contractName;

    @Schema(description = "项目经理ID")
    private Long projectManagerId;

    @Schema(description = "项目经理名称")
    private String projectManagerName;

    @Schema(description = "对方类型：1CRM客户 2ERP供应商")
    private Integer counterpartyType;

    @Schema(description = "对方单位ID")
    private Long counterpartyId;

    @Schema(description = "对方单位名称")
    private String counterpartyName;

    @Schema(description = "对方联系人")
    private String counterpartyContact;

    @Schema(description = "对方联系电话")
    private String counterpartyPhone;

    // ========== 公共字段 ==========

    @Schema(description = "创建者姓名")
    private String creatorName;

    @Schema(description = "创建者")
    private String creator;

    @Schema(description = "公司编号")
    private Long companyId;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "部门编号")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentSaveReqVO> attachments;

}
