package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.module.oa.dal.dataobject.contract.ContractBillDO;
import cn.dh.oa.module.oa.dal.dataobject.document.DocumentDispatchBillDO;
import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseBillDO;
import cn.dh.oa.module.oa.dal.dataobject.project.ProjectInitiationBillDO;
import cn.dh.oa.module.oa.dal.dataobject.seal.SealApplyBillDO;
import cn.dh.oa.module.oa.dal.mysql.contract.ContractBillMapper;
import cn.dh.oa.module.oa.dal.mysql.document.DocumentDispatchBillMapper;
import cn.dh.oa.module.oa.dal.mysql.expense.ExpenseReimburseBillMapper;
import cn.dh.oa.module.oa.dal.mysql.project.ProjectInitiationBillMapper;
import cn.dh.oa.module.oa.dal.mysql.seal.SealApplyBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.correction.dto.BillCorrectionSourceDTO;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.bpm.enums.task.BpmProcessInstanceStatusEnum.RUNNING;
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

    @Override
    public BillCorrectionSourceDTO loadRequired(String billType, Long billId) {
        if (!SUPPORTED_BILL_TYPES.contains(billType)) {
            throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
        }
        OaBillTypeEnum typeEnum = OaBillTypeEnum.getByProcessDefinitionKey(billType);
        return switch (typeEnum) {
            case OA_CONTRACT_BILL -> fromContract(contractBillMapper.selectById(billId));
            case OA_EXPENSE_REIMBURSE_BILL -> fromExpense(expenseReimburseBillMapper.selectById(billId));
            case OA_SEAL_APPLY_BILL -> fromSeal(sealApplyBillMapper.selectById(billId));
            case OA_PROJECT_INITIATION_BILL -> fromProject(projectInitiationBillMapper.selectById(billId));
            case OA_DOCUMENT_DISPATCH_BILL -> fromDocument(documentDispatchBillMapper.selectById(billId));
            default -> throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
        };
    }

    @Override
    public void updateForReApproval(BillCorrectionSourceDTO source, String newProcessInstanceId) {
        Long billId = source.getBillId();
        String billType = source.getBillType();
        OaBillTypeEnum typeEnum = OaBillTypeEnum.getByProcessDefinitionKey(billType);
        switch (typeEnum) {
            case OA_CONTRACT_BILL -> contractBillMapper.updateById(new ContractBillDO()
                    .setId(billId).setProcessInstanceId(newProcessInstanceId).setProcessStatus(RUNNING.getStatus()));
            case OA_EXPENSE_REIMBURSE_BILL -> expenseReimburseBillMapper.updateById(new ExpenseReimburseBillDO()
                    .setId(billId).setProcessInstanceId(newProcessInstanceId).setProcessStatus(RUNNING.getStatus()));
            case OA_SEAL_APPLY_BILL -> sealApplyBillMapper.updateById(new SealApplyBillDO()
                    .setId(billId).setProcessInstanceId(newProcessInstanceId).setProcessStatus(RUNNING.getStatus()));
            case OA_PROJECT_INITIATION_BILL -> projectInitiationBillMapper.updateById(new ProjectInitiationBillDO()
                    .setId(billId).setProcessInstanceId(newProcessInstanceId).setProcessStatus(RUNNING.getStatus()));
            case OA_DOCUMENT_DISPATCH_BILL -> documentDispatchBillMapper.updateById(new DocumentDispatchBillDO()
                    .setId(billId).setProcessInstanceId(newProcessInstanceId).setProcessStatus(RUNNING.getStatus()));
            default -> throw exception(CORRECTION_BILL_TYPE_NOT_SUPPORTED);
        }
    }

    @Override
    public void updateForCouncilOverride(BillCorrectionSourceDTO source) {
        // 终态由 BillFreezeHandler.applyCouncilOverride 处理
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
