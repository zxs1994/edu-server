package cn.dh.edu.module.bpm.service.task;

import cn.dh.edu.framework.common.util.date.DateUtils;
import cn.dh.edu.module.bpm.dal.dataobject.task.BpmWorkbenchReadDO;
import cn.dh.edu.module.bpm.dal.mysql.task.BpmWorkbenchReadMapper;
import cn.dh.edu.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.edu.module.bpm.framework.flowable.core.enums.BpmnVariableConstants;
import cn.dh.edu.module.bpm.framework.flowable.core.util.FlowableUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.dh.edu.module.bpm.enums.task.BpmnModelConstants.START_USER_NODE_ID;

/**
 * 工作台已读 Service 实现类
 */
@Slf4j
@Service
public class BpmWorkbenchServiceImpl implements BpmWorkbenchService {

    @Resource
    private BpmWorkbenchReadMapper workbenchReadMapper;

    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;

    @Resource
    private BpmProcessInstanceCopyService processInstanceCopyService;

    private static final String TAB_TODO = "todo";
    private static final String TAB_MY_BILL = "myBill";
    private static final String TAB_DONE = "done";
    private static final String TAB_COPY = "copy";

    @Override
    public Map<String, Long> getUnreadCounts(Long userId) {
        Map<String, Long> counts = new HashMap<>(4);

        // 1. 待办任务：新增的待办数（水位线之后创建的）
        counts.put(TAB_TODO, countNewTodoTasks(userId));
        // 2. 我的单据：新增的发起流程数（水位线之后启动的）
        counts.put(TAB_MY_BILL, countNewMyBills(userId));
        // 3. 已办任务：新增的已办数（水位线之后完成的，排除发起人节点）
        counts.put(TAB_DONE, countNewDoneTasks(userId));
        // 4. 抄送我的：未读抄送条数（精确到条目）
        counts.put(TAB_COPY, processInstanceCopyService.getUnreadCopyCount(userId));

        return counts;
    }

    @Override
    public void markTabAsRead(Long userId, String tabKey) {
        BpmWorkbenchReadDO existing = workbenchReadMapper.selectByUserIdAndTabKey(userId, tabKey);
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            // 新建水位线记录
            BpmWorkbenchReadDO readDO = new BpmWorkbenchReadDO();
            readDO.setUserId(userId);
            readDO.setTabKey(tabKey);
            readDO.setLastViewTime(now);
            workbenchReadMapper.insert(readDO);
        } else {
            // 更新水位线时间
            existing.setLastViewTime(now);
            workbenchReadMapper.updateById(existing);
        }

        // 如果是抄送 Tab，同时标记所有抄送为已读
        if (TAB_COPY.equals(tabKey)) {
            processInstanceCopyService.markAllCopyAsRead(userId);
        }
    }

    // ========== 内部计数方法 ==========

    /**
     * 计算待办徽标数：水位线之后的新待办 + 始终计入的驳回待重提（StartUserNode 且流程已驳回）
     */
    private long countNewTodoTasks(Long userId) {
        LocalDateTime watermark = getWatermark(userId, TAB_TODO);
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(String.valueOf(userId))
                .active()
                .taskTenantId(FlowableUtils.getTenantId())
                .includeProcessVariables()
                .list();
        if (watermark == null) {
            return tasks.size();
        }
        Date watermarkDate = DateUtils.of(watermark);
        return tasks.stream()
                .filter(task -> isRejectedResubmitTodoTask(task)
                        || (task.getCreateTime() != null && task.getCreateTime().after(watermarkDate)))
                .count();
    }

    /** 驳回后退回发起人节点、待修改重提的待办（不受已读水位线影响） */
    private boolean isRejectedResubmitTodoTask(Task task) {
        if (!START_USER_NODE_ID.equals(task.getTaskDefinitionKey())) {
            return false;
        }
        Map<String, Object> variables = task.getProcessVariables();
        if (variables == null) {
            return false;
        }
        Object status = variables.get(BpmnVariableConstants.PROCESS_INSTANCE_VARIABLE_STATUS);
        return Objects.equals(status, BpmProcessInstanceStatusEnum.REJECT.getStatus());
    }

    /**
     * 计算新增"我的单据"数（水位线之后启动的流程实例）
     */
    private long countNewMyBills(Long userId) {
        LocalDateTime watermark = getWatermark(userId, TAB_MY_BILL);
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .startedBy(String.valueOf(userId))
                .processInstanceTenantId(FlowableUtils.getTenantId());
        if (watermark != null) {
            query.startedAfter(DateUtils.of(watermark));
        }
        return query.count();
    }

    /**
     * 计算新增"已办任务"数（水位线之后完成的、分配给当前用户的任务，排除发起人自动完成节点）
     */
    private long countNewDoneTasks(Long userId) {
        LocalDateTime watermark = getWatermark(userId, TAB_DONE);
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .finished()
                .taskAssignee(String.valueOf(userId))
                .taskTenantId(FlowableUtils.getTenantId());
        if (watermark != null) {
            query.taskCompletedAfter(DateUtils.of(watermark));
        }
        // 需要排除发起人自动完成节点，但 Flowable count() 无法排除，
        // 因此使用 list 后内存过滤再计数
        long count = query.list().stream()
                .filter(task -> !START_USER_NODE_ID.equals(task.getTaskDefinitionKey()))
                .count();
        return count;
    }

    /**
     * 获取用户指定 Tab 的水位线时间
     *
     * @return 水位线时间，null 表示用户从未查看过该 Tab（返回全部作为未读）
     */
    private LocalDateTime getWatermark(Long userId, String tabKey) {
        BpmWorkbenchReadDO readDO = workbenchReadMapper.selectByUserIdAndTabKey(userId, tabKey);
        return readDO != null ? readDO.getLastViewTime() : null;
    }

}
