package cn.dh.oa.module.oa.service.contract;

import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.util.bill.BillCodeUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import cn.dh.oa.module.oa.controller.admin.contract.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractBillDO;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractCodeSeqDO;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractDetailDO;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractPaymentPlanDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;

import cn.dh.oa.module.oa.dal.mysql.contract.ContractBillMapper;
import cn.dh.oa.module.oa.dal.mysql.contract.ContractCodeSeqMapper;
import cn.dh.oa.module.oa.dal.mysql.contract.ContractDetailMapper;
import cn.dh.oa.module.oa.dal.mysql.contract.ContractPaymentPlanMapper;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;
import static cn.dh.oa.module.oa.enums.OaProcessVariableConstants.*;

/**
 * 合同审批单 Service 实现类
 *
 * @author 鼎衡
 */
@Slf4j
@Service
@Validated
public class ContractBillServiceImpl implements ContractBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private ContractBillMapper contractBillMapper;

    @Resource
    private ContractDetailMapper contractDetailMapper;

    @Resource
    private ContractPaymentPlanMapper contractPaymentPlanMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private OaBillApprovalVisibleService oaBillApprovalVisibleService;

    @Resource
    private ContractCodeSeqMapper contractCodeSeqMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveContractBill(ContractBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CONTRACT_BILL));
        }

        // 合同编号由审批通过后自动生成，保存阶段不允许手填
        saveReqVO.setContractCode(null);
        // 插入或更新
        ContractBillDO contractBill = BeanUtils.toBean(saveReqVO, ContractBillDO.class);
        contractBillMapper.insertOrUpdate(contractBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_CONTRACT_BILL.getTypeCode(), contractBill.getId(), saveReqVO.getAttachments());
        }

        // 保存合同明细（先删后增）
        saveContractDetails(contractBill.getId(), saveReqVO.getContractDetails());

        // 保存收付款计划（先删后增）
        savePaymentPlans(contractBill.getId(), saveReqVO.getPaymentPlans());

        // 返回
        return contractBill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitContractBill(ContractBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CONTRACT_BILL));
        }

        // 合同编号由审批通过后自动生成，提交阶段不允许手填
        saveReqVO.setContractCode(null);
        // 保存或更新
        ContractBillDO contractBill = BeanUtils.toBean(saveReqVO, ContractBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        contractBillMapper.insertOrUpdate(contractBill);

        // 智能提交 BPM 流程
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        // 添加合同审批单特有的流程变量（网关条件期望布尔值）
        processInstanceVariables.put(PV_CONTRACT_IS_MAJOR, saveReqVO.getIsMajor() != null && saveReqVO.getIsMajor() == 1);
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(OaBillTypeEnum.OA_CONTRACT_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(contractBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        contractBillMapper.updateById(new ContractBillDO().setId(contractBill.getId()).setProcessInstanceId(processInstanceId));

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_CONTRACT_BILL.getTypeCode(), contractBill.getId(), saveReqVO.getAttachments());
        }

        // 保存合同明细（先删后增）
        saveContractDetails(contractBill.getId(), saveReqVO.getContractDetails());

        // 保存收付款计划（先删后增）
        savePaymentPlans(contractBill.getId(), saveReqVO.getPaymentPlans());

        // 返回
        return contractBill.getId();
    }

    @Override
    public Long createContractBill(ContractBillSaveReqVO createReqVO) {
        // 生成单号
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CONTRACT_BILL);
        createReqVO.setBillCode(billCode);
        // 插入
        ContractBillDO contractBill = BeanUtils.toBean(createReqVO, ContractBillDO.class);
        contractBillMapper.insertOrUpdate(contractBill);

        // 返回
        return contractBill.getId();
    }

    @Override
    public void updateContractBill(ContractBillSaveReqVO updateReqVO) {
        // 校验存在
        validateContractBillExists(updateReqVO.getId());
        // 更新
        ContractBillDO updateObj = BeanUtils.toBean(updateReqVO, ContractBillDO.class);
        contractBillMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContractBill(Long id) {
        // 校验存在
        validateContractBillExists(id);
        // 删除合同明细和收付款计划
        contractDetailMapper.deleteByBillId(id);
        contractPaymentPlanMapper.deleteByBillId(id);
        // 删除主单
        contractBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContractBillListByIds(List<Long> ids) {
        // 删除关联的合同明细和收付款计划
        for (Long id : ids) {
            contractDetailMapper.deleteByBillId(id);
            contractPaymentPlanMapper.deleteByBillId(id);
        }
        // 删除主单
        contractBillMapper.deleteByIds(ids);
    }

    private void validateContractBillExists(Long id) {
        if (contractBillMapper.selectById(id) == null) {
            throw exception(CONTRACT_BILL_NOT_EXISTS);
        }
    }

    @Override
    public ContractBillDO getContractBill(Long id) {
        return contractBillMapper.selectById(id);
    }

    @Override
    public ContractBillRespVO getContractBillInfo(Long id) {
        validateContractBillExists(id);
        ContractBillDO contractBill = contractBillMapper.selectById(id);
        oaBillApprovalVisibleService.assertCanView(contractBill.getCreator(), contractBill.getProcessInstanceId());

        ContractBillRespVO respVO = BeanUtils.toBean(contractBill, ContractBillRespVO.class);

        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_CONTRACT_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));

        // 获取合同明细
        respVO.setContractDetails(BeanUtils.toBean(
            contractDetailMapper.selectListByBillId(id),
            ContractDetailRespVO.class
        ));

        // 获取收付款计划
        respVO.setPaymentPlans(BeanUtils.toBean(
            contractPaymentPlanMapper.selectListByBillId(id),
            ContractPaymentPlanRespVO.class
        ));

        return respVO;
    }

    @Override
    public PageResult<ContractBillDO> getContractBillPage(ContractBillPageReqVO pageReqVO) {
        return contractBillMapper.selectPageByVisibleScope(pageReqVO, oaBillApprovalVisibleService.resolveCurrentUserScope());
    }

    // ==================== 合同明细和收付款计划 ====================

    /**
     * 保存合同明细（先删后增）
     */
    private void saveContractDetails(Long billId, List<ContractDetailSaveReqVO> details) {
        // 删除旧明细
        contractDetailMapper.deleteByBillId(billId);
        // 插入新明细
        if (details != null && !details.isEmpty()) {
            for (ContractDetailSaveReqVO detail : details) {
                ContractDetailDO detailDO = BeanUtils.toBean(detail, ContractDetailDO.class);
                detailDO.setBillId(billId);
                detailDO.setId(null); // 确保是新插入
                // 服务端重新计算金额 = 数量 × 单价
                if (detailDO.getQuantity() != null && detailDO.getUnitPrice() != null) {
                    detailDO.setAmount(detailDO.getQuantity().multiply(detailDO.getUnitPrice()).setScale(2, RoundingMode.HALF_UP));
                }
                contractDetailMapper.insert(detailDO);
            }
        }
    }

    /**
     * 保存收付款计划（先删后增）
     */
    private void savePaymentPlans(Long billId, List<ContractPaymentPlanSaveReqVO> plans) {
        // 删除旧计划
        contractPaymentPlanMapper.deleteByBillId(billId);
        // 插入新计划
        if (plans != null && !plans.isEmpty()) {
            for (ContractPaymentPlanSaveReqVO plan : plans) {
                ContractPaymentPlanDO planDO = BeanUtils.toBean(plan, ContractPaymentPlanDO.class);
                planDO.setBillId(billId);
                planDO.setId(null); // 确保是新插入
                contractPaymentPlanMapper.insert(planDO);
            }
        }
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_CONTRACT_BILL;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新合同审批单流程状态，id: {}, status: {}", id, status);

        ContractBillDO contractBill = contractBillMapper.selectById(id);
        if (contractBill == null) {
            throw exception(CONTRACT_BILL_NOT_EXISTS);
        }

        ContractBillDO updateObj = new ContractBillDO().setId(id).setProcessStatus(status);
        if (BpmTaskStatusEnum.APPROVE.getStatus().equals(status) && StringUtils.isBlank(contractBill.getContractCode())) {
            updateObj.setContractCode(generateContractCode(contractBill));
        }
        contractBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 合同审批单流程状态更新成功，id: {}, status: {}", id, status);
    }

    private String generateContractCode(ContractBillDO contractBill) {
        String typeCode = resolveContractTypeCode(contractBill);
        int year = contractBill.getSignDate() == null ? LocalDate.now().getYear() : contractBill.getSignDate().getYear();
        ContractCodeSeqDO seq = contractCodeSeqMapper.selectByYearAndTypeForUpdate(year, typeCode);
        if (seq == null) {
            contractCodeSeqMapper.insert(ContractCodeSeqDO.builder()
                    .bizYear(year)
                    .typeCode(typeCode)
                    .currentSeq(0)
                    .build());
            seq = contractCodeSeqMapper.selectByYearAndTypeForUpdate(year, typeCode);
        }
        int nextSeq = seq.getCurrentSeq() + 1;
        if (nextSeq > 999) {
            throw exception(CONTRACT_BILL_NOT_EXISTS, "合同编号已超过当年该类型上限(999)");
        }
        contractCodeSeqMapper.updateById(ContractCodeSeqDO.builder().id(seq.getId()).currentSeq(nextSeq).build());
        return String.format("%dCMPA-%s-%03d", year, typeCode, nextSeq);
    }

    private String resolveContractTypeCode(ContractBillDO contractBill) {
        // 按当前字典实际顺序映射：1项目服务(X) 2采购(C) 3服务外包(F) 4劳动/人事(H) 5合作(Z) 6其他(Q)
        Integer contractType = contractBill.getContractType();
        if (contractType == null) {
            throw exception(CONTRACT_BILL_NOT_EXISTS, "合同类型为空，无法生成合同编号");
        }
        return switch (contractType) {
            case 1 -> "X";
            case 2 -> "C";
            case 3 -> "F";
            case 4 -> "H";
            case 5 -> "Z";
            case 6 -> "Q";
            default -> throw exception(CONTRACT_BILL_NOT_EXISTS, "合同类型不支持自动生成编号");
        };
    }

}
