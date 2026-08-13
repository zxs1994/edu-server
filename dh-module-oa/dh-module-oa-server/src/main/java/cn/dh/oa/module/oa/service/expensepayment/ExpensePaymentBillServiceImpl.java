package cn.dh.oa.module.oa.service.expensepayment;

import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import cn.dh.oa.common.server.attachment.service.AttachmentService;
import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.service.FlowBillService;
import cn.dh.oa.framework.common.util.bill.BillCodeUtils;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.module.bpm.api.task.BpmProcessInstanceApi;
import cn.dh.oa.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.oa.module.bpm.util.BpmProcessInstanceCancelUtils;
import cn.dh.oa.module.bpm.util.BpmProcessVariableUtils;
import cn.dh.oa.module.oa.controller.admin.expensepayment.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.expensepayment.ExpensePaymentBillDO;
import cn.dh.oa.module.oa.dal.dataobject.expensepayment.ExpensePaymentDetailDO;
import cn.dh.oa.module.oa.dal.mysql.expensepayment.ExpensePaymentBillMapper;
import cn.dh.oa.module.oa.dal.mysql.expensepayment.ExpensePaymentDetailMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;
import static cn.dh.oa.module.oa.enums.OaProcessVariableConstants.PV_EXPENSE_IS_LARGE_AMOUNT;

@Slf4j
@Service
@Validated
public class ExpensePaymentBillServiceImpl implements ExpensePaymentBillService, FlowBillService<OaBillTypeEnum> {

    private static final BigDecimal DEFAULT_EXPENSE_LARGE_THRESHOLD = new BigDecimal("50000");

    @Resource
    private ExpensePaymentBillMapper expensePaymentBillMapper;
    @Resource
    private ExpensePaymentDetailMapper expensePaymentDetailMapper;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private OaBillApprovalVisibleService oaBillApprovalVisibleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveExpensePaymentBill(ExpensePaymentBillSaveReqVO saveReqVO) {
        fillTotalAmount(saveReqVO);
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_EXPENSE_PAYMENT_BILL));
        }
        ExpensePaymentBillDO bill = BeanUtils.toBean(saveReqVO, ExpensePaymentBillDO.class);
        expensePaymentBillMapper.insertOrUpdate(bill);
        saveAttachments(bill.getId(), saveReqVO);
        saveDetails(bill.getId(), saveReqVO.getDetails());
        return bill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitExpensePaymentBill(ExpensePaymentBillSaveReqVO saveReqVO) {
        validateDetailsForSubmit(saveReqVO.getDetails());
        fillTotalAmount(saveReqVO);
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_EXPENSE_PAYMENT_BILL));
        }
        ExpensePaymentBillDO bill = BeanUtils.toBean(saveReqVO, ExpensePaymentBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        expensePaymentBillMapper.insertOrUpdate(bill);

        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        BigDecimal amount = saveReqVO.getTotalAmount() != null ? saveReqVO.getTotalAmount() : BigDecimal.ZERO;
        processInstanceVariables.put(PV_EXPENSE_IS_LARGE_AMOUNT,
                amount.compareTo(DEFAULT_EXPENSE_LARGE_THRESHOLD) > 0);
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(OaBillTypeEnum.OA_EXPENSE_PAYMENT_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables)
                        .setBusinessKey(String.valueOf(bill.getId()))
        ).getCheckedData();
        expensePaymentBillMapper.updateById(new ExpensePaymentBillDO()
                .setId(bill.getId()).setProcessInstanceId(processInstanceId));

        saveAttachments(bill.getId(), saveReqVO);
        saveDetails(bill.getId(), saveReqVO.getDetails());
        return bill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpensePaymentBill(Long id) {
        validateExists(id);
        ExpensePaymentBillDO bill = expensePaymentBillMapper.selectById(id);
        BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, bill.getProcessInstanceId());
        expensePaymentDetailMapper.deleteByBillId(id);
        expensePaymentBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpensePaymentBillListByIds(List<Long> ids) {
        List<ExpensePaymentBillDO> bills = expensePaymentBillMapper.selectByIds(ids);
        for (ExpensePaymentBillDO bill : bills) {
            BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, bill.getProcessInstanceId());
        }
        for (Long id : ids) {
            expensePaymentDetailMapper.deleteByBillId(id);
        }
        expensePaymentBillMapper.deleteByIds(ids);
    }

    @Override
    public ExpensePaymentBillDO getExpensePaymentBill(Long id) {
        return expensePaymentBillMapper.selectById(id);
    }

    @Override
    public ExpensePaymentBillRespVO getExpensePaymentBillInfo(Long id) {
        validateExists(id);
        ExpensePaymentBillDO bill = expensePaymentBillMapper.selectById(id);
        oaBillApprovalVisibleService.assertCanView(bill.getCreator(), bill.getProcessInstanceId());
        ExpensePaymentBillRespVO respVO = BeanUtils.toBean(bill, ExpensePaymentBillRespVO.class);
        if (respVO.getTotalAmount() == null) {
            respVO.setTotalAmount(BigDecimal.ZERO);
        }
        respVO.setDetails(BeanUtils.toBean(expensePaymentDetailMapper.selectListByBillId(id), ExpensePaymentDetailRespVO.class));
        respVO.setAttachments(BeanUtils.toBean(
                attachmentService.getAttachmentListByBusiness(
                        OaBillTypeEnum.OA_EXPENSE_PAYMENT_BILL.getTypeCode(), id),
                AttachmentRespVO.class));
        return respVO;
    }

    @Override
    public PageResult<ExpensePaymentBillDO> getExpensePaymentBillPage(ExpensePaymentBillPageReqVO pageReqVO) {
        return expensePaymentBillMapper.selectPageByVisibleScope(pageReqVO, oaBillApprovalVisibleService.resolveCurrentUserScope());
    }

    private void validateExists(Long id) {
        if (expensePaymentBillMapper.selectById(id) == null) {
            throw exception(EXPENSE_PAYMENT_BILL_NOT_EXISTS);
        }
    }

    private void validateDetailsForSubmit(List<ExpensePaymentDetailSaveReqVO> details) {
        if (details == null || details.isEmpty()) {
            throw exception(EXPENSE_PAYMENT_BILL_DETAIL_REQUIRED);
        }
    }

    private void fillTotalAmount(ExpensePaymentBillSaveReqVO saveReqVO) {
        if (saveReqVO.getTotalAmount() == null) {
            saveReqVO.setTotalAmount(BigDecimal.ZERO);
        }
    }

    private void saveAttachments(Long billId, ExpensePaymentBillSaveReqVO saveReqVO) {
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(
                    OaBillTypeEnum.OA_EXPENSE_PAYMENT_BILL.getTypeCode(), billId, saveReqVO.getAttachments());
        }
    }

    private void saveDetails(Long billId, List<ExpensePaymentDetailSaveReqVO> details) {
        expensePaymentDetailMapper.deleteByBillId(billId);
        if (details == null || details.isEmpty()) {
            return;
        }
        int index = 0;
        for (ExpensePaymentDetailSaveReqVO detail : details) {
            ExpensePaymentDetailDO detailDO = BeanUtils.toBean(detail, ExpensePaymentDetailDO.class);
            detailDO.setBillId(billId);
            detailDO.setId(null);
            if (detailDO.getSortOrder() == null) {
                detailDO.setSortOrder(index++);
            }
            expensePaymentDetailMapper.insert(detailDO);
        }
    }

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_EXPENSE_PAYMENT_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        validateExists(id);
        expensePaymentBillMapper.updateById(new ExpensePaymentBillDO().setId(id).setProcessStatus(status));
        log.info("[updateProcessStatus] 费用支出申请流程状态更新，id: {}, status: {}", id, status);
    }

}
