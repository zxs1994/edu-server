package cn.dh.oa.module.oa.service.expense;

import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.util.bill.BillCodeUtils;
import cn.dh.oa.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.oa.module.bpm.api.task.BpmProcessInstanceApi;
import cn.dh.oa.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleService;
import cn.dh.oa.module.bpm.util.BpmProcessVariableUtils;
import cn.dh.oa.framework.common.service.FlowBillService;
import cn.dh.oa.common.server.attachment.service.AttachmentService;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.dh.oa.module.oa.controller.admin.expense.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseBillDO;
import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseDetailDO;
import cn.dh.oa.module.oa.dal.dataobject.travel.TravelApplyBillDO;
import cn.dh.oa.module.oa.controller.admin.travel.vo.TravelApplyBillRespVO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;

import cn.dh.oa.module.oa.dal.mysql.expense.ExpenseReimburseBillMapper;
import cn.dh.oa.module.oa.dal.mysql.expense.ExpenseReimburseDetailMapper;
import cn.dh.oa.module.oa.dal.mysql.travel.TravelApplyBillMapper;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;
import static cn.dh.oa.module.oa.enums.OaProcessVariableConstants.PV_EXPENSE_IS_LARGE_AMOUNT;

/**
 * 差旅报销单 Service 实现类
 *
 * @author 鼎衡
 */
@Slf4j
@Service
@Primary
@Validated
public class ExpenseReimburseBillServiceImpl implements ExpenseReimburseBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    protected ExpenseReimburseBillMapper expenseReimburseBillMapper;

    @Resource
    private ExpenseReimburseDetailMapper expenseReimburseDetailMapper;

    @Resource
    private TravelApplyBillMapper travelApplyBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private OaBillApprovalVisibleService oaBillApprovalVisibleService;

    /**
     * 根据 billType 获取对应的单据类型枚举
     */
    protected OaBillTypeEnum getBillTypeEnum(ExpenseReimburseBillSaveReqVO saveReqVO) {
        return (saveReqVO.getBillType() != null && saveReqVO.getBillType() == 1)
                ? OaBillTypeEnum.OA_DAILY_EXPENSE_BILL
                : OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveExpenseReimburseBill(ExpenseReimburseBillSaveReqVO saveReqVO) {
        OaBillTypeEnum billTypeEnum = getBillTypeEnum(saveReqVO);
        fillTotalAmount(saveReqVO);

        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, billTypeEnum));
        }

        String oldTravelBillCode = getOldTravelBillCode(saveReqVO.getId());

        // 插入或更新
        ExpenseReimburseBillDO expenseReimburseBill = BeanUtils.toBean(saveReqVO, ExpenseReimburseBillDO.class);
        expenseReimburseBillMapper.insertOrUpdate(expenseReimburseBill);

        syncTravelApplyLinks(expenseReimburseBill.getId(), expenseReimburseBill.getBillType(),
                saveReqVO.getTravelBillCode(), oldTravelBillCode);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(billTypeEnum.getTypeCode(), expenseReimburseBill.getId(), saveReqVO.getAttachments());
        }

        // 保存费用明细（先删后增）
        saveExpenseDetails(expenseReimburseBill.getId(), saveReqVO.getDetails());

        // 返回
        return expenseReimburseBill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitExpenseReimburseBill(ExpenseReimburseBillSaveReqVO saveReqVO) {
        OaBillTypeEnum billTypeEnum = getBillTypeEnum(saveReqVO);
        fillTotalAmount(saveReqVO);

        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, billTypeEnum));
        }

        String oldTravelBillCode = getOldTravelBillCode(saveReqVO.getId());

        // 保存或更新
        ExpenseReimburseBillDO expenseReimburseBill = BeanUtils.toBean(saveReqVO, ExpenseReimburseBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        expenseReimburseBillMapper.insertOrUpdate(expenseReimburseBill);

        syncTravelApplyLinks(expenseReimburseBill.getId(), expenseReimburseBill.getBillType(),
                saveReqVO.getTravelBillCode(), oldTravelBillCode);

        // 智能提交 BPM 流程
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        fillExpenseGatewayVariables(processInstanceVariables, saveReqVO.getTotalAmount());
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(billTypeEnum.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(expenseReimburseBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        expenseReimburseBillMapper.updateById(new ExpenseReimburseBillDO().setId(expenseReimburseBill.getId()).setProcessInstanceId(processInstanceId));

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(billTypeEnum.getTypeCode(), expenseReimburseBill.getId(), saveReqVO.getAttachments());
        }

        // 保存费用明细（先删后增）
        saveExpenseDetails(expenseReimburseBill.getId(), saveReqVO.getDetails());

        // 返回
        return expenseReimburseBill.getId();
    }

    @Override
    public Long createExpenseReimburseBill(ExpenseReimburseBillSaveReqVO createReqVO) {
        OaBillTypeEnum billTypeEnum = getBillTypeEnum(createReqVO);
        // 生成单号
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.OA, billTypeEnum);
        createReqVO.setBillCode(billCode);
        // 插入
        ExpenseReimburseBillDO expenseReimburseBill = BeanUtils.toBean(createReqVO, ExpenseReimburseBillDO.class);
        expenseReimburseBillMapper.insertOrUpdate(expenseReimburseBill);

        // 返回
        return expenseReimburseBill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExpenseReimburseBill(ExpenseReimburseBillSaveReqVO updateReqVO) {
        ExpenseReimburseBillDO existing = expenseReimburseBillMapper.selectById(updateReqVO.getId());
        if (existing == null) {
            throw exception(EXPENSE_REIMBURSE_BILL_NOT_EXISTS);
        }
        ExpenseReimburseBillDO updateObj = BeanUtils.toBean(updateReqVO, ExpenseReimburseBillDO.class);
        expenseReimburseBillMapper.updateById(updateObj);

        // 差旅报销标记已支付时，同步关联差旅申请为已报销
        if (Integer.valueOf(1).equals(updateReqVO.getPaymentStatus())
                && !Integer.valueOf(1).equals(existing.getPaymentStatus())
                && existing.getBillType() != null && existing.getBillType() != 1) {
            markTravelApplyBillsReimbursed(existing.getTravelBillCode());
        }
    }

    /** 将关联差旅申请单标记为已报销 */
    private void markTravelApplyBillsReimbursed(String travelBillCode) {
        if (StringUtils.isBlank(travelBillCode)) {
            return;
        }
        List<String> codeList = Arrays.stream(travelBillCode.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        if (codeList.isEmpty()) {
            return;
        }
        travelApplyBillMapper.selectList(
                new LambdaQueryWrapperX<TravelApplyBillDO>()
                        .in(TravelApplyBillDO::getBillCode, codeList)
        ).forEach(bill -> travelApplyBillMapper.updateById(
                new TravelApplyBillDO().setId(bill.getId()).setReimbursementStatus(1)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpenseReimburseBill(Long id) {
        // 校验存在
        validateExpenseReimburseBillExists(id);
        releaseTravelApplyLinks(id);
        // 删除费用明细
        expenseReimburseDetailMapper.deleteByBillId(id);
        // 删除主单
        expenseReimburseBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpenseReimburseBillListByIds(List<Long> ids) {
        for (Long id : ids) {
            releaseTravelApplyLinks(id);
            expenseReimburseDetailMapper.deleteByBillId(id);
        }
        // 删除主单
        expenseReimburseBillMapper.deleteByIds(ids);
    }

    private void validateExpenseReimburseBillExists(Long id) {
        if (expenseReimburseBillMapper.selectById(id) == null) {
            throw exception(EXPENSE_REIMBURSE_BILL_NOT_EXISTS);
        }
    }

    @Override
    public ExpenseReimburseBillDO getExpenseReimburseBill(Long id) {
        return expenseReimburseBillMapper.selectById(id);
    }

    @Override
    public ExpenseReimburseBillRespVO getExpenseReimburseBillInfo(Long id) {
        validateExpenseReimburseBillExists(id);
        ExpenseReimburseBillDO expenseReimburseBill = expenseReimburseBillMapper.selectById(id);
        oaBillApprovalVisibleService.assertCanView(expenseReimburseBill.getCreator(), expenseReimburseBill.getProcessInstanceId());

        ExpenseReimburseBillRespVO respVO = BeanUtils.toBean(expenseReimburseBill, ExpenseReimburseBillRespVO.class);
        if (respVO.getTotalAmount() == null) {
            respVO.setTotalAmount(BigDecimal.ZERO);
        }

        // 根据 billType 确定附件类型编码
        String typeCode = (expenseReimburseBill.getBillType() != null && expenseReimburseBill.getBillType() == 1)
                ? OaBillTypeEnum.OA_DAILY_EXPENSE_BILL.getTypeCode()
                : OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getTypeCode();

        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(typeCode, id),
            AttachmentRespVO.class
        ));

        // 获取费用明细
        respVO.setDetails(BeanUtils.toBean(
            expenseReimburseDetailMapper.selectListByBillId(id),
            ExpenseReimburseDetailRespVO.class
        ));

        // 获取关联的差旅申请单列表
        if (StringUtils.isNotBlank(expenseReimburseBill.getTravelBillCode())) {
            String[] codes = expenseReimburseBill.getTravelBillCode().split(",");
            List<String> codeList = Arrays.stream(codes)
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());

            if (!codeList.isEmpty()) {
                List<TravelApplyBillDO> travelBills = travelApplyBillMapper.selectList(
                    new LambdaQueryWrapperX<TravelApplyBillDO>()
                        .in(TravelApplyBillDO::getBillCode, codeList)
                );
                List<TravelApplyBillRespVO> travelBillVOs = BeanUtils.toBean(travelBills, TravelApplyBillRespVO.class);
                respVO.setTravelBills(travelBillVOs);
                // 出差事由不落库，查询时从关联差旅申请单拼接
                String travelCause = travelBillVOs.stream()
                        .map(TravelApplyBillRespVO::getCause)
                        .filter(StringUtils::isNotBlank)
                        .distinct()
                        .collect(Collectors.joining("；"));
                respVO.setTravelCause(travelCause);
            }
        }

        return respVO;
    }

    @Override
    public PageResult<ExpenseReimburseBillDO> getExpenseReimburseBillPage(ExpenseReimburseBillPageReqVO pageReqVO) {
        return expenseReimburseBillMapper.selectPageByVisibleScope(pageReqVO, oaBillApprovalVisibleService.resolveCurrentUserScope());
    }

    /**
     * 保存费用明细（先删后增）
     */
    private void saveExpenseDetails(Long billId, List<ExpenseReimburseDetailSaveReqVO> details) {
        // 删除旧明细
        expenseReimburseDetailMapper.deleteByBillId(billId);
        // 插入新明细
        if (details != null && !details.isEmpty()) {
            for (ExpenseReimburseDetailSaveReqVO detail : details) {
                ExpenseReimburseDetailDO detailDO = BeanUtils.toBean(detail, ExpenseReimburseDetailDO.class);
                detailDO.setBillId(billId);
                detailDO.setId(null); // 确保是新插入
                expenseReimburseDetailMapper.insert(detailDO);
            }
        }
    }

    /** 报销总金额：差旅单按「明细合计 + 交通/伙食补贴」重算；日常单未填时默认 0 */
    private void fillTotalAmount(ExpenseReimburseBillSaveReqVO saveReqVO) {
        if (isTravelExpenseBill(saveReqVO.getBillType())) {
            saveReqVO.setTotalAmount(calcTravelReimburseTotal(saveReqVO));
            return;
        }
        if (saveReqVO.getTotalAmount() == null) {
            saveReqVO.setTotalAmount(BigDecimal.ZERO);
        }
    }

    private static final BigDecimal TRAFFIC_SUBSIDY_STANDARD = new BigDecimal("80");
    private static final BigDecimal MEAL_SUBSIDY_STANDARD = new BigDecimal("100");

    private BigDecimal calcTravelReimburseTotal(ExpenseReimburseBillSaveReqVO saveReqVO) {
        BigDecimal detailSum = BigDecimal.ZERO;
        if (saveReqVO.getDetails() != null) {
            for (ExpenseReimburseDetailSaveReqVO detail : saveReqVO.getDetails()) {
                if (detail.getAmount() != null) {
                    detailSum = detailSum.add(detail.getAmount());
                }
            }
        }
        return detailSum
                .add(subsidyAmount(saveReqVO.getTrafficSubsidyDays(), saveReqVO.getTrafficSubsidyPeople(), TRAFFIC_SUBSIDY_STANDARD))
                .add(subsidyAmount(saveReqVO.getMealSubsidyDays(), saveReqVO.getMealSubsidyPeople(), MEAL_SUBSIDY_STANDARD))
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private BigDecimal subsidyAmount(BigDecimal days, Integer people, BigDecimal standard) {
        BigDecimal d = days == null || days.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : days;
        BigDecimal p = people == null || people < 0 ? BigDecimal.ZERO : BigDecimal.valueOf(people);
        return standard.multiply(p).multiply(d);
    }

    /** 大额报销阈值（元），默认 50000 */
    private static final BigDecimal DEFAULT_EXPENSE_LARGE_THRESHOLD = new BigDecimal("50000");

    /** 补充费用报销流程网关变量 expenseIsLargeAmount */
    static void fillExpenseGatewayVariables(Map<String, Object> variables, BigDecimal totalAmount) {
        BigDecimal amount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        variables.put(PV_EXPENSE_IS_LARGE_AMOUNT, amount.compareTo(DEFAULT_EXPENSE_LARGE_THRESHOLD) > 0);
    }

    private String getOldTravelBillCode(Long expenseBillId) {
        if (expenseBillId == null) {
            return null;
        }
        ExpenseReimburseBillDO existing = expenseReimburseBillMapper.selectById(expenseBillId);
        return existing != null ? existing.getTravelBillCode() : null;
    }

    private boolean isTravelExpenseBill(Integer billType) {
        return billType == null || billType != 1;
    }

    private Set<String> parseTravelBillCodes(String travelBillCode) {
        if (StringUtils.isBlank(travelBillCode)) {
            return Collections.emptySet();
        }
        return Arrays.stream(travelBillCode.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    /** 保存/提交差旅报销单时，同步出差申请单的关联锁定 */
    private void syncTravelApplyLinks(Long expenseBillId, Integer billType,
                                      String newTravelBillCode, String oldTravelBillCode) {
        if (!isTravelExpenseBill(billType)) {
            return;
        }
        Set<String> newCodes = parseTravelBillCodes(newTravelBillCode);
        Set<String> oldCodes = parseTravelBillCodes(oldTravelBillCode);

        Set<String> toUnlock = new HashSet<>(oldCodes);
        toUnlock.removeAll(newCodes);
        if (!toUnlock.isEmpty()) {
            travelApplyBillMapper.update(null, new LambdaUpdateWrapper<TravelApplyBillDO>()
                    .set(TravelApplyBillDO::getLinkedExpenseBillId, null)
                    .in(TravelApplyBillDO::getBillCode, toUnlock)
                    .eq(TravelApplyBillDO::getLinkedExpenseBillId, expenseBillId));
        }

        Set<String> toLock = new HashSet<>(newCodes);
        toLock.removeAll(oldCodes);
        for (String code : toLock) {
            TravelApplyBillDO travel = travelApplyBillMapper.selectOne(TravelApplyBillDO::getBillCode, code);
            if (travel == null) {
                continue;
            }
            if (travel.getLinkedExpenseBillId() != null && !travel.getLinkedExpenseBillId().equals(expenseBillId)) {
                throw exception(TRAVEL_APPLY_ALREADY_LINKED);
            }
        }
        if (!newCodes.isEmpty()) {
            travelApplyBillMapper.update(null, new LambdaUpdateWrapper<TravelApplyBillDO>()
                    .set(TravelApplyBillDO::getLinkedExpenseBillId, expenseBillId)
                    .in(TravelApplyBillDO::getBillCode, newCodes));
        }
    }

    /** 删除差旅报销单时，释放已锁定的出差申请单 */
    private void releaseTravelApplyLinks(Long expenseBillId) {
        travelApplyBillMapper.update(null, new LambdaUpdateWrapper<TravelApplyBillDO>()
                .set(TravelApplyBillDO::getLinkedExpenseBillId, null)
                .eq(TravelApplyBillDO::getLinkedExpenseBillId, expenseBillId));
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新差旅报销单流程状态，id: {}, status: {}", id, status);

        // 校验差旅报销单存在
        validateExpenseReimburseBillExists(id);

        // 更新流程状态
        ExpenseReimburseBillDO updateObj = new ExpenseReimburseBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        expenseReimburseBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 差旅报销单流程状态更新成功，id: {}, status: {}", id, status);
    }

}
