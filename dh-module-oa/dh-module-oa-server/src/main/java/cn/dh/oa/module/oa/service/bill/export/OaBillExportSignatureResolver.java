package cn.dh.oa.module.oa.service.bill.export;

import cn.dh.oa.module.bpm.api.task.BpmTaskApi;
import cn.dh.oa.module.bpm.api.task.dto.BpmHistoricTaskRespDTO;
import cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.oa.module.oa.service.system.SystemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 流程单据导出：解析审批节点并匹配 classpath 签名图
 */
@Component
public class OaBillExportSignatureResolver {

    private static final String AUDIT_LABEL = "审核";
    private static final String APPROVE_LABEL = "审批";
    private static final String GENERIC_APPROVER_NAME = "审批人";

    @Resource
    private BpmTaskApi bpmTaskApi;
    @Resource
    private SystemService systemService;
    @Resource
    private OaSignatureTemplateLoader signatureTemplateLoader;

    public List<BillExportImage> resolve(String processInstanceId) {
        return resolveInternal(processInstanceId, true, AUDIT_LABEL, APPROVE_LABEL);
    }

    /**
     * 按关键字匹配已通过任务并落签名。
     * 不含「审批人」链式拆分；审批匹配不含「终审/批准」等扩展词。
     *
     * @param auditKeyword    审核栏节点名需包含的字，如「审核」
     * @param approveKeyword  审批栏节点名需包含的字，如「审批」
     * @param auditLabel      模板定位标签，如「审核」
     * @param approveLabel    模板定位标签，如「审批人」
     */
    public List<BillExportImage> resolveByKeywords(String processInstanceId,
                                                   String auditKeyword,
                                                   String approveKeyword,
                                                   String auditLabel,
                                                   String approveLabel) {
        if (processInstanceId == null || processInstanceId.isBlank()) {
            return List.of();
        }
        List<BpmHistoricTaskRespDTO> approvedTasks = loadApprovedTasks(processInstanceId);
        if (approvedTasks.isEmpty()) {
            return List.of();
        }
        List<BpmHistoricTaskRespDTO> auditTasks = new ArrayList<>();
        List<BpmHistoricTaskRespDTO> approveTasks = new ArrayList<>();
        for (BpmHistoricTaskRespDTO task : approvedTasks) {
            String taskName = task.getName();
            if (taskName == null || taskName.isBlank()) {
                continue;
            }
            if (taskName.contains(auditKeyword)) {
                auditTasks.add(task);
            } else if (taskName.contains(approveKeyword)) {
                approveTasks.add(task);
            }
        }
        List<BillExportImage> images = new ArrayList<>();
        images.addAll(toImages(auditTasks, auditLabel));
        images.addAll(toImages(approveTasks, approveLabel));
        return images;
    }

    private List<BillExportImage> resolveInternal(String processInstanceId, boolean useGenericChain,
                                                  String auditLabel, String approveLabel) {
        if (processInstanceId == null || processInstanceId.isBlank()) {
            return List.of();
        }
        List<BpmHistoricTaskRespDTO> approvedTasks = loadApprovedTasks(processInstanceId);
        if (approvedTasks.isEmpty()) {
            return List.of();
        }

        List<BpmHistoricTaskRespDTO> auditTasks = new ArrayList<>();
        List<BpmHistoricTaskRespDTO> approveTasks = new ArrayList<>();
        List<BpmHistoricTaskRespDTO> genericApproverChain = new ArrayList<>();
        for (BpmHistoricTaskRespDTO task : approvedTasks) {
            String taskName = task.getName();
            if (useGenericChain && isGenericApproverName(taskName)) {
                genericApproverChain.add(task);
                continue;
            }
            if (matchesAudit(taskName)) {
                auditTasks.add(task);
            } else if (matchesApprove(taskName)) {
                approveTasks.add(task);
            }
        }
        if (useGenericChain) {
            appendGenericApproverChain(auditTasks, approveTasks, genericApproverChain);
        }

        List<BillExportImage> images = new ArrayList<>();
        images.addAll(toImages(auditTasks, auditLabel));
        images.addAll(toImages(approveTasks, approveLabel));
        return images;
    }

    private List<BpmHistoricTaskRespDTO> loadApprovedTasks(String processInstanceId) {
        List<BpmHistoricTaskRespDTO> tasks = bpmTaskApi.getFinishedTaskList(processInstanceId).getCheckedData();
        if (tasks == null || tasks.isEmpty()) {
            return List.of();
        }
        return tasks.stream()
                .filter(task -> Objects.equals(task.getStatus(), BpmTaskStatusEnum.APPROVE.getStatus()))
                .filter(task -> !isStarterTask(task.getName()))
                .sorted(Comparator.comparing(BpmHistoricTaskRespDTO::getEndTime,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private void appendGenericApproverChain(List<BpmHistoricTaskRespDTO> auditTasks,
                                            List<BpmHistoricTaskRespDTO> approveTasks,
                                            List<BpmHistoricTaskRespDTO> genericApproverChain) {
        if (genericApproverChain.isEmpty()) {
            return;
        }
        if (genericApproverChain.size() == 1) {
            approveTasks.add(genericApproverChain.get(0));
            return;
        }
        auditTasks.addAll(genericApproverChain.subList(0, genericApproverChain.size() - 1));
        approveTasks.add(genericApproverChain.get(genericApproverChain.size() - 1));
    }

    private List<BillExportImage> toImages(List<BpmHistoricTaskRespDTO> tasks, String label) {
        if (tasks.isEmpty()) {
            return List.of();
        }
        List<BillExportImage> images = new ArrayList<>(tasks.size());
        Set<Long> seenUserIds = new HashSet<>();
        for (BpmHistoricTaskRespDTO task : tasks) {
            if (task.getAssigneeUserId() == null || !seenUserIds.add(task.getAssigneeUserId())) {
                continue;
            }
            String nickname = systemService.getUserNickname(task.getAssigneeUserId());
            if (nickname == null || nickname.isBlank()) {
                continue;
            }
            signatureTemplateLoader.loadByNickname(nickname.trim()).ifPresent(loaded -> {
                BillExportImage image = new BillExportImage();
                image.setAnchorLabel(label);
                image.setData(loaded.data());
                image.setPictureType(loaded.pictureType());
                images.add(image);
            });
        }
        return images;
    }

    private boolean isStarterTask(String taskName) {
        return taskName != null && taskName.contains("发起");
    }

    private boolean isGenericApproverName(String taskName) {
        return GENERIC_APPROVER_NAME.equals(taskName == null ? null : taskName.trim());
    }

    private boolean matchesAudit(String taskName) {
        if (taskName == null || taskName.isBlank()) {
            return false;
        }
        return taskName.contains("审核") || taskName.contains("复核") || taskName.contains("符合");
    }

    private boolean matchesApprove(String taskName) {
        if (taskName == null || taskName.isBlank()) {
            return false;
        }
        // 最终审批/批准/终审 → 审批（用印单映射为「批准」）
        return taskName.contains("审批") || taskName.contains("终审") || taskName.contains("批准");
    }

}
