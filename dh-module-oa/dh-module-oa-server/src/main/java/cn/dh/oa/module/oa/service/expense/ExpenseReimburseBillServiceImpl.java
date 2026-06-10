package cn.dh.oa.module.oa.service.expense;

import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.util.bill.BillCodeUtils;
import cn.dh.oa.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.oa.module.bpm.api.task.BpmProcessInstanceApi;
import cn.dh.oa.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.bpm.util.BpmProcessVariableUtils;
import cn.dh.oa.framework.common.service.FlowBillService;
import cn.dh.oa.common.server.attachment.service.AttachmentService;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.dh.oa.module.oa.controller.admin.expense.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.expense.ExpenseReimburseBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;

import cn.dh.oa.module.oa.dal.mysql.expense.ExpenseReimburseBillMapper;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;
import static cn.dh.oa.module.oa.enums.OaProcessVariableConstants.*;

/**
 * 费用报销单 Service 实现类
 *
 * @author 鼎衡
 */
@Slf4j
@Service
@Validated
public class ExpenseReimburseBillServiceImpl implements ExpenseReimburseBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private ExpenseReimburseBillMapper expenseReimburseBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    public Long saveExpenseReimburseBill(ExpenseReimburseBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL));
        }

        // 插入或更新
        ExpenseReimburseBillDO expenseReimburseBill = BeanUtils.toBean(saveReqVO, ExpenseReimburseBillDO.class);
        expenseReimburseBillMapper.insertOrUpdate(expenseReimburseBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getTypeCode(), expenseReimburseBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return expenseReimburseBill.getId();
    }

    @Override
    public Long submitExpenseReimburseBill(ExpenseReimburseBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL));
        }

        // 保存或更新
        ExpenseReimburseBillDO expenseReimburseBill = BeanUtils.toBean(saveReqVO, ExpenseReimburseBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        expenseReimburseBillMapper.insertOrUpdate(expenseReimburseBill);

        // 智能提交 BPM 流程
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        // 添加费用报销单特有的流程变量
        processInstanceVariables.put(PV_EXPENSE_IS_LARGE_AMOUNT, saveReqVO.getIsLargeAmount());
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(expenseReimburseBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        expenseReimburseBillMapper.updateById(new ExpenseReimburseBillDO().setId(expenseReimburseBill.getId()).setProcessInstanceId(processInstanceId));

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getTypeCode(), expenseReimburseBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return expenseReimburseBill.getId();
    }

    @Override
    public Long createExpenseReimburseBill(ExpenseReimburseBillSaveReqVO createReqVO) {
        // 生成单号
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL);
        createReqVO.setBillCode(billCode);
        // 插入
        ExpenseReimburseBillDO expenseReimburseBill = BeanUtils.toBean(createReqVO, ExpenseReimburseBillDO.class);
        expenseReimburseBillMapper.insertOrUpdate(expenseReimburseBill);

        // 返回
        return expenseReimburseBill.getId();
    }

    @Override
    public void updateExpenseReimburseBill(ExpenseReimburseBillSaveReqVO updateReqVO) {
        // 校验存在
        validateExpenseReimburseBillExists(updateReqVO.getId());
        // 更新
        ExpenseReimburseBillDO updateObj = BeanUtils.toBean(updateReqVO, ExpenseReimburseBillDO.class);
        expenseReimburseBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteExpenseReimburseBill(Long id) {
        // 校验存在
        validateExpenseReimburseBillExists(id);
        // 删除
        expenseReimburseBillMapper.deleteById(id);
    }

    @Override
    public void deleteExpenseReimburseBillListByIds(List<Long> ids) {
        // 删除
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
        ExpenseReimburseBillDO expenseReimburseBill = expenseReimburseBillMapper.selectById(id);
        if (expenseReimburseBill == null) {
            return null;
        }

        ExpenseReimburseBillRespVO respVO = BeanUtils.toBean(expenseReimburseBill, ExpenseReimburseBillRespVO.class);

        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));

        return respVO;
    }

    @Override
    public PageResult<ExpenseReimburseBillDO> getExpenseReimburseBillPage(ExpenseReimburseBillPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return expenseReimburseBillMapper.selectPage(pageReqVO);
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_EXPENSE_REIMBURSE_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新费用报销单流程状态，id: {}, status: {}", id, status);

        // 校验费用报销单存在
        validateExpenseReimburseBillExists(id);

        // 更新流程状态
        ExpenseReimburseBillDO updateObj = new ExpenseReimburseBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        expenseReimburseBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 费用报销单流程状态更新成功，id: {}, status: {}", id, status);
    }

}
