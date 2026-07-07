package cn.dh.oa.module.bpm.service.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.dh.oa.framework.common.core.KeyValue;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.module.bpm.controller.admin.base.user.UserSimpleBaseVO;
import cn.dh.oa.module.bpm.controller.admin.task.vo.task.BpmTaskPageReqVO;
import cn.dh.oa.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.oa.module.bpm.service.definition.BpmProcessDefinitionService;
import cn.dh.oa.module.oa.api.correction.OaPresidentCorrectionApi;
import cn.dh.oa.module.oa.api.correction.dto.OaCorrectionResubmitTodoDTO;
import cn.dh.oa.module.oa.api.correction.dto.OaCorrectionResubmitTodoQueryDTO;
import jakarta.annotation.Resource;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 会长纠错待重提：合并进 BPM 待办列表（不写入 Flowable）
 */
@Service
public class BpmPresidentCorrectionTodoService {

    private static final String TASK_ID_PREFIX = "president-correction-resubmit-";
    private static final String TASK_NAME = "会长纠错待重提";

    @Resource
    private OaPresidentCorrectionApi oaPresidentCorrectionApi;
    @Resource
    private BpmProcessDefinitionService processDefinitionService;

    public void mergeIntoTodoPage(Long userId, BpmTaskPageReqVO pageVO, PageResult<BpmTaskRespVO> page) {
        if (userId == null || pageVO == null || page == null || pageVO.getPageNo() != 1) {
            return;
        }
        List<OaCorrectionResubmitTodoDTO> todos = oaPresidentCorrectionApi.listResubmitTodos(userId, buildQuery(pageVO));
        if (CollUtil.isEmpty(todos)) {
            return;
        }
        List<BpmTaskRespVO> correctionTasks = todos.stream().map(this::toTaskRespVO).toList();
        List<BpmTaskRespVO> merged = new ArrayList<>(correctionTasks);
        if (CollUtil.isNotEmpty(page.getList())) {
            merged.addAll(page.getList());
        }
        int pageSize = pageVO.getPageSize();
        if (merged.size() > pageSize) {
            merged = new ArrayList<>(merged.subList(0, pageSize));
        }
        page.setList(merged);
        page.setTotal(page.getTotal() + correctionTasks.size());
    }

    private OaCorrectionResubmitTodoQueryDTO buildQuery(BpmTaskPageReqVO pageVO) {
        OaCorrectionResubmitTodoQueryDTO query = new OaCorrectionResubmitTodoQueryDTO();
        if (StrUtil.isNotBlank(pageVO.getBillType())) {
            query.setBillType(pageVO.getBillType());
        } else if (StrUtil.isNotBlank(pageVO.getProcessDefinitionKey())) {
            query.setBillType(pageVO.getProcessDefinitionKey());
        }
        query.setBillCode(pageVO.getBillCode());
        query.setCompanyId(pageVO.getCompanyId());
        query.setDeptId(pageVO.getDeptId());
        if (ArrayUtil.isNotEmpty(pageVO.getReceiveTime())) {
            query.setReceiveTimeStart(pageVO.getReceiveTime()[0]);
            if (pageVO.getReceiveTime().length > 1) {
                query.setReceiveTimeEnd(pageVO.getReceiveTime()[1]);
            }
        }
        return query;
    }

    private BpmTaskRespVO toTaskRespVO(OaCorrectionResubmitTodoDTO todo) {
        BpmTaskRespVO vo = new BpmTaskRespVO();
        vo.setId(TASK_ID_PREFIX + todo.getCorrectionBillId());
        vo.setName(TASK_NAME);
        vo.setCreateTime(todo.getRevokeTime());
        vo.setStatus(BpmTaskStatusEnum.NOT_START.getStatus());
        vo.setReason(todo.getCorrectionReason());
        vo.setPresidentCorrectionResubmitTodo(true);
        vo.setSourceBillId(todo.getSourceBillId());
        vo.setSourceBillType(todo.getSourceBillType());

        UserSimpleBaseVO startUser = new UserSimpleBaseVO();
        startUser.setId(todo.getCreatorUserId());
        startUser.setNickname(todo.getCreatorName());
        vo.setAssigneeUser(startUser);

        BpmTaskRespVO.ProcessInstance processInstance = new BpmTaskRespVO.ProcessInstance();
        processInstance.setId(todo.getSourceProcessInstanceId());
        processInstance.setName(resolveProcessDefinitionName(todo.getSourceBillType()));
        processInstance.setCreateTime(todo.getRevokeTime());
        processInstance.setBillCode(todo.getSourceBillCode());
        processInstance.setCompanyId(todo.getCompanyId());
        processInstance.setCompanyName(todo.getCompanyName());
        processInstance.setDeptId(todo.getDeptId());
        processInstance.setDeptName(todo.getDeptName());
        processInstance.setStartUser(startUser);
        processInstance.setBillDeleted(false);
        if (StrUtil.isNotBlank(todo.getSourceBillTitle())) {
            processInstance.setSummary(List.of(new KeyValue<>("标题", todo.getSourceBillTitle())));
        }
        vo.setProcessInstance(processInstance);
        vo.setProcessInstanceId(todo.getSourceProcessInstanceId());
        return vo;
    }

    private String resolveProcessDefinitionName(String processDefinitionKey) {
        if (StrUtil.isBlank(processDefinitionKey)) {
            return TASK_NAME;
        }
        ProcessDefinition definition = processDefinitionService.getActiveProcessDefinition(processDefinitionKey);
        return definition != null ? definition.getName() : processDefinitionKey;
    }

}
