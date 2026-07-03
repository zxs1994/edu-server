package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.module.oa.dal.dataobject.car.CarApplyBillDO;
import cn.dh.oa.module.oa.dal.dataobject.car.CarReturnBillDO;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractBillDO;
import cn.dh.oa.module.oa.dal.dataobject.document.DocumentDispatchBillDO;
import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseBillDO;
import cn.dh.oa.module.oa.dal.dataobject.incoming.IncomingDocumentBillDO;
import cn.dh.oa.module.oa.dal.dataobject.meetingroom.MeetingRoomBookingDO;
import cn.dh.oa.module.oa.dal.dataobject.project.ProjectInitiationBillDO;
import cn.dh.oa.module.oa.dal.dataobject.seal.SealApplyBillDO;
import cn.dh.oa.module.oa.dal.dataobject.travel.TravelApplyBillDO;
import cn.dh.oa.module.oa.dal.mysql.car.CarApplyBillMapper;
import cn.dh.oa.module.oa.dal.mysql.car.CarReturnBillMapper;
import cn.dh.oa.module.oa.dal.mysql.contract.ContractBillMapper;
import cn.dh.oa.module.oa.dal.mysql.document.DocumentDispatchBillMapper;
import cn.dh.oa.module.oa.dal.mysql.expense.ExpenseReimburseBillMapper;
import cn.dh.oa.module.oa.dal.mysql.incoming.IncomingDocumentBillMapper;
import cn.dh.oa.module.oa.dal.mysql.meetingroom.MeetingRoomBookingMapper;
import cn.dh.oa.module.oa.dal.mysql.project.ProjectInitiationBillMapper;
import cn.dh.oa.module.oa.dal.mysql.seal.SealApplyBillMapper;
import cn.dh.oa.module.oa.dal.mysql.travel.TravelApplyBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.correction.dto.BillCorrectionSourceDTO;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum.NOT_START;
import static cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum.REJECT;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;

@Service
public class BillCorrectionSourceServiceImpl implements BillCorrectionSourceService {

    @Resource
    private ContractBillMapper contractBillMapper;
    @Resource
    private ExpenseReimburseBillMapper expenseReimburseBillMapper;
    @Resource
    private SealApplyBillMapper sealApplyBillMapper;
    @Resource
    private ProjectInitiationBillMapper projectInitiationBillMapper;
    @Resource
    private DocumentDispatchBillMapper documentDispatchBillMapper;
    @Resource
    private TravelApplyBillMapper travelApplyBillMapper;
    @Resource
    private CarApplyBillMapper carApplyBillMapper;
    @Resource
    private CarReturnBillMapper carReturnBillMapper;
    @Resource
    private MeetingRoomBookingMapper meetingRoomBookingMapper;
    @Resource
    private IncomingDocumentBillMapper incomingDocumentBillMapper;

    @Override
    public BillCorrectionSourceDTO loadRequired(String billType, Long billId) {
        if (!OaBillTypeEnum.isPresidentCorrectionSupported(billType)) {
            throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
        }
        OaBillTypeEnum typeEnum = OaBillTypeEnum.getByProcessDefinitionKey(billType);
        if (typeEnum == null) {
            throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
        }
        return switch (typeEnum) {
            case OA_CONTRACT_BILL -> fromContract(contractBillMapper.selectById(billId));
            case OA_EXPENSE_REIMBURSE_BILL -> fromExpense(expenseReimburseBillMapper.selectById(billId));
            case OA_DAILY_EXPENSE_BILL -> fromDailyExpense(expenseReimburseBillMapper.selectById(billId));
            case OA_SEAL_APPLY_BILL -> fromSeal(sealApplyBillMapper.selectById(billId));
            case OA_PROJECT_INITIATION_BILL -> fromProject(projectInitiationBillMapper.selectById(billId));
            case OA_DOCUMENT_DISPATCH_BILL -> fromDocument(documentDispatchBillMapper.selectById(billId));
            case OA_TRAVEL_APPLY_BILL, OA_OVERSEAS_TRAVEL_APPLY_BILL -> fromTravel(
                    travelApplyBillMapper.selectById(billId), billType);
            case OA_CAR_APPLY_BILL -> fromCarApply(carApplyBillMapper.selectById(billId));
            case OA_CAR_RETURN_BILL -> fromCarReturn(carReturnBillMapper.selectById(billId));
            case OA_MEETING_ROOM_BOOKING -> fromMeetingRoom(meetingRoomBookingMapper.selectById(billId));
            case OA_INCOMING_DOCUMENT_BILL -> fromIncoming(incomingDocumentBillMapper.selectById(billId));
            default -> throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
        };
    }

    @Override
    public boolean exists(String billType, Long billId) {
        if (billId == null || !OaBillTypeEnum.isPresidentCorrectionSupported(billType)) {
            return false;
        }
        OaBillTypeEnum typeEnum = OaBillTypeEnum.getByProcessDefinitionKey(billType);
        if (typeEnum == null) {
            return false;
        }
        return switch (typeEnum) {
            case OA_CONTRACT_BILL -> contractBillMapper.selectById(billId) != null;
            case OA_EXPENSE_REIMBURSE_BILL, OA_DAILY_EXPENSE_BILL ->
                    expenseReimburseBillMapper.selectById(billId) != null;
            case OA_SEAL_APPLY_BILL -> sealApplyBillMapper.selectById(billId) != null;
            case OA_PROJECT_INITIATION_BILL -> projectInitiationBillMapper.selectById(billId) != null;
            case OA_DOCUMENT_DISPATCH_BILL -> documentDispatchBillMapper.selectById(billId) != null;
            case OA_TRAVEL_APPLY_BILL, OA_OVERSEAS_TRAVEL_APPLY_BILL ->
                    travelApplyBillMapper.selectById(billId) != null;
            case OA_CAR_APPLY_BILL -> carApplyBillMapper.selectById(billId) != null;
            case OA_CAR_RETURN_BILL -> carReturnBillMapper.selectById(billId) != null;
            case OA_MEETING_ROOM_BOOKING -> meetingRoomBookingMapper.selectById(billId) != null;
            case OA_INCOMING_DOCUMENT_BILL -> incomingDocumentBillMapper.selectById(billId) != null;
            default -> false;
        };
    }

    @Override
    public void updateForReApproval(BillCorrectionSourceDTO source, String newProcessInstanceId) {
        resetBillProcess(source);
    }

    @Override
    public void applyReApprovalCompleted(String billType, Long billId, String processInstanceId, Integer status) {
        updateBillProcess(OaBillTypeEnum.getByProcessDefinitionKey(billType), billId, processInstanceId, status);
    }

    private void resetBillProcess(BillCorrectionSourceDTO source) {
        updateBillProcess(OaBillTypeEnum.getByProcessDefinitionKey(source.getBillType()),
                source.getBillId(), null, NOT_START.getStatus());
    }

    private void updateBillProcess(OaBillTypeEnum typeEnum, Long billId, String processInstanceId, Integer processStatus) {
        if (typeEnum == null) {
            throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
        }
        switch (typeEnum) {
            case OA_CONTRACT_BILL -> contractBillMapper.updateById(new ContractBillDO()
                    .setId(billId).setProcessInstanceId(processInstanceId).setProcessStatus(processStatus));
            case OA_EXPENSE_REIMBURSE_BILL, OA_DAILY_EXPENSE_BILL -> expenseReimburseBillMapper.updateById(
                    new ExpenseReimburseBillDO().setId(billId).setProcessInstanceId(processInstanceId).setProcessStatus(processStatus));
            case OA_SEAL_APPLY_BILL -> sealApplyBillMapper.updateById(new SealApplyBillDO()
                    .setId(billId).setProcessInstanceId(processInstanceId).setProcessStatus(processStatus));
            case OA_PROJECT_INITIATION_BILL -> projectInitiationBillMapper.updateById(new ProjectInitiationBillDO()
                    .setId(billId).setProcessInstanceId(processInstanceId).setProcessStatus(processStatus));
            case OA_DOCUMENT_DISPATCH_BILL -> documentDispatchBillMapper.updateById(new DocumentDispatchBillDO()
                    .setId(billId).setProcessInstanceId(processInstanceId).setProcessStatus(processStatus));
            case OA_TRAVEL_APPLY_BILL, OA_OVERSEAS_TRAVEL_APPLY_BILL -> travelApplyBillMapper.updateById(
                    new TravelApplyBillDO().setId(billId).setProcessInstanceId(processInstanceId).setProcessStatus(processStatus));
            case OA_CAR_APPLY_BILL -> carApplyBillMapper.updateById(new CarApplyBillDO()
                    .setId(billId).setProcessInstanceId(processInstanceId).setProcessStatus(processStatus));
            case OA_CAR_RETURN_BILL -> carReturnBillMapper.updateById(new CarReturnBillDO()
                    .setId(billId).setProcessInstanceId(processInstanceId).setProcessStatus(processStatus));
            case OA_MEETING_ROOM_BOOKING -> meetingRoomBookingMapper.updateById(new MeetingRoomBookingDO()
                    .setId(billId).setProcessInstanceId(processInstanceId).setProcessStatus(processStatus));
            case OA_INCOMING_DOCUMENT_BILL -> incomingDocumentBillMapper.updateById(new IncomingDocumentBillDO()
                    .setId(billId).setProcessInstanceId(processInstanceId).setProcessStatus(processStatus));
            default -> throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
        }
    }

    @Override
    public void updateForCouncilOverride(BillCorrectionSourceDTO source) {
        // 终态由 BillFreezeHandler.applyCouncilOverride 处理
    }

    @Override
    public void applyCouncilReject(String billType, Long billId) {
        updateBillProcess(OaBillTypeEnum.getByProcessDefinitionKey(billType), billId, null, REJECT.getStatus());
    }

    private BillCorrectionSourceDTO fromContract(ContractBillDO bill) {
        if (bill == null) {
            throw exception(CONTRACT_BILL_NOT_EXISTS);
        }
        return build(bill.getId(), OaBillTypeEnum.OA_CONTRACT_BILL.getProcessDefinitionKey(),
                bill.getBillCode(), bill.getContractTitle(), bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), bill.getCompanyId(), bill.getCompanyName(),
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO fromExpense(ExpenseReimburseBillDO bill) {
        if (bill == null) {
            throw exception(EXPENSE_REIMBURSE_BILL_NOT_EXISTS);
        }
        String title = StrUtil.blankToDefault(bill.getCause(), bill.getBillCode());
        return build(bill.getId(), OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getProcessDefinitionKey(),
                bill.getBillCode(), title, bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), bill.getCompanyId(), bill.getCompanyName(),
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO fromSeal(SealApplyBillDO bill) {
        if (bill == null) {
            throw exception(SEAL_APPLY_BILL_NOT_EXISTS);
        }
        return build(bill.getId(), OaBillTypeEnum.OA_SEAL_APPLY_BILL.getProcessDefinitionKey(),
                bill.getBillCode(), bill.getDocumentTitle(), bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), null, null,
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO fromProject(ProjectInitiationBillDO bill) {
        if (bill == null) {
            throw exception(PROJECT_INITIATION_BILL_NOT_EXISTS);
        }
        return build(bill.getId(), OaBillTypeEnum.OA_PROJECT_INITIATION_BILL.getProcessDefinitionKey(),
                bill.getBillCode(), bill.getProjectName(), bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), bill.getCompanyId(), bill.getCompanyName(),
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO fromDocument(DocumentDispatchBillDO bill) {
        if (bill == null) {
            throw exception(DOCUMENT_DISPATCH_BILL_NOT_EXISTS);
        }
        return build(bill.getId(), OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL.getProcessDefinitionKey(),
                bill.getBillCode(), bill.getDocTitle(), bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), bill.getCompanyId(), bill.getCompanyName(),
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO fromTravel(TravelApplyBillDO bill, String billType) {
        if (bill == null) {
            throw exception(TRAVEL_APPLY_BILL_NOT_EXISTS);
        }
        return build(bill.getId(), billType, bill.getBillCode(), bill.getCause(),
                bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), bill.getCompanyId(), bill.getCompanyName(),
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO fromCarApply(CarApplyBillDO bill) {
        if (bill == null) {
            throw exception(CAR_APPLY_BILL_NOT_EXISTS);
        }
        return build(bill.getId(), OaBillTypeEnum.OA_CAR_APPLY_BILL.getProcessDefinitionKey(),
                bill.getBillCode(), bill.getCause(), bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), null, null,
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO fromCarReturn(CarReturnBillDO bill) {
        if (bill == null) {
            throw exception(CAR_RETURN_BILL_NOT_EXISTS);
        }
        String title = StrUtil.blankToDefault(bill.getCause(), bill.getBillCode());
        return build(bill.getId(), OaBillTypeEnum.OA_CAR_RETURN_BILL.getProcessDefinitionKey(),
                bill.getBillCode(), title, bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), null, null,
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO fromMeetingRoom(MeetingRoomBookingDO bill) {
        if (bill == null) {
            throw exception(MEETING_ROOM_BOOKING_NOT_EXISTS);
        }
        return build(bill.getId(), OaBillTypeEnum.OA_MEETING_ROOM_BOOKING.getProcessDefinitionKey(),
                bill.getBillCode(), bill.getMeetingTitle(), bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), bill.getCompanyId(), bill.getCompanyName(),
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO fromIncoming(IncomingDocumentBillDO bill) {
        if (bill == null) {
            throw exception(INCOMING_DOCUMENT_BILL_NOT_EXISTS);
        }
        return build(bill.getId(), OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL.getProcessDefinitionKey(),
                bill.getBillCode(), bill.getDocTitle(), bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), bill.getCompanyId(), bill.getCompanyName(),
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO fromDailyExpense(ExpenseReimburseBillDO bill) {
        if (bill == null) {
            throw exception(DAILY_EXPENSE_BILL_NOT_EXISTS);
        }
        String title = StrUtil.blankToDefault(bill.getCause(), bill.getBillCode());
        return build(bill.getId(), OaBillTypeEnum.OA_DAILY_EXPENSE_BILL.getProcessDefinitionKey(),
                bill.getBillCode(), title, bill.getProcessInstanceId(), bill.getProcessStatus(),
                bill.getCreator(), bill.getCreatorName(), bill.getCompanyId(), bill.getCompanyName(),
                bill.getDeptId(), bill.getDeptName());
    }

    private BillCorrectionSourceDTO build(Long billId, String billType, String billCode, String title,
                                          String processInstanceId, Integer processStatus,
                                          String creator, String creatorName,
                                          Long companyId, String companyName, Long deptId, String deptName) {
        BillCorrectionSourceDTO dto = new BillCorrectionSourceDTO();
        dto.setBillId(billId);
        dto.setBillType(billType);
        dto.setBillCode(billCode);
        dto.setBillTitle(title);
        dto.setProcessInstanceId(processInstanceId);
        dto.setProcessStatus(processStatus);
        dto.setCreatorUserId(StrUtil.isNotBlank(creator) ? Long.parseLong(creator) : null);
        dto.setCreatorName(creatorName);
        dto.setCompanyId(companyId);
        dto.setCompanyName(companyName);
        dto.setDeptId(deptId);
        dto.setDeptName(deptName);
        return dto;
    }

}
