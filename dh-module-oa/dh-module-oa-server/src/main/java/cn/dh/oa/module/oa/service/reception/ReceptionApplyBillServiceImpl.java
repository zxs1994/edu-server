package cn.dh.oa.module.oa.service.reception;

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
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillPageReqVO;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillRespVO;
import cn.dh.oa.module.oa.controller.admin.reception.vo.ReceptionApplyBillSaveReqVO;
import cn.dh.oa.module.oa.dal.dataobject.reception.ReceptionApplyBillDO;
import cn.dh.oa.module.oa.dal.mysql.reception.ReceptionApplyBillMapper;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.RECEPTION_APPLY_BILL_NOT_EXISTS;

@Slf4j
@Service
@Validated
public class ReceptionApplyBillServiceImpl implements ReceptionApplyBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private ReceptionApplyBillMapper receptionApplyBillMapper;
    @Resource
    private BpmProcessInstanceApi processInstanceApi;
    @Resource
    private AttachmentService attachmentService;
    @Resource
    private OaBillApprovalVisibleService oaBillApprovalVisibleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveReceptionApplyBill(ReceptionApplyBillSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_RECEPTION_APPLY_BILL));
        }
        ReceptionApplyBillDO bill = BeanUtils.toBean(saveReqVO, ReceptionApplyBillDO.class);
        receptionApplyBillMapper.insertOrUpdate(bill);
        saveAttachments(bill.getId(), saveReqVO);
        return bill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitReceptionApplyBill(ReceptionApplyBillSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_RECEPTION_APPLY_BILL));
        }
        ReceptionApplyBillDO bill = BeanUtils.toBean(saveReqVO, ReceptionApplyBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        receptionApplyBillMapper.insertOrUpdate(bill);

        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(OaBillTypeEnum.OA_RECEPTION_APPLY_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables)
                        .setBusinessKey(String.valueOf(bill.getId()))
        ).getCheckedData();
        receptionApplyBillMapper.updateById(new ReceptionApplyBillDO()
                .setId(bill.getId()).setProcessInstanceId(processInstanceId));
        saveAttachments(bill.getId(), saveReqVO);
        return bill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReceptionApplyBill(Long id) {
        validateExists(id);
        ReceptionApplyBillDO bill = receptionApplyBillMapper.selectById(id);
        BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, bill.getProcessInstanceId());
        receptionApplyBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReceptionApplyBillListByIds(List<Long> ids) {
        List<ReceptionApplyBillDO> bills = receptionApplyBillMapper.selectByIds(ids);
        for (ReceptionApplyBillDO bill : bills) {
            BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, bill.getProcessInstanceId());
        }
        receptionApplyBillMapper.deleteByIds(ids);
    }

    @Override
    public ReceptionApplyBillDO getReceptionApplyBill(Long id) {
        return receptionApplyBillMapper.selectById(id);
    }

    @Override
    public ReceptionApplyBillRespVO getReceptionApplyBillInfo(Long id) {
        validateExists(id);
        ReceptionApplyBillDO bill = receptionApplyBillMapper.selectById(id);
        oaBillApprovalVisibleService.assertCanView(bill.getCreator(), bill.getProcessInstanceId());
        ReceptionApplyBillRespVO respVO = BeanUtils.toBean(bill, ReceptionApplyBillRespVO.class);
        respVO.setAttachments(BeanUtils.toBean(
                attachmentService.getAttachmentListByBusiness(
                        OaBillTypeEnum.OA_RECEPTION_APPLY_BILL.getTypeCode(), id),
                AttachmentRespVO.class));
        return respVO;
    }

    @Override
    public PageResult<ReceptionApplyBillDO> getReceptionApplyBillPage(ReceptionApplyBillPageReqVO pageReqVO) {
        return receptionApplyBillMapper.selectPageByVisibleScope(pageReqVO, oaBillApprovalVisibleService.resolveCurrentUserScope());
    }

    private void validateExists(Long id) {
        if (receptionApplyBillMapper.selectById(id) == null) {
            throw exception(RECEPTION_APPLY_BILL_NOT_EXISTS);
        }
    }

    private void saveAttachments(Long billId, ReceptionApplyBillSaveReqVO saveReqVO) {
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(
                    OaBillTypeEnum.OA_RECEPTION_APPLY_BILL.getTypeCode(), billId, saveReqVO.getAttachments());
        }
    }

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_RECEPTION_APPLY_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        validateExists(id);
        receptionApplyBillMapper.updateById(new ReceptionApplyBillDO().setId(id).setProcessStatus(status));
        log.info("[updateProcessStatus] 接待申请单流程状态更新，id: {}, status: {}", id, status);
    }

}
