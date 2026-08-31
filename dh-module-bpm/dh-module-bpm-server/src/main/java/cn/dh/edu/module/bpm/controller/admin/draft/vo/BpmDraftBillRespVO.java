package cn.dh.edu.module.bpm.controller.admin.draft.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 草稿箱 Response VO")
@Data
public class BpmDraftBillRespVO {

    @Schema(description = "业务单据 ID", example = "1024")
    private Long billId;

    @Schema(description = "单据编号", example = "CC20250101001")
    private String billCode;

    @Schema(description = "流程定义 key", example = "oa_travel_apply_bill")
    private String processDefinitionKey;

    @Schema(description = "单据类型名称", example = "差旅申请单")
    private String billTypeName;

    @Schema(description = "流程分类", example = "OA")
    private String category;

    @Schema(description = "流程分类名称", example = "OA办公")
    private String categoryName;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "保存时间")
    private LocalDateTime createTime;

    @Schema(description = "所属部门")
    private String deptName;

    @Schema(description = "业务表单详情路径", example = "/oa/expense-travel/travel-apply-info")
    private String infoPath;

}
