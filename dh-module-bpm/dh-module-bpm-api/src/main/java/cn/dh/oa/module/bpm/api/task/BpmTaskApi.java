package cn.dh.oa.module.bpm.api.task;

import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.module.bpm.api.task.dto.BpmHistoricTaskRespDTO;
import cn.dh.oa.module.bpm.enums.ApiConstants;
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

}
