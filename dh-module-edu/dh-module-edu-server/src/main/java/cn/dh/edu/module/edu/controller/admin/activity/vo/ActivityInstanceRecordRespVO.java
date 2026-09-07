package cn.dh.edu.module.edu.controller.admin.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "专项活动执行实例 - 活动记录 Response VO")
@Data
public class ActivityInstanceRecordRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "执行实例ID")
    private Long instanceId;

    @Schema(description = "实际开始时间")
    private LocalDateTime actualStartTime;

    @Schema(description = "实际结束时间")
    private LocalDateTime actualEndTime;

    @Schema(description = "缺席用户ID列表（system_users.id）")
    private List<Long> absentUserIds;

    @Schema(description = "缺席用户姓名（展示）")
    private String absentUserNames;

    @Schema(description = "内容纪要")
    private String summary;

    @Schema(description = "附件列表")
    private List<ActivityInstanceRecordAttachmentVO> attachments;

}
