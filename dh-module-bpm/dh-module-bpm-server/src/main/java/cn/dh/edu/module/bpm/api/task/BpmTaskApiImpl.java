package cn.dh.edu.module.bpm.api.task;

import cn.dh.edu.framework.common.pojo.CommonResult;
import cn.dh.edu.framework.common.util.number.NumberUtils;
import cn.dh.edu.module.bpm.api.task.dto.BpmHistoricTaskRespDTO;
import cn.dh.edu.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.edu.module.bpm.framework.flowable.core.util.FlowableUtils;
import cn.dh.edu.module.bpm.service.task.BpmTaskService;
import jakarta.annotation.Resource;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static cn.dh.edu.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class BpmTaskApiImpl implements BpmTaskApi {

    @Resource
    private BpmTaskService taskService;

    @Override
    public CommonResult<List<BpmHistoricTaskRespDTO>> getFinishedTaskList(String processInstanceId) {
        List<HistoricTaskInstance> taskList = taskService.getFinishedTaskListByProcessInstanceIdWithoutCancel(
                processInstanceId);
        List<BpmHistoricTaskRespDTO> result = new ArrayList<>(taskList.size());
        for (HistoricTaskInstance task : taskList) {
            Integer status = FlowableUtils.getTaskStatus(task);
            if (BpmTaskStatusEnum.isCancelStatus(status)) {
                continue;
            }
            BpmHistoricTaskRespDTO dto = new BpmHistoricTaskRespDTO();
            dto.setName(task.getName());
            dto.setAssigneeUserId(NumberUtils.parseLong(task.getAssignee()));
            dto.setStatus(status);
            if (task.getEndTime() != null) {
                dto.setEndTime(cn.hutool.core.date.LocalDateTimeUtil.of(task.getEndTime()));
            }
            result.add(dto);
        }
        return success(result);
    }

    @Override
    public CommonResult<List<String>> getTodoProcessInstanceIds(Long userId) {
        return success(taskService.getTodoProcessInstanceIds(userId));
    }

    @Override
    public CommonResult<Boolean> isUserTaskParticipant(Long userId, String processInstanceId) {
        return success(taskService.isUserTaskParticipant(userId, processInstanceId));
    }

}
