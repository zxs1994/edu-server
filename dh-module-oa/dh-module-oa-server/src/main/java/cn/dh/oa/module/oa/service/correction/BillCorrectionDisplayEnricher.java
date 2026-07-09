package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.module.oa.controller.admin.correction.vo.CorrectionBillRespVO;
import cn.dh.oa.module.oa.controller.admin.car.vo.CarApplyBillRespVO;
import cn.dh.oa.module.oa.controller.admin.car.vo.CarReturnBillRespVO;
import cn.dh.oa.module.oa.controller.admin.contract.vo.ContractBillRespVO;
import cn.dh.oa.module.oa.controller.admin.document.vo.DocumentDispatchBillRespVO;
import cn.dh.oa.module.oa.controller.admin.expense.vo.ExpenseReimburseBillRespVO;
import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.ExpensePaymentBillRespVO;
import cn.dh.oa.module.oa.controller.admin.incoming.vo.IncomingDocumentBillRespVO;
import cn.dh.oa.module.oa.controller.admin.meetingroom.vo.MeetingRoomBookingRespVO;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillRespVO;
import cn.dh.oa.module.oa.controller.admin.project.vo.ProjectInitiationBillRespVO;
import cn.dh.oa.module.oa.controller.admin.seal.vo.SealApplyBillRespVO;
import cn.dh.oa.module.oa.controller.admin.travel.vo.TravelApplyBillRespVO;
import cn.dh.oa.module.oa.dal.dataobject.correction.BillCorrectionStateDO;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.module.oa.dal.mysql.correction.BillCorrectionStateMapper;
import cn.dh.oa.module.oa.dal.mysql.correction.CorrectionBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.enums.correction.OaBillCorrectionStatusEnum;
import cn.dh.oa.module.oa.service.correction.dto.BillCorrectionSourceDTO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 会长纠错：列表/详情展示层叠加「会长异议/纠错」标记
 */
@Component
public class BillCorrectionDisplayEnricher {

    @Resource
    private BillCorrectionStateMapper billCorrectionStateMapper;
    @Resource
    private CorrectionBillMapper correctionBillMapper;
    @Resource
    private BillCorrectionSourceService billCorrectionSourceService;

    /**
     * 纠错管理列表：单据状态展示来源单实时流程状态，并套用会长纠错互斥规则
     */
    public void enrichCorrectionBills(List<CorrectionBillRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        list.forEach(vo -> {
            if (StrUtil.isNotBlank(vo.getSourceBillType()) && vo.getSourceBillId() != null) {
                BillCorrectionSourceDTO source = billCorrectionSourceService.loadRequired(
                        vo.getSourceBillType(), vo.getSourceBillId());
                vo.setSourceBillProcessStatus(source.getProcessStatus());
            }
            boolean awaitingResubmit = shouldAwaitResubmitOnCorrectionBill(vo);
            vo.setPresidentCorrectionDisplay(awaitingResubmit);
            vo.setPresidentCorrectionAwaitingResubmit(awaitingResubmit);
        });
    }

    private boolean shouldAwaitResubmitOnCorrectionBill(CorrectionBillRespVO vo) {
        if (!Objects.equals(vo.getFreezeStatus(), 1)
                || !OaBillCorrectionStatusEnum.shouldDisplayOverlay(vo.getCorrectionStatus())) {
            return false;
        }
        return StrUtil.isBlank(vo.getNewProcessInstanceId());
    }

    public void enrichProjectBills(List<ProjectInitiationBillRespVO> list) {
        enrich(list, OaBillTypeEnum.OA_PROJECT_INITIATION_BILL.getProcessDefinitionKey(),
                ProjectInitiationBillRespVO::getId,
                ProjectInitiationBillRespVO::setPresidentCorrectionDisplay,
                ProjectInitiationBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichContractBills(List<ContractBillRespVO> list) {
        enrich(list, OaBillTypeEnum.OA_CONTRACT_BILL.getProcessDefinitionKey(),
                ContractBillRespVO::getId,
                ContractBillRespVO::setPresidentCorrectionDisplay,
                ContractBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichDocumentBills(List<DocumentDispatchBillRespVO> list) {
        enrich(list, OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL.getProcessDefinitionKey(),
                DocumentDispatchBillRespVO::getId,
                DocumentDispatchBillRespVO::setPresidentCorrectionDisplay,
                DocumentDispatchBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichExpenseBills(List<ExpenseReimburseBillRespVO> list) {
        enrichByBillType(list,
                vo -> (vo.getBillType() != null && vo.getBillType() == 1)
                        ? OaBillTypeEnum.OA_DAILY_EXPENSE_BILL.getProcessDefinitionKey()
                        : OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getProcessDefinitionKey(),
                ExpenseReimburseBillRespVO::getId,
                ExpenseReimburseBillRespVO::setPresidentCorrectionDisplay,
                ExpenseReimburseBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichExpensePaymentBills(List<ExpensePaymentBillRespVO> list) {
        enrich(list, OaBillTypeEnum.OA_EXPENSE_PAYMENT_BILL.getProcessDefinitionKey(),
                ExpensePaymentBillRespVO::getId,
                ExpensePaymentBillRespVO::setPresidentCorrectionDisplay,
                ExpensePaymentBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichSealApplyBills(List<SealApplyBillRespVO> list) {
        enrich(list, OaBillTypeEnum.OA_SEAL_APPLY_BILL.getProcessDefinitionKey(),
                SealApplyBillRespVO::getId,
                SealApplyBillRespVO::setPresidentCorrectionDisplay,
                SealApplyBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichTravelBills(List<TravelApplyBillRespVO> list) {
        enrichByBillType(list,
                vo -> (vo.getTravelType() != null && vo.getTravelType() == 2)
                        ? OaBillTypeEnum.OA_OVERSEAS_TRAVEL_APPLY_BILL.getProcessDefinitionKey()
                        : OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getProcessDefinitionKey(),
                TravelApplyBillRespVO::getId,
                TravelApplyBillRespVO::setPresidentCorrectionDisplay,
                TravelApplyBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichCarApplyBills(List<CarApplyBillRespVO> list) {
        enrich(list, OaBillTypeEnum.OA_CAR_APPLY_BILL.getProcessDefinitionKey(),
                CarApplyBillRespVO::getId,
                CarApplyBillRespVO::setPresidentCorrectionDisplay,
                CarApplyBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichCarReturnBills(List<CarReturnBillRespVO> list) {
        enrich(list, OaBillTypeEnum.OA_CAR_RETURN_BILL.getProcessDefinitionKey(),
                CarReturnBillRespVO::getId,
                CarReturnBillRespVO::setPresidentCorrectionDisplay,
                CarReturnBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichMeetingRoomBookings(List<MeetingRoomBookingRespVO> list) {
        enrich(list, OaBillTypeEnum.OA_MEETING_ROOM_BOOKING.getProcessDefinitionKey(),
                MeetingRoomBookingRespVO::getId,
                MeetingRoomBookingRespVO::setPresidentCorrectionDisplay,
                MeetingRoomBookingRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichIncomingDocumentBills(List<IncomingDocumentBillRespVO> list) {
        enrich(list, OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL.getProcessDefinitionKey(),
                IncomingDocumentBillRespVO::getId,
                IncomingDocumentBillRespVO::setPresidentCorrectionDisplay,
                IncomingDocumentBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    public void enrichReceptionApplyBills(List<ReceptionApplyBillRespVO> list) {
        enrich(list, OaBillTypeEnum.OA_RECEPTION_APPLY_BILL.getProcessDefinitionKey(),
                ReceptionApplyBillRespVO::getId,
                ReceptionApplyBillRespVO::setPresidentCorrectionDisplay,
                ReceptionApplyBillRespVO::setPresidentCorrectionAwaitingResubmit);
    }

    private <T> void enrichByBillType(List<T> list, Function<T, String> billTypeGetter, Function<T, Long> idGetter,
                                      BiConsumer<T, Boolean> displaySetter,
                                      BiConsumer<T, Boolean> awaitingSetter) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        list.stream().filter(vo -> idGetter.apply(vo) != null)
                .collect(Collectors.groupingBy(billTypeGetter))
                .forEach((billType, items) -> enrich(items, billType, idGetter, displaySetter, awaitingSetter));
    }

    private <T> void enrich(List<T> list, String billType, Function<T, Long> idGetter,
                            BiConsumer<T, Boolean> displaySetter,
                            BiConsumer<T, Boolean> awaitingSetter) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Collection<Long> billIds = list.stream().map(idGetter).filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollUtil.isEmpty(billIds)) {
            return;
        }
        Map<Long, BillCorrectionStateDO> stateMap = billCorrectionStateMapper.selectListByBillIds(billType, billIds)
                .stream().collect(Collectors.toMap(BillCorrectionStateDO::getBillId, s -> s, (a, b) -> a));
        Map<Long, CorrectionBillDO> correctionMap = loadActiveCorrectionMap(stateMap.values());
        list.forEach(vo -> {
            Long billId = idGetter.apply(vo);
            BillCorrectionStateDO state = stateMap.get(billId);
            boolean awaitingResubmit = shouldAwaitResubmit(billType, billId, state, correctionMap);
            displaySetter.accept(vo, awaitingResubmit);
            awaitingSetter.accept(vo, awaitingResubmit);
        });
    }

    private Map<Long, CorrectionBillDO> loadActiveCorrectionMap(Collection<BillCorrectionStateDO> states) {
        Set<Long> correctionIds = states.stream()
                .map(BillCorrectionStateDO::getActiveCorrectionBillId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(correctionIds)) {
            return Map.of();
        }
        return correctionBillMapper.selectList(CorrectionBillDO::getId, correctionIds).stream()
                .collect(Collectors.toMap(CorrectionBillDO::getId, c -> c, (a, b) -> a));
    }

    private boolean shouldDisplay(BillCorrectionStateDO state) {
        return state != null
                && Objects.equals(state.getFreezeStatus(), 1)
                && OaBillCorrectionStatusEnum.shouldDisplayOverlay(state.getCorrectionStatus());
    }

    private boolean shouldAwaitResubmit(String billType, Long billId, BillCorrectionStateDO state,
                                        Map<Long, CorrectionBillDO> correctionMap) {
        if (!shouldDisplay(state)) {
            return false;
        }
        CorrectionBillDO correction = resolveCurrentCorrection(billType, billId, state, correctionMap);
        return correction == null || !hasResubmittedProcess(correction, billType, billId);
    }

    private CorrectionBillDO resolveCurrentCorrection(String billType, Long billId, BillCorrectionStateDO state,
                                                      Map<Long, CorrectionBillDO> correctionMap) {
        if (state.getActiveCorrectionBillId() != null) {
            return correctionMap.get(state.getActiveCorrectionBillId());
        }
        return correctionBillMapper.selectLatestBySourceBill(billType, billId);
    }

    private boolean hasResubmittedProcess(CorrectionBillDO correction, String billType, Long billId) {
        if (StrUtil.isNotBlank(correction.getNewProcessInstanceId())) {
            return true;
        }
        BillCorrectionSourceDTO source = billCorrectionSourceService.loadRequired(billType, billId);
        return StrUtil.isNotBlank(source.getProcessInstanceId())
                && !Objects.equals(source.getProcessInstanceId(), correction.getSourceProcessInstanceId());
    }

}
