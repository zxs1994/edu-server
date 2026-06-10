package cn.dh.oa.module.oa.controller.admin.document.vo;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import java.util.List;

@Schema(description = "管理后台 - 公文发文单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DocumentDispatchBillRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1882")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "流程实例编号")
    @ExcelProperty("流程实例编号")
    private String processInstanceId;

    @Schema(description = "单据状态", example = "2")
    @ExcelProperty("单据状态")
    private Integer processStatus;

    @Schema(description = "公文标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "关于XXX的通知")
    @ExcelProperty("公文标题")
    private String docTitle;

    @Schema(description = "公文编号", example = "DH-2024-001")
    @ExcelProperty("公文编号")
    private String docNumber;

    @Schema(description = "公文类型（1通知 2公告 3报告 4请示 5批复 6函 7纪要 8其他）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("公文类型")
    private Integer docType;

    @Schema(description = "紧急程度（0普通 1紧急 2特急）", example = "0")
    @ExcelProperty("紧急程度")
    private Integer urgencyLevel;

    @Schema(description = "公文内容")
    @ExcelProperty("公文内容")
    private String docContent;

    @Schema(description = "收文人")
    @ExcelProperty("收文人")
    private String recipients;

    @Schema(description = "抄送人")
    @ExcelProperty("抄送人")
    private String ccList;

    @Schema(description = "是否重要（0否 1是）", example = "0")
    @ExcelProperty("是否重要")
    private Integer isImportant;

    @Schema(description = "申请事由", requiredMode = Schema.RequiredMode.REQUIRED, example = "发布通知")
    @ExcelProperty("申请事由")
    private String cause;

    @Schema(description = "创建者", example = "1")
    @ExcelProperty("创建者")
    private String creator;

    @Schema(description = "创建者姓名", example = "张三")
    @ExcelProperty("创建者姓名")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "部门ID", example = "1")
    @ExcelProperty("部门ID")
    private Long deptId;

    @Schema(description = "部门名称", example = "行政部")
    @ExcelProperty("部门名称")
    private String deptName;

    @Schema(description = "公司ID", example = "1")
    @ExcelProperty("公司ID")
    private Long companyId;

    @Schema(description = "公司名称", example = "鼎衡科技")
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "附件列表")
    private List<AttachmentRespVO> attachments;

}
