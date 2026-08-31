package cn.dh.edu.module.bpm.service.bill;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.module.bpm.controller.admin.task.vo.cc.BpmProcessInstanceCopyRespVO;
import cn.dh.edu.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceRespVO;
import cn.dh.edu.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import cn.dh.edu.module.hrm.api.bill.HrmBillExistenceApi;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 判断流程关联的业务单据是否已被删除
 */
@Service
public class BpmBillDeletedService {

    @Resource
    private HrmBillExistenceApi hrmBillExistenceApi;

    public boolean isBillDeleted(String processDefinitionKey, String businessKey) {
        if (StrUtil.isBlank(businessKey) || StrUtil.isBlank(processDefinitionKey)) {
            return false;
        }
        // HrmBillExistenceApi.exists 对非 HRM 单据返回 true，避免误判已删
        return !hrmBillExistenceApi.exists(processDefinitionKey, businessKey);
    }

    public void fillTodoTaskPage(PageResult<BpmTaskRespVO> page,
                                 Map<String, ProcessInstance> processInstanceMap) {
        if (page == null || page.getList() == null) {
            return;
        }
        for (BpmTaskRespVO task : page.getList()) {
            ProcessInstance instance = processInstanceMap.get(task.getProcessInstanceId());
            if (instance == null || task.getProcessInstance() == null) {
                continue;
            }
            task.getProcessInstance().setBillDeleted(
                    isBillDeleted(instance.getProcessDefinitionKey(), instance.getBusinessKey()));
        }
    }

    public void fillHistoricTaskPage(PageResult<BpmTaskRespVO> page,
                                     Map<String, HistoricProcessInstance> processInstanceMap) {
        if (page == null || page.getList() == null) {
            return;
        }
        for (BpmTaskRespVO task : page.getList()) {
            HistoricProcessInstance instance = processInstanceMap.get(task.getProcessInstanceId());
            if (instance == null || task.getProcessInstance() == null) {
                continue;
            }
            task.getProcessInstance().setBillDeleted(
                    isBillDeleted(instance.getProcessDefinitionKey(), instance.getBusinessKey()));
        }
    }

    public void fillProcessInstancePage(PageResult<BpmProcessInstanceRespVO> page,
                                        List<HistoricProcessInstance> instances) {
        if (page == null || page.getList() == null || instances == null) {
            return;
        }
        Map<String, HistoricProcessInstance> instanceMap = instances.stream()
                .collect(java.util.stream.Collectors.toMap(HistoricProcessInstance::getId, i -> i, (a, b) -> a));
        for (BpmProcessInstanceRespVO vo : page.getList()) {
            HistoricProcessInstance instance = instanceMap.get(vo.getId());
            if (instance == null) {
                continue;
            }
            vo.setBillDeleted(isBillDeleted(instance.getProcessDefinitionKey(), instance.getBusinessKey()));
        }
    }

    public void fillCopyPage(PageResult<BpmProcessInstanceCopyRespVO> page,
                             Map<String, HistoricProcessInstance> processInstanceMap) {
        if (page == null || page.getList() == null) {
            return;
        }
        for (BpmProcessInstanceCopyRespVO copy : page.getList()) {
            HistoricProcessInstance instance = processInstanceMap.get(copy.getProcessInstanceId());
            if (instance == null) {
                copy.setBillDeleted(true);
                continue;
            }
            copy.setBillDeleted(isBillDeleted(instance.getProcessDefinitionKey(), instance.getBusinessKey()));
        }
    }

    /** 从抄送分页结果中移除业务单据已删除的记录 */
    public void removeDeletedFromCopyPage(PageResult<BpmProcessInstanceCopyRespVO> page) {
        if (page == null || page.getList() == null) {
            return;
        }
        int removed = (int) page.getList().stream()
                .filter(copy -> Boolean.TRUE.equals(copy.getBillDeleted()))
                .count();
        if (removed == 0) {
            return;
        }
        page.setList(page.getList().stream()
                .filter(copy -> !Boolean.TRUE.equals(copy.getBillDeleted()))
                .collect(java.util.stream.Collectors.toList()));
        page.setTotal(Math.max(0L, page.getTotal() - removed));
    }

    /** 从待办/已办分页结果中移除业务单据已删除的记录 */
    public void removeDeletedFromTodoPage(PageResult<BpmTaskRespVO> page) {
        if (page == null || page.getList() == null) {
            return;
        }
        int removed = (int) page.getList().stream()
                .filter(task -> task.getProcessInstance() != null
                        && Boolean.TRUE.equals(task.getProcessInstance().getBillDeleted()))
                .count();
        if (removed == 0) {
            return;
        }
        page.setList(page.getList().stream()
                .filter(task -> task.getProcessInstance() == null
                        || !Boolean.TRUE.equals(task.getProcessInstance().getBillDeleted()))
                .collect(java.util.stream.Collectors.toList()));
        page.setTotal(Math.max(0L, page.getTotal() - removed));
    }

    /** 从流程实例分页结果中移除业务单据已删除的记录 */
    public void removeDeletedFromProcessInstancePage(PageResult<BpmProcessInstanceRespVO> page) {
        if (page == null || page.getList() == null) {
            return;
        }
        int removed = (int) page.getList().stream().filter(vo -> Boolean.TRUE.equals(vo.getBillDeleted())).count();
        if (removed == 0) {
            return;
        }
        page.setList(page.getList().stream()
                .filter(vo -> !Boolean.TRUE.equals(vo.getBillDeleted()))
                .collect(java.util.stream.Collectors.toList()));
        page.setTotal(Math.max(0L, page.getTotal() - removed));
    }

}
