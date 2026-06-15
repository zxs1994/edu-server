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

    @Schema(description = "机关/公司名称（红头大字）", requiredMode = Schema.RequiredMode.REQUIRED, example = "XX有限公司")
    @NotEmpty(message = "机关/公司名称不能为空")
    private String orgName;

    @Schema(description = "名称字号（红头大字字号）", example = "36")
    private Integer nameFontSize;

    @Schema(description = "字号前缀（如：无办发）", example = "无办发")
    private String docNumberPrefix;

    @Schema(description = "印章图片URL")
    private String sealImage;

    @Schema(description = "分隔线样式（single=单线 double=双线）", example = "single")
    private String separatorStyle;

    @Schema(description = "状态（0正常 1停用）", example = "0")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

}
