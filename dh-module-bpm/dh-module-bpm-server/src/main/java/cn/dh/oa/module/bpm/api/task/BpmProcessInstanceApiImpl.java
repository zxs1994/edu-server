package cn.dh.oa.module.bpm.api.task;

import cn.dh.oa.framework.common.pojo.CommonResult;
import cn.dh.oa.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.oa.module.bpm.service.task.BpmProcessInstanceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.Map;

import static cn.dh.oa.framework.common.pojo.CommonResult.success;

/**
 * Flowable 流程实例 Api 实现类
 *
 * @author 鼎衡
 */
@RestController
@Validated
public class BpmProcessInstanceApiImpl implements BpmProcessInstanceApi {

    @Resource
    private BpmProcessInstanceService processInstanceService;

    @Override
    public CommonResult<String> createProcessInstance(Long userId, @Valid BpmProcessInstanceCreateReqDTO reqDTO) {
        return success(processInstanceService.createProcessInstance(userId, reqDTO));
    }


    @Override
    public CommonResult<String> submitProcessInstance(Long userId, @Valid BpmProcessInstanceCreateReqDTO reqDTO) {
        return success(processInstanceService.submitProcessInstance(userId, reqDTO));
    }

    @Override
    public CommonResult<Map<String, Object>> getHistoricProcessVariables(String processInstanceId) {
        return success(processInstanceService.getHistoricProcessVariables(processInstanceId));
    }

    @Override
    public CommonResult<Boolean> cancelProcessInstanceByReason(String processInstanceId, String reason) {
        processInstanceService.cancelProcessInstanceByReason(processInstanceId, reason);
        return success(true);
    }

}
