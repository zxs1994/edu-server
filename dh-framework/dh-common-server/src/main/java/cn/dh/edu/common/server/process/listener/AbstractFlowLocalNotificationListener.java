package cn.dh.edu.common.server.process.listener;

import cn.dh.edu.common.server.process.FlowProcessPrefixUtils;
import cn.dh.edu.framework.common.enums.BillTypeEnum;
import cn.dh.edu.framework.common.enums.SystemEnum;
import cn.dh.edu.framework.common.service.FlowBillService;
import cn.dh.edu.framework.common.service.FlowBillServiceFactory;
import cn.dh.edu.framework.security.core.LoginUser;
import cn.dh.edu.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.edu.framework.tenant.core.util.TenantUtils;
import cn.dh.edu.module.bpm.api.event.BpmEventTypeEnum;
import cn.dh.edu.module.bpm.api.event.BpmProcessInstanceInfo;
import cn.dh.edu.module.bpm.api.event.BpmProcessInstanceStatusEvent;
import cn.dh.edu.module.bpm.api.event.BpmTaskInfo;
import cn.dh.edu.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;

import static cn.dh.edu.module.bpm.enums.task.BpmnModelConstants.START_USER_NODE_ID;

/**
 * 通用 BPM 本地事件监听器抽象类
 *
 * @param <T> 单据类型枚举
 * @author 鼎衡
 */
@Slf4j
public abstract class AbstractFlowLocalNotificationListener<T extends BillTypeEnum>
        implements ApplicationListener<BpmProcessInstanceStatusEvent> {

    protected abstract SystemEnum getSystem();

    protected abstract FlowBillServiceFactory<T> getFlowBillServiceFactory();

    @Override
    public void onApplicationEvent(BpmProcessInstanceStatusEvent event) {
        log.info("[onApplicationEvent][本地事件] 收到 BPM 事件: {}", event);
        if (event.getProcessInstanceInfo() == null || event.getEventType() == null) {
            log.warn("[onApplicationEvent] 流程实例信息或事件类型为空");
            return;
        }

        String processDefinitionKey = event.getProcessDefinitionKey();
        if (!FlowProcessPrefixUtils.match(getSystem(), processDefinitionKey)) {
            log.debug("[onApplicationEvent] 非本模块流程，跳过处理: {}", processDefinitionKey);
            return;
        }

        BpmEventTypeEnum eventType = event.getEventType();
        log.info("[onApplicationEvent][本地事件] 收到 BPM 事件: {}", eventType);
        if (eventType.isProcessInstanceEvent()) {
            handleProcessInstanceEvent(event);
            return;
        }
        if (eventType.isTaskEvent()) {
            handleTaskEvent(event);
        }
    }


    /**
     * 处理流程实例事件
     */
    private void handleProcessInstanceEvent(BpmProcessInstanceStatusEvent message) {
        try {
            updateBillStatus(message);

            log.info("[handleProcessInstanceEvent] 流程实例状态更新成功，eventType: {}, processDefinitionKey: {}, businessKey: {}, status: {}",
                    message.getEventType().getName(),
                    message.getProcessInstanceInfo().getProcessDefinitionKey(),
                    message.getProcessInstanceInfo().getBusinessKey(),
                    message.getProcessInstanceInfo().getStatus());

        } catch (IllegalArgumentException e) {
            log.debug("[handleProcessInstanceEvent] 未知的OA流程类型: {}",
                    message.getProcessInstanceInfo().getProcessDefinitionKey());
        }
    }



    private void updateBillStatus(BpmProcessInstanceStatusEvent message) {
        updateBillStatus(message, null);
    }

    /**
     * 更新单据状态
     * @param message 消息内容
     * @param status 单据状态
     */
    private void updateBillStatus(BpmProcessInstanceStatusEvent message, Integer status) {
        // 通过工厂获取对应的服务实现
        FlowBillService<T> flowBillService = getFlowBillServiceFactory().getServiceByProcessKey(
                message.getProcessInstanceInfo().getProcessDefinitionKey());

        fillOperatorLoginUser(message.getProcessInstanceInfo());

        Runnable task = () -> {
            if (status == null) {
                flowBillService.updateProcessStatus(
                        message.getProcessInstanceInfo().getBusinessKey(),
                        message.getProcessInstanceInfo().getStatus());
            } else {
                flowBillService.updateProcessStatus(
                        message.getProcessInstanceInfo().getBusinessKey(),
                        status);
            }
        };

        // 按流程租户执行，避免 TenantIgnore 导致下游站内信等落库 tenant_id=0
        String tenantIdStr = message.getProcessInstanceInfo().getTenantId();
        if (StringUtils.isNotBlank(tenantIdStr)) {
            try {
                TenantUtils.execute(Long.parseLong(tenantIdStr), task);
                return;
            } catch (NumberFormatException ex) {
                log.warn("[updateBillStatus] 租户ID非法: {}", tenantIdStr);
            }
        }
        TenantUtils.executeIgnore(task);
    }

    /**
     * 将事件中的操作人写入登录态，供下游发公告等业务取最终审批人
     */
    private void fillOperatorLoginUser(BpmProcessInstanceInfo processInstanceInfo) {
        if (processInstanceInfo == null || SecurityFrameworkUtils.getLoginUserId() != null) {
            return;
        }
        String operatorUserId = processInstanceInfo.getOperatorUserId();
        if (StringUtils.isBlank(operatorUserId)) {
            return;
        }
        try {
            LoginUser loginUser = new LoginUser();
            loginUser.setId(Long.parseLong(operatorUserId));
            if (StringUtils.isNotBlank(processInstanceInfo.getTenantId())) {
                loginUser.setTenantId(Long.parseLong(processInstanceInfo.getTenantId()));
            }
            SecurityFrameworkUtils.setLoginUser(loginUser, null);
        } catch (NumberFormatException ex) {
            log.warn("[fillOperatorLoginUser] 操作人ID非法: {}", operatorUserId);
        }
    }

    /**
     * 处理任务事件
     */
    private void handleTaskEvent(BpmProcessInstanceStatusEvent message) {
        BpmEventTypeEnum eventType = message.getEventType();

        log.info("[handleTaskEvent] 处理任务事件，eventType: {}, taskId: {}, taskName: {}, assigneeId: {}, taskResult: {}, taskReason: {}",
                eventType.getName(),
                message.getTaskInfo() != null ? message.getTaskInfo().getTaskId() : null,
                message.getTaskInfo() != null ? message.getTaskInfo().getTaskName() : null,
                message.getTaskInfo() != null ? message.getTaskInfo().getAssigneeId() : null,
                message.getTaskInfo() != null ? message.getTaskInfo().getTaskResult() : null,
                message.getTaskInfo() != null ? message.getTaskInfo().getTaskReason() : null);

        // 根据不同的任务事件类型进行处理
        switch (eventType) {
            case TASK_CREATED:
                handleTaskCreated(message);
                break;
            case TASK_CREATED_REENTER:
                handleTaskCreatedReenter(message);
                break;
            case TASK_APPROVED:
                handleTaskApproved(message);
                break;
            case TASK_REJECTED:
                handleTaskRejected(message);
                break;
            case TASK_WITHDRAWN:
                handleTaskWithdrawn(message);
                break;
            case TASK_TRANSFERRED:
                handleTaskTransferred(message);
                break;
            case TASK_DELEGATED:
                handleTaskDelegated(message);
                break;
            default:
                log.debug("[handleTaskEvent] 暂不处理的任务事件类型: {}", eventType.getName());
                break;
        }
    }

    /**
     * 处理任务创建事件（首次创建）
     */
    private void handleTaskCreated(BpmProcessInstanceStatusEvent message) {
        log.info("[handleTaskCreated] 任务创建通知（首次创建），taskId: {}, taskName: {}, assigneeId: {}",
                message.getTaskInfo() != null ? message.getTaskInfo().getTaskId() : null,
                message.getTaskInfo() != null ? message.getTaskInfo().getTaskName() : null,
                message.getTaskInfo() != null ? message.getTaskInfo().getAssigneeId() : null);

        // 首次创建流程实例时，不更新单据状态为未开始
        // 这里可以添加其他首次创建时需要的业务逻辑
    }

    /**
     * 处理任务创建事件（重新进入开始节点）
     */
    private void handleTaskCreatedReenter(BpmProcessInstanceStatusEvent message) {
        log.info("[handleTaskCreatedReenter] 任务创建通知（重新进入开始节点），taskId: {}, taskName: {}, assigneeId: {}",
                message.getTaskInfo() != null ? message.getTaskInfo().getTaskId() : null,
                message.getTaskInfo() != null ? message.getTaskInfo().getTaskName() : null,
                message.getTaskInfo() != null ? message.getTaskInfo().getAssigneeId() : null);

        BpmTaskInfo taskInfo = message.getTaskInfo();
        if (taskInfo != null && StringUtils.isNoneEmpty(taskInfo.getTaskDefinitionKey())) {
            String taskDefinitionKey = taskInfo.getTaskDefinitionKey();
            // 重新进入开始节点时，根据流程状态决定单据状态
            if (taskDefinitionKey.equals(START_USER_NODE_ID)) {
                Integer processStatus = message.getProcessInstanceInfo() != null
                        ? message.getProcessInstanceInfo().getStatus() : null;
                if (processStatus != null
                        && processStatus.equals(BpmProcessInstanceStatusEnum.REJECT.getStatus())) {
                    // 驳回后：单据状态设为 REJECT（已驳回），允许发起人重新提交
                    log.info("[handleTaskCreatedReenter] 驳回后重新进入开始节点，更新单据状态为已驳回，processInstanceId: {}",
                            message.getProcessInstanceInfo().getProcessInstanceId());
                    updateBillStatus(message, BpmProcessInstanceStatusEnum.REJECT.getStatus());
                } else {
                    // 撤回等其他情况：单据状态设为 NOT_START（未提交）
                    log.info("[handleTaskCreatedReenter] 重新进入开始节点，更新单据状态为未开始，processInstanceId: {}",
                            message.getProcessInstanceInfo().getProcessInstanceId());
                    updateBillStatus(message, BpmProcessInstanceStatusEnum.NOT_START.getStatus());
                }
            }
        }
    }

    /**
     * 处理任务审批通过事件
     */
    private void handleTaskApproved(BpmProcessInstanceStatusEvent message) {
        log.info("[handleTaskApproved] 任务审批通过，taskId: {}, assigneeId: {}, reason: {}",
                message.getTaskInfo() != null ? message.getTaskInfo().getTaskId() : null,
                message.getTaskInfo() != null ? message.getTaskInfo().getAssigneeId() : null,
                message.getTaskInfo() != null ? message.getTaskInfo().getTaskReason() : null);

        // 驳回后发起人重新提交成功：将单据状态恢复为审批中
        BpmTaskInfo taskInfo = message.getTaskInfo();
        BpmProcessInstanceInfo processInfo = message.getProcessInstanceInfo();
        if (taskInfo != null
                && processInfo != null
                && START_USER_NODE_ID.equals(taskInfo.getTaskDefinitionKey())
                && BpmProcessInstanceStatusEnum.isRejectStatus(processInfo.getStatus())) {
            updateBillStatus(message, BpmProcessInstanceStatusEnum.RUNNING.getStatus());
        }

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据审批记录
        // 2. 发送审批结果通知
        // 3. 触发下游业务流程等
    }

    /**
     * 处理任务审批拒绝事件
     */
    private void handleTaskRejected(BpmProcessInstanceStatusEvent message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskRejected] 任务审批拒绝，taskId: {}, assigneeId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId(), taskInfo.getTaskReason());

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据为拒绝状态
        // 2. 发送拒绝通知给申请人
        // 3. 记录拒绝原因等
    }

    /**
     * 处理任务撤回事件
     */
    private void handleTaskWithdrawn(BpmProcessInstanceStatusEvent message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskWithdrawn] 任务撤回，taskId: {}, reason: {}",
                taskInfo.getTaskId(), taskInfo.getTaskReason());

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 更新业务单据为撤回状态
        // 2. 发送撤回通知
        // 3. 清理相关数据等
    }

    /**
     * 处理任务转办事件
     */
    private void handleTaskTransferred(BpmProcessInstanceStatusEvent message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskTransferred] 任务转办，taskId: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId());

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送转办通知
        // 2. 更新审批人信息等
    }

    /**
     * 处理任务委派事件
     */
    private void handleTaskDelegated(BpmProcessInstanceStatusEvent message) {
        BpmTaskInfo taskInfo = message.getTaskInfo();

        log.info("[handleTaskDelegated] 任务委派，taskId: {}, assigneeId: {}",
                taskInfo.getTaskId(), taskInfo.getAssigneeId());

        // 这里可以实现具体的业务逻辑，比如：
        // 1. 发送委派通知
        // 2. 更新委派人信息等
    }
}

