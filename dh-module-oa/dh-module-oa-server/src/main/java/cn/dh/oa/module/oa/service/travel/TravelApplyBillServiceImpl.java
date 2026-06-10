package cn.dh.oa.module.oa.service.travel;

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

import cn.dh.oa.module.oa.controller.admin.travel.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.travel.TravelApplyBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.module.oa.dal.mysql.travel.TravelApplyBillMapper;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;
import static cn.dh.oa.module.oa.enums.OaProcessVariableConstants.*;

@Slf4j
@Service
@Validated
public class TravelApplyBillServiceImpl implements TravelApplyBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private TravelApplyBillMapper travelApplyBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    public Long saveTravelApplyBill(TravelApplyBillSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_TRAVEL_APPLY_BILL));
        }
        TravelApplyBillDO bill = BeanUtils.toBean(saveReqVO, TravelApplyBillDO.class);
        travelApplyBillMapper.insertOrUpdate(bill);
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getTypeCode(), bill.getId(), saveReqVO.getAttachments());
        }
        return bill.getId();
    }

    @Override
    public Long submitTravelApplyBill(TravelApplyBillSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_TRAVEL_APPLY_BILL));
        }
        TravelApplyBillDO bill = BeanUtils.toBean(saveReqVO, TravelApplyBillDO.class).setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        travelApplyBillMapper.insertOrUpdate(bill);
        Map<String, Object> vars = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        // 差旅申请单特有流程变量：是否出国
        vars.put(PV_TRAVEL_IS_OVERSEAS, saveReqVO.getIsOverseas());
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getProcessDefinitionKey())
                        .setVariables(vars)
                        .setBusinessKey(String.valueOf(bill.getId()))
        ).getCheckedData();
        travelApplyBillMapper.updateById(new TravelApplyBillDO().setId(bill.getId()).setProcessInstanceId(processInstanceId));
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getTypeCode(), bill.getId(), saveReqVO.getAttachments());
        }
        return bill.getId();
    }

    @Override
    public Long createTravelApplyBill(TravelApplyBillSaveReqVO createReqVO) {
        TravelApplyBillDO bill = BeanUtils.toBean(createReqVO, TravelApplyBillDO.class);
        travelApplyBillMapper.insert(bill);
        return bill.getId();
    }

    @Override
    public void updateTravelApplyBill(TravelApplyBillSaveReqVO updateReqVO) {
        validateTravelApplyBillExists(updateReqVO.getId());
        TravelApplyBillDO updateObj = BeanUtils.toBean(updateReqVO, TravelApplyBillDO.class);
        travelApplyBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteTravelApplyBill(Long id) {
        validateTravelApplyBillExists(id);
        travelApplyBillMapper.deleteById(id);
    }

    @Override
    public void deleteTravelApplyBillListByIds(List<Long> ids) {
        ids.forEach(this::validateTravelApplyBillExists);
        travelApplyBillMapper.deleteByIds(ids);
    }

    @Override
    public TravelApplyBillDO getTravelApplyBill(Long id) {
        return travelApplyBillMapper.selectById(id);
    }

    @Override
    public TravelApplyBillRespVO getTravelApplyBillInfo(Long id) {
        TravelApplyBillDO bill = travelApplyBillMapper.selectById(id);
        if (bill == null) {
            return null;
        }
        TravelApplyBillRespVO respVO = BeanUtils.toBean(bill, TravelApplyBillRespVO.class);
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        return respVO;
    }

    @Override
    public PageResult<TravelApplyBillDO> getTravelApplyBillPage(TravelApplyBillPageReqVO pageReqVO) {
        return travelApplyBillMapper.selectPage(pageReqVO);
    }

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_TRAVEL_APPLY_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        travelApplyBillMapper.updateById(new TravelApplyBillDO().setId(id).setProcessStatus(status));
    }

    private void validateTravelApplyBillExists(Long id) {
        if (travelApplyBillMapper.selectById(id) == null) {
            throw exception(TRAVEL_APPLY_BILL_NOT_EXISTS);
        }
    }

}
