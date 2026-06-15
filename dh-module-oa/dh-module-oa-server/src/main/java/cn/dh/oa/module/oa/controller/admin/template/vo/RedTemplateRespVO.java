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

    @Schema(description = "机关/公司名称（红头大字）")
    @ExcelProperty("机关/公司名称")
    private String orgName;

    @Schema(description = "名称字号（红头大字字号）", example = "36")
    @ExcelProperty("名称字号")
    private Integer nameFontSize;

    @Schema(description = "字号前缀（如：无办发）", example = "无办发")
    @ExcelProperty("字号前缀")
    private String docNumberPrefix;

    @Schema(description = "印章图片URL")
    private String sealImage;

    @Schema(description = "分隔线样式（single=单线 double=双线）", example = "single")
    @ExcelProperty("分隔线样式")
    private String separatorStyle;

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
