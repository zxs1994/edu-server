package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "活动记录附件")
@Data
public class ActivityInstanceRecordAttachmentVO {

    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @Schema(description = "文件地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件地址不能为空")
    private String fileUrl;

}
