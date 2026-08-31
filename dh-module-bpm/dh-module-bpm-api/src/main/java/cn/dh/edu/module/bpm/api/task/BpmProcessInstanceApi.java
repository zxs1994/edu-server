package cn.dh.edu.module.bpm.api.task;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.edu.module.bpm.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import java.util.Map;

@FeignClient(name = ApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 流程实例")
public interface BpmProcessInstanceApi {

    String PREFIX = ApiConstants.PREFIX + "/process-instance";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建流程实例（提供给内部），返回实例编号")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    CommonResult<String> createProcessInstance(@RequestParam("userId") Long userId,
                                               @Valid @RequestBody BpmProcessInstanceCreateReqDTO reqDTO);

    @PostMapping(PREFIX + "/submit")
    @Operation(summary = "智能提交流程实例（提供给内部），如果流程实例不存在则创建，存在则审批发起人任务")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    CommonResult<String> submitProcessInstance(@RequestParam("userId") Long userId,
                                               @Valid @RequestBody BpmProcessInstanceCreateReqDTO reqDTO);

    @GetMapping(PREFIX + "/get-historic-variables")
    @Operation(summary = "获取历史流程实例变量（会长纠错复制流程变量用）")
    CommonResult<Map<String, Object>> getHistoricProcessVariables(
            @RequestParam("processInstanceId") String processInstanceId);

    @PostMapping(PREFIX + "/cancel-by-reason")
    @Operation(summary = "按指定原因取消流程实例（内部调用，如会长纠错撤销原流程）")
    CommonResult<Boolean> cancelProcessInstanceByReason(
            @RequestParam("processInstanceId") String processInstanceId,
            @RequestParam("reason") String reason);

    @PostMapping(PREFIX + "/delete-copy")
    @Operation(summary = "删除流程实例对应的抄送记录（内部调用，如单据删除）")
    CommonResult<Boolean> deleteProcessInstanceCopy(
            @RequestParam("processInstanceId") String processInstanceId);

}
