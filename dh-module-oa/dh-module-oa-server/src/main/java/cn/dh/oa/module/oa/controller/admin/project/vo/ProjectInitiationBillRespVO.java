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

    // ========== 业务字段 ==========

    @ExcelProperty("项目名称")
    @Schema(description = "项目名称")
    private String projectName;

    @ExcelProperty("项目类型")
    @Schema(description = "项目类型：1活动 2项目 3课题 4其他")
    private Integer projectType;

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

    @ExcelProperty("预期成果")
    @Schema(description = "预期成果")
    private String expectedOutcome;

    @ExcelProperty("是否重大")
    @Schema(description = "是否重大：0否 1是")
    private Integer isMajor;

    @ExcelProperty("重大备注")
    @Schema(description = "重大备注")
    private String majorRemark;

    @ExcelProperty("事由")
    @Schema(description = "事由")
    private String cause;

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
