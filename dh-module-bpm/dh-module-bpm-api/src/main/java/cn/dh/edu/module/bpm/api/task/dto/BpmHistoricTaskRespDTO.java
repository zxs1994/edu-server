package cn.dh.edu.module.bpm.api.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "RPC 服务 - 流程历史任务 Response DTO")
@Data
public class BpmHistoricTaskRespDTO {

    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "审批人用户编号")
    private Long assigneeUserId;

    @Schema(description = "任务定义 Key")
    private String taskDefinitionKey;

    @Schema(description = "任务状态，参见 BpmTaskStatusEnum")
    private Integer status;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

}
