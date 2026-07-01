package cn.dh.oa.module.oa.service.incoming;

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

import cn.dh.oa.module.oa.controller.admin.incoming.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.incoming.IncomingDocumentBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.module.oa.dal.mysql.incoming.IncomingDocumentBillMapper;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;
import static cn.dh.oa.module.oa.enums.OaProcessVariableConstants.*;

@Slf4j
@Service
@Validated
public class IncomingDocumentBillServiceImpl implements IncomingDocumentBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private IncomingDocumentBillMapper incomingDocumentBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    public Long saveIncomingDocumentBill(IncomingDocumentBillSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL));
        }
        IncomingDocumentBillDO bill = BeanUtils.toBean(saveReqVO, IncomingDocumentBillDO.class);
        incomingDocumentBillMapper.insertOrUpdate(bill);
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL.getTypeCode(), bill.getId(), saveReqVO.getAttachments());
        }
        return bill.getId();
    }

    @Override
    public Long submitIncomingDocumentBill(IncomingDocumentBillSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL));
        }
        IncomingDocumentBillDO bill = BeanUtils.toBean(saveReqVO, IncomingDocumentBillDO.class).setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        incomingDocumentBillMapper.insertOrUpdate(bill);
        Map<String, Object> vars = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        // 收文特有流程变量：紧急程度
        vars.put("incomingUrgencyLevel", saveReqVO.getUrgencyLevel());
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL.getProcessDefinitionKey())
                        .setVariables(vars)
                        .setBusinessKey(String.valueOf(bill.getId()))
        ).getCheckedData();
        incomingDocumentBillMapper.updateById(new IncomingDocumentBillDO().setId(bill.getId()).setProcessInstanceId(processInstanceId));
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL.getTypeCode(), bill.getId(), saveReqVO.getAttachments());
        }
        return bill.getId();
    }

    @Override
    public Long createIncomingDocumentBill(IncomingDocumentBillSaveReqVO createReqVO) {
        IncomingDocumentBillDO bill = BeanUtils.toBean(createReqVO, IncomingDocumentBillDO.class);
        incomingDocumentBillMapper.insert(bill);
        return bill.getId();
    }

    @Override
    public void updateIncomingDocumentBill(IncomingDocumentBillSaveReqVO updateReqVO) {
        validateIncomingDocumentBillExists(updateReqVO.getId());
        IncomingDocumentBillDO updateObj = BeanUtils.toBean(updateReqVO, IncomingDocumentBillDO.class);
        incomingDocumentBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteIncomingDocumentBill(Long id) {
        validateIncomingDocumentBillExists(id);
        incomingDocumentBillMapper.deleteById(id);
    }

    @Override
    public void deleteIncomingDocumentBillListByIds(List<Long> ids) {
        ids.forEach(this::validateIncomingDocumentBillExists);
        incomingDocumentBillMapper.deleteByIds(ids);
    }

    @Override
    public IncomingDocumentBillDO getIncomingDocumentBill(Long id) {
        return incomingDocumentBillMapper.selectById(id);
    }

    @Override
    public IncomingDocumentBillRespVO getIncomingDocumentBillInfo(Long id) {
        validateIncomingDocumentBillExists(id);
        IncomingDocumentBillDO bill = incomingDocumentBillMapper.selectById(id);
        IncomingDocumentBillRespVO respVO = BeanUtils.toBean(bill, IncomingDocumentBillRespVO.class);
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        return respVO;
    }

    @Override
    public PageResult<IncomingDocumentBillDO> getIncomingDocumentBillPage(IncomingDocumentBillPageReqVO pageReqVO) {
        return incomingDocumentBillMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateHandlingStatus(Long id, Integer handlingStatus) {
        validateIncomingDocumentBillExists(id);
        incomingDocumentBillMapper.updateById(new IncomingDocumentBillDO().setId(id).setHandlingStatus(handlingStatus));
    }

    @Override
    public void updateHandlingResult(Long id, String handlingResult) {
        validateIncomingDocumentBillExists(id);
        incomingDocumentBillMapper.updateById(new IncomingDocumentBillDO().setId(id).setHandlingResult(handlingResult));
    }

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_INCOMING_DOCUMENT_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        incomingDocumentBillMapper.updateById(new IncomingDocumentBillDO().setId(id).setProcessStatus(status));
    }

    private void validateIncomingDocumentBillExists(Long id) {
        if (incomingDocumentBillMapper.selectById(id) == null) {
            throw exception(INCOMING_DOCUMENT_BILL_NOT_EXISTS);
        }
    }

}
