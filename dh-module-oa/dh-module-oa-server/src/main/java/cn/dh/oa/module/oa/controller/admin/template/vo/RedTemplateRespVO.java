package cn.dh.oa.module.oa.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;

@Schema(description = "管理后台 - 套红模板 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RedTemplateRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("模板名称")
    private String templateName;

    @Schema(description = "机关名称（红头大字）")
    @ExcelProperty("机关名称")
    private String orgName;

    @Schema(description = "文件类型标签")
    @ExcelProperty("文件类型标签")
    private String docTypeLabel;

    @Schema(description = "红头颜色")
    @ExcelProperty("红头颜色")
    private String headerColor;

    @Schema(description = "模板HTML内容")
    private String templateContent;

    @Schema(description = "预览图片URL")
    @ExcelProperty("预览图片")
    private String previewImage;

    @Schema(description = "状态（0正常 1停用）", example = "0")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "排序")
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
