package cn.dh.oa.module.oa.service.contract;

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
import cn.dh.oa.module.oa.controller.admin.contract.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.contract.ContractBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;

import cn.dh.oa.module.oa.dal.mysql.contract.ContractBillMapper;

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
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    public Long saveContractBill(ContractBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CONTRACT_BILL));
        }

        // 插入或更新
        ContractBillDO contractBill = BeanUtils.toBean(saveReqVO, ContractBillDO.class);
        contractBillMapper.insertOrUpdate(contractBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_CONTRACT_BILL.getTypeCode(), contractBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return contractBill.getId();
    }

    @Override
    public Long submitContractBill(ContractBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CONTRACT_BILL));
        }

        // 保存或更新
        ContractBillDO contractBill = BeanUtils.toBean(saveReqVO, ContractBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        contractBillMapper.insertOrUpdate(contractBill);

        // 智能提交 BPM 流程
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        // 添加合同审批单特有的流程变量
        processInstanceVariables.put(PV_CONTRACT_IS_MAJOR, saveReqVO.getIsMajor());
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
    public void deleteContractBill(Long id) {
        // 校验存在
        validateContractBillExists(id);
        // 删除
        contractBillMapper.deleteById(id);
    }

    @Override
    public void deleteContractBillListByIds(List<Long> ids) {
        // 删除
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
        ContractBillDO contractBill = contractBillMapper.selectById(id);
        if (contractBill == null) {
            return null;
        }

        ContractBillRespVO respVO = BeanUtils.toBean(contractBill, ContractBillRespVO.class);

        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_CONTRACT_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));

        return respVO;
    }

    @Override
    public PageResult<ContractBillDO> getContractBillPage(ContractBillPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return contractBillMapper.selectPage(pageReqVO);
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_CONTRACT_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新合同审批单流程状态，id: {}, status: {}", id, status);

        // 校验合同审批单存在
        validateContractBillExists(id);

        // 更新流程状态
        ContractBillDO updateObj = new ContractBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        contractBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 合同审批单流程状态更新成功，id: {}, status: {}", id, status);
    }

}
