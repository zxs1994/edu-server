package cn.dh.edu.module.bpm.api.task;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.module.bpm.api.task.dto.BpmHistoricTaskRespDTO;
import cn.dh.edu.module.bpm.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 流程任务")
public interface BpmTaskApi {

    String PREFIX = ApiConstants.PREFIX + "/task";

    @GetMapping(PREFIX + "/list-finished")
    @Operation(summary = "获取流程实例已完成任务列表（供单据导出签名等内部场景）")
    @Parameter(name = "processInstanceId", description = "流程实例编号", required = true)
    CommonResult<List<BpmHistoricTaskRespDTO>> getFinishedTaskList(
            @RequestParam("processInstanceId") String processInstanceId);

    @GetMapping(PREFIX + "/todo-process-instance-ids")
    @Operation(summary = "获取用户待办任务对应的流程实例编号列表")
    @Parameter(name = "userId", description = "用户编号", required = true)
    CommonResult<List<String>> getTodoProcessInstanceIds(@RequestParam("userId") Long userId);

    @GetMapping(PREFIX + "/is-user-task-participant")
    @Operation(summary = "判断用户是否作为办理人参与过指定流程实例（含已办）")
    @Parameter(name = "userId", description = "用户编号", required = true)
    @Parameter(name = "processInstanceId", description = "流程实例编号", required = true)
    CommonResult<Boolean> isUserTaskParticipant(@RequestParam("userId") Long userId,
                                                @RequestParam("processInstanceId") String processInstanceId);

}
