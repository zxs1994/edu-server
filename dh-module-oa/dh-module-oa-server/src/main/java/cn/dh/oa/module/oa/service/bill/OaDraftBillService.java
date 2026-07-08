package cn.dh.oa.module.oa.service.bill;

import cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.oa.module.oa.api.bill.OaDraftBillApi;
import cn.dh.oa.module.oa.api.bill.dto.OaDraftBillQueryDTO;
import cn.dh.oa.module.oa.api.bill.dto.OaDraftBillRespDTO;
import cn.dh.oa.module.oa.dal.dataobject.car.CarApplyBillDO;
import cn.dh.oa.module.oa.dal.dataobject.car.CarReturnBillDO;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractBillDO;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.module.oa.dal.dataobject.document.DocumentDispatchBillDO;
import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseBillDO;
import cn.dh.oa.module.oa.dal.dataobject.expensepayment.ExpensePaymentBillDO;
import cn.dh.oa.module.oa.dal.dataobject.incoming.IncomingDocumentBillDO;
import cn.dh.oa.module.oa.dal.dataobject.meetingroom.MeetingRoomBookingDO;
import cn.dh.oa.module.oa.dal.dataobject.project.ProjectInitiationBillDO;
import cn.dh.oa.module.oa.dal.dataobject.seal.SealApplyBillDO;
import cn.dh.oa.module.oa.dal.dataobject.travel.TravelApplyBillDO;
import cn.dh.oa.module.oa.dal.mysql.car.CarApplyBillMapper;
import cn.dh.oa.module.oa.dal.mysql.car.CarReturnBillMapper;
import cn.dh.oa.module.oa.dal.mysql.contract.ContractBillMapper;
import cn.dh.oa.module.oa.dal.mysql.correction.CorrectionBillMapper;
import cn.dh.oa.module.oa.dal.mysql.document.DocumentDispatchBillMapper;
import cn.dh.oa.module.oa.dal.mysql.expense.ExpenseReimburseBillMapper;
import cn.dh.oa.module.oa.dal.mysql.expensepayment.ExpensePaymentBillMapper;
import cn.dh.oa.module.oa.dal.mysql.incoming.IncomingDocumentBillMapper;
import cn.dh.oa.module.oa.dal.mysql.meetingroom.MeetingRoomBookingMapper;
import cn.dh.oa.module.oa.dal.mysql.project.ProjectInitiationBillMapper;
import cn.dh.oa.module.oa.dal.mysql.seal.SealApplyBillMapper;
import cn.dh.oa.module.oa.dal.mysql.travel.TravelApplyBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 查询 OA 未提交草稿单据
 */
@Service
public class OaDraftBillService implements OaDraftBillApi {

    @Resource
    private CarApplyBillMapper carApplyBillMapper;
    @Resource
    private CarReturnBillMapper carReturnBillMapper;
    @Resource
    private SealApplyBillMapper sealApplyBillMapper;
    @Resource
    private MeetingRoomBookingMapper meetingRoomBookingMapper;
    @Resource
    private ContractBillMapper contractBillMapper;
    @Resource
    private DocumentDispatchBillMapper documentDispatchBillMapper;
    @Resource
    private ExpenseReimburseBillMapper expenseReimburseBillMapper;
    @Resource
    private ExpensePaymentBillMapper expensePaymentBillMapper;
    @Resource
    private ProjectInitiationBillMapper projectInitiationBillMapper;
    @Resource
    private TravelApplyBillMapper travelApplyBillMapper;
    @Resource
    private IncomingDocumentBillMapper incomingDocumentBillMapper;
    @Resource
    private CorrectionBillMapper correctionBillMapper;

    @Override
    public List<OaDraftBillRespDTO> listMyDraftBills(Long userId, OaDraftBillQueryDTO query) {
        if (userId == null) {
            return List.of();
        }
        OaDraftBillQueryDTO safeQuery = query != null ? query : new OaDraftBillQueryDTO();
        List<OaDraftBillRespDTO> result = new ArrayList<>();
        String creator = String.valueOf(userId);
        Integer notStart = BpmProcessInstanceStatusEnum.NOT_START.getStatus();

        if (shouldQuery(OaBillTypeEnum.OA_CAR_APPLY_BILL.getProcessDefinitionKey(), safeQuery)) {
            carApplyBillMapper.selectList(draftWrapper(CarApplyBillDO.class, creator, notStart, safeQuery, false))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_CAR_APPLY_BILL.getProcessDefinitionKey(), bill.getCause(),
                            bill.getCreateTime(), null, null,
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_CAR_RETURN_BILL.getProcessDefinitionKey(), safeQuery)) {
            carReturnBillMapper.selectList(draftWrapper(CarReturnBillDO.class, creator, notStart, safeQuery, false))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_CAR_RETURN_BILL.getProcessDefinitionKey(), bill.getCause(),
                            bill.getCreateTime(), null, null,
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_SEAL_APPLY_BILL.getProcessDefinitionKey(), safeQuery)) {
            sealApplyBillMapper.selectList(draftWrapper(SealApplyBillDO.class, creator, notStart, safeQuery, false))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_SEAL_APPLY_BILL.getProcessDefinitionKey(), bill.getCause(),
                            bill.getCreateTime(), null, null,
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_MEETING_ROOM_BOOKING.getProcessDefinitionKey(), safeQuery)) {
            meetingRoomBookingMapper.selectList(draftWrapper(MeetingRoomBookingDO.class, creator, notStart, safeQuery, true))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_MEETING_ROOM_BOOKING.getProcessDefinitionKey(), bill.getMeetingTitle(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_CONTRACT_BILL.getProcessDefinitionKey(), safeQuery)) {
            contractBillMapper.selectList(draftWrapper(ContractBillDO.class, creator, notStart, safeQuery, true))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_CONTRACT_BILL.getProcessDefinitionKey(), bill.getCause(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL.getProcessDefinitionKey(), safeQuery)) {
            documentDispatchBillMapper.selectList(draftWrapper(DocumentDispatchBillDO.class, creator, notStart, safeQuery, true))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL.getProcessDefinitionKey(), bill.getDocTitle(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getProcessDefinitionKey(), safeQuery)) {
            LambdaQueryWrapper<ExpenseReimburseBillDO> wrapper = draftWrapper(ExpenseReimburseBillDO.class,
                    creator, notStart, safeQuery, true);
            wrapper.eq(ExpenseReimburseBillDO::getBillType, 2);
            expenseReimburseBillMapper.selectList(wrapper)
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getProcessDefinitionKey(), bill.getCause(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_DAILY_EXPENSE_BILL.getProcessDefinitionKey(), safeQuery)) {
            LambdaQueryWrapper<ExpenseReimburseBillDO> wrapper = draftWrapper(ExpenseReimburseBillDO.class,
                    creator, notStart, safeQuery, true);
            wrapper.eq(ExpenseReimburseBillDO::getBillType, 1);
            expenseReimburseBillMapper.selectList(wrapper)
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_DAILY_EXPENSE_BILL.getProcessDefinitionKey(), bill.getCause(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_EXPENSE_PAYMENT_BILL.getProcessDefinitionKey(), safeQuery)) {
            expensePaymentBillMapper.selectList(draftWrapper(ExpensePaymentBillDO.class, creator, notStart, safeQuery, true))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_EXPENSE_PAYMENT_BILL.getProcessDefinitionKey(), bill.getCause(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_PROJECT_INITIATION_BILL.getProcessDefinitionKey(), safeQuery)) {
            projectInitiationBillMapper.selectList(draftWrapper(ProjectInitiationBillDO.class, creator, notStart, safeQuery, true))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_PROJECT_INITIATION_BILL.getProcessDefinitionKey(), bill.getProjectName(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getProcessDefinitionKey(), safeQuery)) {
            LambdaQueryWrapper<TravelApplyBillDO> wrapper = draftWrapper(TravelApplyBillDO.class, creator, notStart, safeQuery, true);
            wrapper.eq(TravelApplyBillDO::getTravelType, 1);
            travelApplyBillMapper.selectList(wrapper)
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getProcessDefinitionKey(), bill.getCause(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_OVERSEAS_TRAVEL_APPLY_BILL.getProcessDefinitionKey(), safeQuery)) {
            LambdaQueryWrapper<TravelApplyBillDO> wrapper = draftWrapper(TravelApplyBillDO.class, creator, notStart, safeQuery, true);
            wrapper.eq(TravelApplyBillDO::getTravelType, 2);
            travelApplyBillMapper.selectList(wrapper)
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_OVERSEAS_TRAVEL_APPLY_BILL.getProcessDefinitionKey(), bill.getCause(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL.getProcessDefinitionKey(), safeQuery)) {
            incomingDocumentBillMapper.selectList(draftWrapper(IncomingDocumentBillDO.class, creator, notStart, safeQuery, true))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL.getProcessDefinitionKey(), bill.getDocTitle(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }
        if (shouldQuery(OaBillTypeEnum.OA_CORRECTION_BILL.getProcessDefinitionKey(), safeQuery)) {
            correctionBillMapper.selectList(draftWrapper(CorrectionBillDO.class, creator, notStart, safeQuery, true))
                    .forEach(bill -> result.add(toDto(bill.getId(), bill.getBillCode(),
                            OaBillTypeEnum.OA_CORRECTION_BILL.getProcessDefinitionKey(), bill.getCorrectionReason(),
                            bill.getCreateTime(), bill.getCompanyId(), bill.getCompanyName(),
                            bill.getDeptId(), bill.getDeptName())));
        }

        result.sort(Comparator.comparing(OaDraftBillRespDTO::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    private boolean shouldQuery(String processDefinitionKey, OaDraftBillQueryDTO query) {
        if (StrUtil.isNotBlank(query.getProcessDefinitionKey())) {
            return processDefinitionKey.equals(query.getProcessDefinitionKey());
        }
        if (CollUtil.isNotEmpty(query.getProcessDefinitionKeys())) {
            return query.getProcessDefinitionKeys().contains(processDefinitionKey);
        }
        return true;
    }

    private <T> LambdaQueryWrapper<T> draftWrapper(Class<T> clazz, String creator, Integer notStart,
                                                   OaDraftBillQueryDTO query, boolean supportCompanyFilter) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>(clazz);
        wrapper.apply("creator = {0}", creator)
                .apply("process_status IN ({0}, 0)", notStart)
                .and(w -> w.apply("process_instance_id IS NULL").or().apply("process_instance_id = ''"));
        if (StrUtil.isNotBlank(query.getBillCode())) {
            wrapper.apply("bill_code LIKE {0}", "%" + query.getBillCode() + "%");
        }
        if (supportCompanyFilter && query.getCompanyId() != null) {
            wrapper.apply("company_id = {0}", query.getCompanyId());
        }
        if (query.getDeptId() != null) {
            wrapper.apply("dept_id = {0}", query.getDeptId());
        }
        if (query.getCreateTimeStart() != null) {
            wrapper.apply("create_time >= {0}", query.getCreateTimeStart());
        }
        if (query.getCreateTimeEnd() != null) {
            wrapper.apply("create_time <= {0}", query.getCreateTimeEnd());
        }
        return wrapper;
    }

    private OaDraftBillRespDTO toDto(Long billId, String billCode, String processDefinitionKey, String summary,
                                     java.time.LocalDateTime createTime, Long companyId, String companyName,
                                     Long deptId, String deptName) {
        OaDraftBillRespDTO dto = new OaDraftBillRespDTO();
        dto.setBillId(billId);
        dto.setBillCode(billCode);
        dto.setProcessDefinitionKey(processDefinitionKey);
        dto.setSummary(summary);
        dto.setCreateTime(createTime);
        dto.setCompanyId(companyId);
        dto.setCompanyName(companyName);
        dto.setDeptId(deptId);
        dto.setDeptName(deptName);
        return dto;
    }

}
