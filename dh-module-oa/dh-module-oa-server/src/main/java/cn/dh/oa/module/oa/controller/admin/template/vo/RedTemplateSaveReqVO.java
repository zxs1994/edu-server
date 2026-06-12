package cn.dh.oa.module.oa.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 套红模板新增/修改 Request VO")
@Data
public class RedTemplateSaveReqVO {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "标准红头文件模板")
    @NotEmpty(message = "模板名称不能为空")
    private String templateName;

    @Schema(description = "机关名称（红头大字）", requiredMode = Schema.RequiredMode.REQUIRED, example = "XX有限公司")
    @NotEmpty(message = "机关名称不能为空")
    private String orgName;

    @Schema(description = "文件类型标签", example = "文 件")
    private String docTypeLabel;

    @Schema(description = "红头颜色", example = "#FF0000")
    private String headerColor;

    @Schema(description = "模板HTML内容")
    private String templateContent;

    @Schema(description = "预览图片URL")
    private String previewImage;

    @Schema(description = "状态（0正常 1停用）", example = "0")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

}
