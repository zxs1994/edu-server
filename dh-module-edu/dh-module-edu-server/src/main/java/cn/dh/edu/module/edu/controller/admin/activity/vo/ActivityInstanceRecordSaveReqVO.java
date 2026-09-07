package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "专项活动执行实例 - 活动记录保存 Request VO")
@Data
public class ActivityInstanceRecordSaveReqVO {

    @Schema(description = "执行实例ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "活动实例ID不能为空")
    private Long instanceId;

    @Schema(description = "实际执行时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "实际执行时间不能为空")
    private LocalDateTime actualDate;

    @Schema(description = "实际开始时间")
    private LocalDateTime actualStartTime;

    @Schema(description = "实际结束时间")
    private LocalDateTime actualEndTime;

    @Schema(description = "缺席用户ID列表（system_users.id，须在活动参与人范围内）")
    private List<Long> absentUserIds;

    @Schema(description = "内容纪要", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "内容纪要不能为空")
    @Size(max = 2000, message = "内容纪要不能超过2000个字符")
    private String summary;

    @Schema(description = "附件列表（最多5个）")
    @Valid
    @Size(max = 5, message = "附件最多5个")
    private List<ActivityInstanceRecordAttachmentVO> attachments;

}
