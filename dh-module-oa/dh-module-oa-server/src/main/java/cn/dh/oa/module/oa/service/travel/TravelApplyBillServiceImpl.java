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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveTravelApplyBill(TravelApplyBillSaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_TRAVEL_APPLY_BILL));
        }
        validateAndFillTravelDays(saveReqVO);
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
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_TRAVEL_APPLY_BILL));
        }
        validateAndFillTravelDays(saveReqVO);
        TravelApplyBillDO bill = BeanUtils.toBean(saveReqVO, TravelApplyBillDO.class).setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        travelApplyBillMapper.insertOrUpdate(bill);
        Map<String, Object> vars = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
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
        travelItineraryMapper.deleteByBillId(id);
        travelApplyBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTravelApplyBillListByIds(List<Long> ids) {
        ids.forEach(this::validateTravelApplyBillExists);
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
        TravelApplyBillDO bill = travelApplyBillMapper.selectById(id);
        if (bill == null) {
            return null;
        }
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

    /** 校验日期顺序并由后端重新计算出差天数 */
    private void validateAndFillTravelDays(TravelApplyBillSaveReqVO saveReqVO) {
        if (saveReqVO.getTravelStartDate() == null || saveReqVO.getTravelEndDate() == null) {
            return;
        }
        if (!saveReqVO.getTravelEndDate().isAfter(saveReqVO.getTravelStartDate())) {
            throw exception(TRAVEL_END_DATE_INVALID);
        }
        long diffMs = Duration.between(saveReqVO.getTravelStartDate(), saveReqVO.getTravelEndDate()).toMillis();
        saveReqVO.setTravelDays(BigDecimal.valueOf(diffMs)
                .divide(BigDecimal.valueOf(86_400_000L), 1, RoundingMode.HALF_UP));
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
