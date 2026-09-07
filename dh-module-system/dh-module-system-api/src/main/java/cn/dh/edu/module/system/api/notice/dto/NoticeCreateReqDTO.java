package cn.dh.edu.module.system.api.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "RPC - 通知公告创建 Request DTO")
@Data
public class NoticeCreateReqDTO {

    @Schema(description = "公告标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过50个字符")
    private String title;

    @Schema(description = "公告类型，字典 system_notice_type", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "公告类型不能为空")
    private Integer type;

    @Schema(description = "公告内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "公告内容不能为空")
    private String content;

    @Schema(description = "状态，参见 CommonStatusEnum")
    private Integer status;

    @Schema(description = "是否重要通知")
    private Boolean isImportant;

    @Schema(description = "是否 WebSocket 推送给在线用户")
    private Boolean push;

    @Schema(description = "租户编号（跨模块调用且当前忽略租户上下文时必填）")
    private Long tenantId;

    @Schema(description = "发布人用户编号（不传则按当前登录用户自动填充）", example = "1")
    private String creator;

}
