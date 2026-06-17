package cn.dh.oa.module.oa.service.document;

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
import cn.dh.oa.module.oa.controller.admin.document.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.document.DocumentDispatchBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;

import cn.dh.oa.module.oa.dal.mysql.document.DocumentDispatchBillMapper;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;
import static cn.dh.oa.module.oa.enums.OaProcessVariableConstants.*;

/**
 * 公文发文单 Service 实现类
 *
 * @author 鼎衡
 */
@Slf4j
@Service
@Validated
public class DocumentDispatchBillServiceImpl implements DocumentDispatchBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private DocumentDispatchBillMapper documentDispatchBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    public Long saveDocumentDispatchBill(DocumentDispatchBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL));
        }

        // 插入或更新
        DocumentDispatchBillDO documentDispatchBill = BeanUtils.toBean(saveReqVO, DocumentDispatchBillDO.class);
        documentDispatchBillMapper.insertOrUpdate(documentDispatchBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL.getTypeCode(), documentDispatchBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return documentDispatchBill.getId();
    }

    @Override
    public Long submitDocumentDispatchBill(DocumentDispatchBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL));
        }

        // 保存或更新
        DocumentDispatchBillDO documentDispatchBill = BeanUtils.toBean(saveReqVO, DocumentDispatchBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        documentDispatchBillMapper.insertOrUpdate(documentDispatchBill);

        // 智能提交 BPM 流程
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(documentDispatchBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        documentDispatchBillMapper.updateById(new DocumentDispatchBillDO().setId(documentDispatchBill.getId()).setProcessInstanceId(processInstanceId));

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL.getTypeCode(), documentDispatchBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return documentDispatchBill.getId();
    }

    @Override
    public Long createDocumentDispatchBill(DocumentDispatchBillSaveReqVO createReqVO) {
        // 生成单号
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL);
        createReqVO.setBillCode(billCode);
        // 插入
        DocumentDispatchBillDO documentDispatchBill = BeanUtils.toBean(createReqVO, DocumentDispatchBillDO.class);
        documentDispatchBillMapper.insertOrUpdate(documentDispatchBill);

        // 返回
        return documentDispatchBill.getId();
    }

    @Override
    public void updateDocumentDispatchBill(DocumentDispatchBillSaveReqVO updateReqVO) {
        // 校验存在
        validateDocumentDispatchBillExists(updateReqVO.getId());
        // 更新
        DocumentDispatchBillDO updateObj = BeanUtils.toBean(updateReqVO, DocumentDispatchBillDO.class);
        documentDispatchBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteDocumentDispatchBill(Long id) {
        // 校验存在
        validateDocumentDispatchBillExists(id);
        // 删除
        documentDispatchBillMapper.deleteById(id);
    }

    @Override
    public void deleteDocumentDispatchBillListByIds(List<Long> ids) {
        // 删除
        documentDispatchBillMapper.deleteByIds(ids);
    }

    private void validateDocumentDispatchBillExists(Long id) {
        if (documentDispatchBillMapper.selectById(id) == null) {
            throw exception(DOCUMENT_DISPATCH_BILL_NOT_EXISTS);
        }
    }

    @Override
    public DocumentDispatchBillDO getDocumentDispatchBill(Long id) {
        return documentDispatchBillMapper.selectById(id);
    }

    @Override
    public DocumentDispatchBillRespVO getDocumentDispatchBillInfo(Long id) {
        DocumentDispatchBillDO documentDispatchBill = documentDispatchBillMapper.selectById(id);
        if (documentDispatchBill == null) {
            return null;
        }

        DocumentDispatchBillRespVO respVO = BeanUtils.toBean(documentDispatchBill, DocumentDispatchBillRespVO.class);

        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));

        return respVO;
    }

    @Override
    public PageResult<DocumentDispatchBillDO> getDocumentDispatchBillPage(DocumentDispatchBillPageReqVO pageReqVO) {
        return documentDispatchBillMapper.selectPage(pageReqVO);
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_DOCUMENT_DISPATCH_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新公文发文单流程状态，id: {}, status: {}", id, status);

        // 校验公文发文单存在
        validateDocumentDispatchBillExists(id);

        // 更新流程状态
        DocumentDispatchBillDO updateObj = new DocumentDispatchBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        documentDispatchBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 公文发文单流程状态更新成功，id: {}, status: {}", id, status);
    }

}
