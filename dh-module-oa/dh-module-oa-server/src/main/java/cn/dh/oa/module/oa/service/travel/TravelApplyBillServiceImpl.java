package cn.dh.oa.module.oa.service.travel;

import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.util.bill.BillCodeUtils;
import cn.dh.oa.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.oa.module.bpm.api.task.BpmProcessInstanceApi;
import cn.dh.oa.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleService;
import cn.dh.oa.module.bpm.util.BpmProcessInstanceCancelUtils;
import cn.dh.oa.module.bpm.util.BpmProcessVariableUtils;
import cn.dh.oa.framework.common.service.FlowBillService;
import cn.dh.oa.common.server.attachment.service.AttachmentService;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import java.math.BigDecimal;
import java.util.*;

import cn.dh.oa.module.oa.controller.admin.travel.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.travel.TravelApplyBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;
import cn.dh.oa.module.oa.dal.mysql.travel.TravelApplyBillMapper;
import cn.dh.oa.module.oa.dal.mysql.travel.TravelItineraryMapper;
import cn.dh.oa.module.oa.dal.dataobject.travel.TravelItineraryDO;
import org.springframework.transaction.annotation.Transactional;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;

@Slf4j
@Primary
@Service
@Validated
public class TravelApplyBillServiceImpl implements TravelApplyBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private TravelApplyBillMapper travelApplyBillMapper;

    @Resource
    private TravelItineraryMapper travelItineraryMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private OaBillApprovalVisibleService oaBillApprovalVisibleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveTravelApplyBill(TravelApplyBillSaveReqVO saveReqVO) {
        fillTravelType(saveReqVO);
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_TRAVEL_APPLY_BILL));
        }
        validateTravelDatesAndDays(saveReqVO);
        TravelApplyBillDO bill = BeanUtils.toBean(saveReqVO, TravelApplyBillDO.class);
        travelApplyBillMapper.insertOrUpdate(bill);
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getTypeCode(), bill.getId(), saveReqVO.getAttachments());
        }
        // 保存行程明细（先删后增）
        saveItineraries(bill.getId(), saveReqVO.getItineraries());
        return bill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitTravelApplyBill(TravelApplyBillSaveReqVO saveReqVO) {
        fillTravelType(saveReqVO);
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_TRAVEL_APPLY_BILL));
        }
        validateTravelDatesAndDays(saveReqVO);
        validateCompanionAndTravelerCount(saveReqVO);
        TravelApplyBillDO bill = BeanUtils.toBean(saveReqVO, TravelApplyBillDO.class).setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        travelApplyBillMapper.insertOrUpdate(bill);
        Map<String, Object> vars = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(getProcessDefinitionKey(saveReqVO))
                        .setVariables(vars)
                        .setBusinessKey(String.valueOf(bill.getId()))
        ).getCheckedData();
        travelApplyBillMapper.updateById(new TravelApplyBillDO().setId(bill.getId()).setProcessInstanceId(processInstanceId));
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getTypeCode(), bill.getId(), saveReqVO.getAttachments());
        }
        // 保存行程明细（先删后增）
        saveItineraries(bill.getId(), saveReqVO.getItineraries());
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
    @Transactional(rollbackFor = Exception.class)
    public void deleteTravelApplyBill(Long id) {
        validateTravelApplyBillExists(id);
        TravelApplyBillDO bill = travelApplyBillMapper.selectById(id);
        BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, bill.getProcessInstanceId());
        travelItineraryMapper.deleteByBillId(id);
        travelApplyBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTravelApplyBillListByIds(List<Long> ids) {
        List<TravelApplyBillDO> bills = travelApplyBillMapper.selectByIds(ids);
        for (TravelApplyBillDO bill : bills) {
            BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, bill.getProcessInstanceId());
        }
        for (Long id : ids) {
            travelItineraryMapper.deleteByBillId(id);
        }
        travelApplyBillMapper.deleteByIds(ids);
    }

    @Override
    public TravelApplyBillDO getTravelApplyBill(Long id) {
        return travelApplyBillMapper.selectById(id);
    }

    @Override
    public TravelApplyBillRespVO getTravelApplyBillInfo(Long id) {
        validateTravelApplyBillExists(id);
        TravelApplyBillDO bill = travelApplyBillMapper.selectById(id);
        oaBillApprovalVisibleService.assertCanView(bill.getCreator(), bill.getProcessInstanceId());
        TravelApplyBillRespVO respVO = BeanUtils.toBean(bill, TravelApplyBillRespVO.class);
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        // 获取行程明细
        respVO.setItineraries(BeanUtils.toBean(
            travelItineraryMapper.selectListByBillId(id),
            TravelItineraryRespVO.class
        ));
        return respVO;
    }

    @Override
    public PageResult<TravelApplyBillDO> getTravelApplyBillPage(TravelApplyBillPageReqVO pageReqVO) {
        return travelApplyBillMapper.selectPageByVisibleScope(pageReqVO, oaBillApprovalVisibleService.resolveCurrentUserScope());
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

    /** 根据申请类型选择流程定义 key */
    private String getProcessDefinitionKey(TravelApplyBillSaveReqVO saveReqVO) {
        return isOverseasTravel(saveReqVO.getTravelType())
                ? OaBillTypeEnum.OA_OVERSEAS_TRAVEL_APPLY_BILL.getProcessDefinitionKey()
                : OaBillTypeEnum.OA_TRAVEL_APPLY_BILL.getProcessDefinitionKey();
    }

    private void fillTravelType(TravelApplyBillSaveReqVO saveReqVO) {
        if (saveReqVO.getTravelType() == null) {
            saveReqVO.setTravelType(1);
        }
    }

    private boolean isOverseasTravel(Integer travelType) {
        return travelType != null && travelType == 2;
    }

    /** 同行人、出行人数必填（国内/出境均适用） */
    private void validateCompanionAndTravelerCount(TravelApplyBillSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getCompanion())) {
            throw exception(TRAVEL_COMPANION_REQUIRED);
        }
        if (saveReqVO.getTravelerCount() == null || saveReqVO.getTravelerCount() < 1) {
            throw exception(TRAVEL_TRAVELER_COUNT_REQUIRED);
        }
    }

    private void validateTravelApplyBillExists(Long id) {
        if (travelApplyBillMapper.selectById(id) == null) {
            throw exception(TRAVEL_APPLY_BILL_NOT_EXISTS);
        }
    }

    /** 校验日期顺序；出差天数由前端填写，后端不再覆盖 */
    private void validateTravelDatesAndDays(TravelApplyBillSaveReqVO saveReqVO) {
        if (saveReqVO.getTravelStartDate() != null && saveReqVO.getTravelEndDate() != null
                && saveReqVO.getTravelEndDate().isBefore(saveReqVO.getTravelStartDate())) {
            throw exception(TRAVEL_END_DATE_INVALID);
        }
        if (saveReqVO.getTravelDays() == null || saveReqVO.getTravelDays().compareTo(BigDecimal.ZERO) < 0) {
            throw exception(TRAVEL_DAYS_REQUIRED);
        }
    }

    /**
     * 保存行程明细（先删后增）
     */
    private void saveItineraries(Long billId, List<TravelItinerarySaveReqVO> itineraries) {
        travelItineraryMapper.deleteByBillId(billId);
        if (itineraries != null && !itineraries.isEmpty()) {
            for (TravelItinerarySaveReqVO item : itineraries) {
                TravelItineraryDO itineraryDO = BeanUtils.toBean(item, TravelItineraryDO.class);
                itineraryDO.setBillId(billId);
                itineraryDO.setId(null);
                travelItineraryMapper.insert(itineraryDO);
            }
        }
    }

}
