package cn.dh.oa.module.oa.service.bill;

import cn.dh.oa.module.oa.api.bill.OaBillExistenceApi;
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
import cn.dh.oa.module.oa.dal.mysql.reception.ReceptionApplyBillMapper;
import cn.dh.oa.module.oa.dal.mysql.travel.TravelApplyBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 判断 OA 业务单据是否仍存在
 */
@Service
public class OaBillExistenceService implements OaBillExistenceApi {

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
    @Resource
    private ReceptionApplyBillMapper receptionApplyBillMapper;

    @Override
    public boolean exists(String processDefinitionKey, String businessKey) {
        if (StrUtil.isBlank(businessKey)) {
            return true;
        }
        OaBillTypeEnum billType = OaBillTypeEnum.getByProcessDefinitionKey(processDefinitionKey);
        if (billType == null) {
            return true;
        }
        long id;
        try {
            id = Long.parseLong(businessKey);
        } catch (NumberFormatException ex) {
            return true;
        }
        return switch (billType) {
            case OA_CAR_APPLY_BILL, OA_CAR_APPLY_BILL_COPY -> carApplyBillMapper.selectById(id) != null;
            case OA_CAR_RETURN_BILL -> carReturnBillMapper.selectById(id) != null;
            case OA_SEAL_APPLY_BILL -> sealApplyBillMapper.selectById(id) != null;
            case OA_MEETING_ROOM_BOOKING -> meetingRoomBookingMapper.selectById(id) != null;
            case OA_CONTRACT_BILL -> contractBillMapper.selectById(id) != null;
            case OA_DOCUMENT_DISPATCH_BILL -> documentDispatchBillMapper.selectById(id) != null;
            case OA_EXPENSE_REIMBURSE_BILL, OA_DAILY_EXPENSE_BILL -> expenseReimburseBillMapper.selectById(id) != null;
            case OA_EXPENSE_PAYMENT_BILL -> expensePaymentBillMapper.selectById(id) != null;
            case OA_PROJECT_INITIATION_BILL -> projectInitiationBillMapper.selectById(id) != null;
            case OA_TRAVEL_APPLY_BILL, OA_OVERSEAS_TRAVEL_APPLY_BILL -> travelApplyBillMapper.selectById(id) != null;
            case OA_INCOMING_DOCUMENT_BILL -> incomingDocumentBillMapper.selectById(id) != null;
            case OA_CORRECTION_BILL -> correctionBillMapper.selectById(id) != null;
            case OA_RECEPTION_APPLY_BILL -> receptionApplyBillMapper.selectById(id) != null;
        };
    }

}
