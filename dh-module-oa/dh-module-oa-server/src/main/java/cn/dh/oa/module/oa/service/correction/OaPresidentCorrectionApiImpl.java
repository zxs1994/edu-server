package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.oa.module.oa.api.correction.OaPresidentCorrectionApi;
import cn.dh.oa.module.oa.api.correction.dto.OaCorrectionResubmitTodoDTO;
import cn.dh.oa.module.oa.api.correction.dto.OaCorrectionResubmitTodoQueryDTO;
import cn.dh.oa.module.oa.api.correction.dto.OaCorrectionRevokeNodeDTO;
import cn.dh.oa.module.oa.dal.dataobject.correction.BillCorrectionStateDO;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.module.oa.dal.mysql.correction.BillCorrectionStateMapper;
import cn.dh.oa.module.oa.dal.mysql.correction.CorrectionBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.enums.correction.OaBillCorrectionStatusEnum;
import cn.dh.oa.module.oa.service.correction.dto.BillCorrectionSourceDTO;
import cn.dh.oa.module.oa.service.correction.freeze.BillFreezeHandlerRegistry;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 会长纠错 API 实现（供 BPM 模块调用，不依赖 BpmProcessInstanceApi，避免循环依赖）
 */
@Slf4j
@Service
public class OaPresidentCorrectionApiImpl implements OaPresidentCorrectionApi {

    @Resource
    private CorrectionBillMapper correctionBillMapper;
    @Resource
    private BillCorrectionStateMapper billCorrectionStateMapper;
    @Resource
    private BillFreezeHandlerRegistry billFreezeHandlerRegistry;
    @Resource
    private BillCorrectionSourceService billCorrectionSourceService;

    @Override
    public OaCorrectionRevokeNodeDTO getRevokeNode(String sourceProcessInstanceId) {
        CorrectionBillDO correction = correctionBillMapper.selectLatestBySourceProcessInstanceId(sourceProcessInstanceId);
        if (correction == null) {
            return null;
        }
        OaCorrectionRevokeNodeDTO dto = new OaCorrectionRevokeNodeDTO();
        dto.setRevokeUserId(correction.getRevokeUserId());
        dto.setRevokeUserName(correction.getRevokeUserName());
        dto.setCorrectionReason(correction.getCorrectionReason());
        dto.setRevokeTime(correction.getRevokeTime());
        return dto;
    }

    @Override
    public void onReApprovalProcessCompleted(String processDefinitionKey, String businessKey,
                                             String processInstanceId, Integer status) {
        if (!BillCorrectionSourceService.SUPPORTED_BILL_TYPES.contains(processDefinitionKey)) {
            return;
        }
        Long billId = Long.parseLong(businessKey);
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(processDefinitionKey, billId);
        CorrectionBillDO correction = resolveCurrentCorrection(state, processDefinitionKey, billId);
        if (correction == null || !OaBillCorrectionStatusEnum.shouldDisplayOverlay(correction.getCorrectionStatus())) {
            return;
        }
        if (!isCorrectionReApprovalProcess(correction, processDefinitionKey, billId, processInstanceId)) {
            return;
        }
        if (BpmProcessInstanceStatusEnum.APPROVE.getStatus().equals(status)) {
            billCorrectionSourceService.applyReApprovalCompleted(processDefinitionKey, billId, processInstanceId, status);
            billFreezeHandlerRegistry.unfreeze(processDefinitionKey, billId);
            correctionBillMapper.updateById(new CorrectionBillDO()
                    .setId(correction.getId())
                    .setCorrectionStatus(OaBillCorrectionStatusEnum.COMPLETED.getStatus())
                    .setFreezeStatus(2));
            updateBillStateCompleted(processDefinitionKey, billId);
            log.info("[onReApprovalProcessCompleted] 纠错重审通过 billId={}", billId);
        }
    }

    @Override
    public boolean isBillFrozen(String billType, Long billId) {
        BillCorrectionStateDO state = findStateByBillTypeWithFallback(billType, billId);
        return state != null && Objects.equals(state.getFreezeStatus(), 1);
    }

    @Override
    public boolean shouldDisplayCorrectionOverlay(String billType, Long billId) {
        if (!billCorrectionSourceService.exists(billType, billId)) {
            return false;
        }
        BillCorrectionStateDO state = findStateByBillTypeWithFallback(billType, billId);
        return isAwaitingResubmit(state, billType, billId);
    }

    @Override
    public boolean isAwaitingResubmitAfterCorrection(String billType, Long billId) {
        if (!billCorrectionSourceService.exists(billType, billId)) {
            return false;
        }
        BillCorrectionStateDO state = findStateByBillTypeWithFallback(billType, billId);
        return isAwaitingResubmit(state, billType, billId);
    }

    @Override
    public void onSourceBillResubmitted(String billType, Long billId, String processInstanceId) {
        BillCorrectionStateDO state = findStateByBillTypeWithFallback(billType, billId);
        if (state == null || !Objects.equals(state.getFreezeStatus(), 1)
                || !OaBillCorrectionStatusEnum.shouldDisplayOverlay(state.getCorrectionStatus())) {
            return;
        }
        CorrectionBillDO correction = resolveCurrentCorrection(state, billType, billId);
        if (correction == null) {
            return;
        }
        correctionBillMapper.updateById(new CorrectionBillDO()
                .setId(correction.getId())
                .setNewProcessInstanceId(processInstanceId));
        if (!Objects.equals(state.getActiveCorrectionBillId(), correction.getId())) {
            billCorrectionStateMapper.updateById(new BillCorrectionStateDO()
                    .setId(state.getId())
                    .setActiveCorrectionBillId(correction.getId()));
        }
        log.info("[onSourceBillResubmitted] 纠错中单据已重新发起流程 billType={}, billId={}, pi={}",
                billType, billId, processInstanceId);
    }

    @Override
    public boolean shouldSyncFrozenBillProcessStatus(String billType, Long billId, String processInstanceId) {
        if (StrUtil.isBlank(processInstanceId)) {
            return false;
        }
        if (!billCorrectionSourceService.exists(billType, billId)) {
            return false;
        }
        BillCorrectionStateDO state = findStateByBillTypeWithFallback(billType, billId);
        if (state == null) {
            return false;
        }
        CorrectionBillDO correction = resolveCurrentCorrection(state, billType, billId);
        return correction != null && isCorrectionReApprovalProcess(correction, billType, billId, processInstanceId);
    }

    @Override
    public Set<String> listProtectedSourceProcessInstanceIds(String billType, Long billId) {
        Set<String> ids = new HashSet<>();
        List<CorrectionBillDO> corrections = correctionBillMapper.selectListBySourceBill(billType, billId);
        for (CorrectionBillDO correction : corrections) {
            if (StrUtil.isNotBlank(correction.getSourceProcessInstanceId())) {
                ids.add(correction.getSourceProcessInstanceId());
            }
        }
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        if (state != null && StrUtil.isNotBlank(state.getLastProcessInstanceId())) {
            ids.add(state.getLastProcessInstanceId());
        }
        return ids;
    }

    @Override
    public boolean shouldHideCorrectedSourceProcessInstance(String billType, Long billId, String processInstanceId) {
        if (StrUtil.isBlank(billType) || billId == null || StrUtil.isBlank(processInstanceId)) {
            return false;
        }
        CorrectionBillDO correction = correctionBillMapper.selectLatestBySourceProcessInstanceId(processInstanceId);
        if (correction == null
                || !Objects.equals(correction.getSourceBillType(), billType)
                || !Objects.equals(correction.getSourceBillId(), billId)) {
            return false;
        }
        if (!billCorrectionSourceService.exists(billType, billId)) {
            return true;
        }
        try {
            return hasResubmittedProcess(correction, billType, billId);
        } catch (RuntimeException ex) {
            log.warn("[shouldHideCorrectedSourceProcessInstance] 判断纠错原流程隐藏失败 billType={}, billId={}, pi={}",
                    billType, billId, processInstanceId, ex);
            return false;
        }
    }

    private void updateBillStateCompleted(String billType, Long billId) {
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        if (state == null) {
            return;
        }
        billCorrectionStateMapper.updateById(new BillCorrectionStateDO()
                .setId(state.getId())
                .setFreezeStatus(0)
                .setCorrectionStatus(OaBillCorrectionStatusEnum.COMPLETED.getStatus())
                .setActiveCorrectionBillId(null));
    }

    private boolean isAwaitingResubmit(BillCorrectionStateDO state, String billType, Long billId) {
        if (state == null || !Objects.equals(state.getFreezeStatus(), 1)
                || !OaBillCorrectionStatusEnum.shouldDisplayOverlay(state.getCorrectionStatus())) {
            return false;
        }
        CorrectionBillDO correction = resolveCurrentCorrection(state, billType, billId);
        return correction == null || !hasResubmittedProcess(correction, billType, billId);
    }

    private CorrectionBillDO resolveCurrentCorrection(BillCorrectionStateDO state, String billType, Long billId) {
        if (state == null) {
            return null;
        }
        if (state.getActiveCorrectionBillId() != null) {
            return correctionBillMapper.selectById(state.getActiveCorrectionBillId());
        }
        return correctionBillMapper.selectLatestBySourceBill(billType, billId);
    }

    private boolean isCorrectionReApprovalProcess(CorrectionBillDO correction, String billType,
                                                  Long billId, String processInstanceId) {
        if (Objects.equals(correction.getNewProcessInstanceId(), processInstanceId)) {
            return true;
        }
        if (StrUtil.isNotBlank(correction.getNewProcessInstanceId())) {
            return false;
        }
        BillCorrectionSourceDTO source = billCorrectionSourceService.loadRequired(billType, billId);
        return Objects.equals(source.getProcessInstanceId(), processInstanceId)
                && !Objects.equals(source.getProcessInstanceId(), correction.getSourceProcessInstanceId());
    }

    private boolean hasResubmittedProcess(CorrectionBillDO correction, String billType, Long billId) {
        if (StrUtil.isNotBlank(correction.getNewProcessInstanceId())) {
            return true;
        }
        BillCorrectionSourceDTO source = billCorrectionSourceService.loadRequired(billType, billId);
        return StrUtil.isNotBlank(source.getProcessInstanceId())
                && !Objects.equals(source.getProcessInstanceId(), correction.getSourceProcessInstanceId());
    }

    /**
     * 兼容历史数据：日常报销与差旅报销曾共用同一数据表，纠错状态可能落在任一 billType 上。
     */
    private BillCorrectionStateDO findStateByBillTypeWithFallback(String billType, Long billId) {
        if (billId == null || StrUtil.isBlank(billType)) {
            return null;
        }
        BillCorrectionStateDO state = billCorrectionStateMapper.selectByBill(billType, billId);
        if (state != null) {
            return state;
        }
        String fallbackType = resolveFallbackBillType(billType);
        if (StrUtil.isBlank(fallbackType)) {
            return null;
        }
        return billCorrectionStateMapper.selectByBill(fallbackType, billId);
    }

    private String resolveFallbackBillType(String billType) {
        if (Objects.equals(billType, OaBillTypeEnum.OA_DAILY_EXPENSE_BILL.getProcessDefinitionKey())) {
            return OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getProcessDefinitionKey();
        }
        if (Objects.equals(billType, OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getProcessDefinitionKey())) {
            return OaBillTypeEnum.OA_DAILY_EXPENSE_BILL.getProcessDefinitionKey();
        }
        return null;
    }

    @Override
    public List<OaCorrectionResubmitTodoDTO> listResubmitTodos(Long userId, OaCorrectionResubmitTodoQueryDTO query) {
        if (userId == null) {
            return List.of();
        }
        OaCorrectionResubmitTodoQueryDTO safeQuery = query != null ? query : new OaCorrectionResubmitTodoQueryDTO();
        List<OaCorrectionResubmitTodoDTO> result = new ArrayList<>();
        for (CorrectionBillDO correction : correctionBillMapper.selectListAwaitingObjectionResubmit()) {
            if (!billCorrectionSourceService.exists(correction.getSourceBillType(), correction.getSourceBillId())) {
                continue;
            }
            BillCorrectionSourceDTO source = billCorrectionSourceService.loadRequired(
                    correction.getSourceBillType(), correction.getSourceBillId());
            if (!Objects.equals(source.getCreatorUserId(), userId)) {
                continue;
            }
            if (!matchesResubmitTodoQuery(correction, source, safeQuery)) {
                continue;
            }
            result.add(toResubmitTodo(correction, source));
        }
        return result;
    }

    private boolean matchesResubmitTodoQuery(CorrectionBillDO correction, BillCorrectionSourceDTO source,
                                           OaCorrectionResubmitTodoQueryDTO query) {
        if (StrUtil.isNotBlank(query.getBillType())
                && !Objects.equals(query.getBillType(), correction.getSourceBillType())) {
            return false;
        }
        if (StrUtil.isNotBlank(query.getBillCode())
                && (StrUtil.isBlank(correction.getSourceBillCode())
                || !correction.getSourceBillCode().contains(query.getBillCode()))) {
            return false;
        }
        if (query.getCompanyId() != null && !Objects.equals(query.getCompanyId(), source.getCompanyId())) {
            return false;
        }
        if (query.getDeptId() != null && !Objects.equals(query.getDeptId(), source.getDeptId())) {
            return false;
        }
        if (query.getReceiveTimeStart() != null && correction.getRevokeTime() != null
                && correction.getRevokeTime().isBefore(query.getReceiveTimeStart())) {
            return false;
        }
        if (query.getReceiveTimeEnd() != null && correction.getRevokeTime() != null
                && correction.getRevokeTime().isAfter(query.getReceiveTimeEnd())) {
            return false;
        }
        return true;
    }

    private OaCorrectionResubmitTodoDTO toResubmitTodo(CorrectionBillDO correction, BillCorrectionSourceDTO source) {
        OaCorrectionResubmitTodoDTO dto = new OaCorrectionResubmitTodoDTO();
        dto.setCorrectionBillId(correction.getId());
        dto.setSourceBillType(correction.getSourceBillType());
        dto.setSourceBillId(correction.getSourceBillId());
        dto.setSourceBillCode(correction.getSourceBillCode());
        dto.setSourceBillTitle(correction.getSourceBillTitle());
        dto.setSourceProcessInstanceId(correction.getSourceProcessInstanceId());
        dto.setCorrectionReason(correction.getCorrectionReason());
        dto.setRevokeTime(correction.getRevokeTime());
        dto.setCreatorUserId(source.getCreatorUserId());
        dto.setCreatorName(source.getCreatorName());
        dto.setCompanyId(source.getCompanyId());
        dto.setCompanyName(source.getCompanyName());
        dto.setDeptId(source.getDeptId());
        dto.setDeptName(source.getDeptName());
        return dto;
    }

}
