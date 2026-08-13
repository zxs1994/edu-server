package cn.dh.oa.module.oa.service.seal;

import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.util.bill.BillCodeUtils;
import cn.dh.oa.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.dh.oa.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.oa.module.bpm.api.task.BpmProcessInstanceApi;
import cn.dh.oa.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import cn.dh.oa.module.oa.service.bill.OaBillApprovalVisibleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.dh.oa.module.oa.controller.admin.seal.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.seal.SealApplyBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;

import cn.dh.oa.module.oa.dal.mysql.seal.SealApplyBillMapper;
import cn.dh.oa.common.server.attachment.service.AttachmentService;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;

import cn.dh.oa.framework.common.service.FlowBillService;

import cn.dh.oa.module.oa.enums.SealUseStatusEnum;
import cn.dh.oa.module.bpm.util.BpmProcessInstanceCancelUtils;
import cn.dh.oa.module.bpm.util.BpmProcessVariableUtils;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;
import static cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum.RUNNING;
import static cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum.APPROVE;
import static cn.dh.oa.module.oa.enums.OaProcessVariableConstants.*;

/**
 * 用印申请单 Service 实现类
 *
 * @author 鼎衡
 */
@Slf4j
@Service
@Validated
public class SealApplyBillServiceImpl implements SealApplyBillService, FlowBillService<OaBillTypeEnum> {

    @Resource
    private SealApplyBillMapper sealApplyBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private OaBillApprovalVisibleService oaBillApprovalVisibleService;

    @Override
    public Long saveSealApplyBill(SealApplyBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_SEAL_APPLY_BILL));
        }

        // 现场用印时，清空归还时间
        if (saveReqVO.getUseMode() != null && saveReqVO.getUseMode() == 1) {
            saveReqVO.setExpectedReturnTime(null);
            saveReqVO.setActualReturnTime(null);
        }

        // 校验时间冲突（现场用章和外借用章都需要校验）
        validateTimeConflict(saveReqVO);

        // 插入或更新
        SealApplyBillDO sealApplyBill = BeanUtils.toBean(saveReqVO, SealApplyBillDO.class);
        sealApplyBillMapper.insertOrUpdate(sealApplyBill);

        // 现场用印时，显式清空归还时间（MyBatis-Plus 默认 NOT_NULL 策略不会将 null 写入 UPDATE）
        if (saveReqVO.getUseMode() != null && saveReqVO.getUseMode() == 1) {
            sealApplyBillMapper.update(new LambdaUpdateWrapper<SealApplyBillDO>()
                    .eq(SealApplyBillDO::getId, sealApplyBill.getId())
                    .set(SealApplyBillDO::getExpectedReturnTime, null)
                    .set(SealApplyBillDO::getActualReturnTime, null));
        }

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_SEAL_APPLY_BILL.getTypeCode(), sealApplyBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return sealApplyBill.getId();
    }

    @Override
    public Long submitSealApplyBill(SealApplyBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if(StringUtils.isBlank(saveReqVO.getBillCode())){
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_SEAL_APPLY_BILL));
        }

        // 现场用印时，清空归还时间
        if (saveReqVO.getUseMode() != null && saveReqVO.getUseMode() == 1) {
            saveReqVO.setExpectedReturnTime(null);
            saveReqVO.setActualReturnTime(null);
        }

        // 校验时间冲突（现场用章和外借用章都需要校验）
        validateTimeConflict(saveReqVO);

        // 保存或更新
        SealApplyBillDO sealApplyBill = BeanUtils.toBean(saveReqVO, SealApplyBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        sealApplyBillMapper.insertOrUpdate(sealApplyBill);

        // 现场用印时，显式清空归还时间（MyBatis-Plus 默认 NOT_NULL 策略不会将 null 写入 UPDATE）
        if (saveReqVO.getUseMode() != null && saveReqVO.getUseMode() == 1) {
            sealApplyBillMapper.update(new LambdaUpdateWrapper<SealApplyBillDO>()
                    .eq(SealApplyBillDO::getId, sealApplyBill.getId())
                    .set(SealApplyBillDO::getExpectedReturnTime, null)
                    .set(SealApplyBillDO::getActualReturnTime, null));
        }

        // 智能提交 BPM 流程（如果流程实例不存在则创建，存在则审批发起人任务）
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        // 添加用印申请单特有的流程变量
        processInstanceVariables.put(PV_SEAL_USE_MODE, saveReqVO.getUseMode());
        String processInstanceId = processInstanceApi.submitProcessInstance(Long.valueOf(saveReqVO.getCreator()),
                new BpmProcessInstanceCreateReqDTO().setProcessDefinitionKey(OaBillTypeEnum.OA_SEAL_APPLY_BILL.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables).setBusinessKey(String.valueOf(sealApplyBill.getId()))
        ).getCheckedData();

        // 将工作流的编号，更新到单据中
        sealApplyBillMapper.updateById(new SealApplyBillDO().setId(sealApplyBill.getId()).setProcessInstanceId(processInstanceId));
        
        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_SEAL_APPLY_BILL.getTypeCode(), sealApplyBill.getId(), saveReqVO.getAttachments());
        }
        
        // 返回
        return sealApplyBill.getId();
    }

    @Override
    public Long createSealApplyBill(SealApplyBillSaveReqVO createReqVO) {
        // 插入
        String billCode = BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_SEAL_APPLY_BILL);
        createReqVO.setBillCode(billCode);
        // 插入
        SealApplyBillDO sealApplyBill = BeanUtils.toBean(createReqVO, SealApplyBillDO.class);
        sealApplyBillMapper.insertOrUpdate(sealApplyBill);

        // 返回
        return sealApplyBill.getId();
    }

    @Override
    public void updateSealApplyBill(SealApplyBillSaveReqVO updateReqVO) {
        // 校验存在
        validateSealApplyBillExists(updateReqVO.getId());
        // 更新
        SealApplyBillDO updateObj = BeanUtils.toBean(updateReqVO, SealApplyBillDO.class);
        sealApplyBillMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSealApplyBill(Long id) {
        SealApplyBillDO bill = sealApplyBillMapper.selectById(id);
        if (bill == null) {
            throw exception(SEAL_APPLY_BILL_NOT_EXISTS);
        }
        BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, bill.getProcessInstanceId());
        sealApplyBillMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSealApplyBillListByIds(List<Long> ids) {
        List<SealApplyBillDO> bills = sealApplyBillMapper.selectByIds(ids);
        for (SealApplyBillDO bill : bills) {
            BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, bill.getProcessInstanceId());
        }
        sealApplyBillMapper.deleteByIds(ids);
    }

    private void validateSealApplyBillExists(Long id) {
        if (sealApplyBillMapper.selectById(id) == null) {
            throw exception(SEAL_APPLY_BILL_NOT_EXISTS);
        }
    }

    @Override
    public SealApplyBillDO getSealApplyBill(Long id) {
        return sealApplyBillMapper.selectById(id);
    }

    @Override
    public SealApplyBillRespVO getSealApplyBillInfo(Long id) {
        validateSealApplyBillExists(id);
        SealApplyBillDO sealApplyBill = sealApplyBillMapper.selectById(id);
        oaBillApprovalVisibleService.assertCanView(sealApplyBill.getCreator(), sealApplyBill.getProcessInstanceId());
        
        SealApplyBillRespVO respVO = BeanUtils.toBean(sealApplyBill, SealApplyBillRespVO.class);
        
        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_SEAL_APPLY_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));
        
        return respVO;
    }
    
    @Override
    public SealApplyBillDO getSealApplyBillByCode(String code) {
        return sealApplyBillMapper.selectOne(new LambdaQueryWrapperX<SealApplyBillDO>().eq(SealApplyBillDO::getBillCode, code));
    }

    @Override
    public PageResult<SealApplyBillDO> getSealApplyBillPage(SealApplyBillPageReqVO pageReqVO) {
        return sealApplyBillMapper.selectPageByVisibleScope(pageReqVO, oaBillApprovalVisibleService.resolveCurrentUserScope());
    }

    // ==================== FlowBillService 接口实现 ====================

    @Override
    public OaBillTypeEnum getSupportedBillType() {
        return OaBillTypeEnum.OA_SEAL_APPLY_BILL;
    }

    @Override
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新用印申请单流程状态，id: {}, status: {}", id, status);

        // 校验用印申请单存在
        validateSealApplyBillExists(id);

        // 更新流程状态
        SealApplyBillDO updateObj = new SealApplyBillDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        
        // 如果审批通过，设置用印状态为待处理
        if (APPROVE.getStatus().equals(status)) {
            updateObj.setUseStatus(SealUseStatusEnum.PENDING.getStatus());
        }
        
        sealApplyBillMapper.updateById(updateObj);

        log.info("[updateProcessStatus] 用印申请单流程状态更新成功，id: {}, status: {}", id, status);
    }

    @Override
    public void updateUseStatus(Long id, Integer useStatus) {
        log.info("[updateUseStatus] 更新用印申请单用印状态，id: {}, useStatus: {}", id, useStatus);
        
        // 校验用印申请单存在
        validateSealApplyBillExists(id);
        
        // 更新用印状态
        SealApplyBillDO updateObj = new SealApplyBillDO();
        updateObj.setId(id);
        updateObj.setUseStatus(useStatus);
        sealApplyBillMapper.updateById(updateObj);
        
        log.info("[updateUseStatus] 用印申请单用印状态更新成功，id: {}, useStatus: {}", id, useStatus);
    }

    @Override
    public void markAsCompleted(Long id) {
        updateUseStatus(id, SealUseStatusEnum.COMPLETED.getStatus());
    }
    
    @Override
    public void markAsBorrowed(Long id) {
        updateUseStatus(id, SealUseStatusEnum.BORROWED.getStatus());
    }
    
    @Override
    public void markAsReturned(Long id) {
        updateUseStatus(id, SealUseStatusEnum.RETURNED.getStatus());
    }
    
    @Override
    public void markAsOverdue(Long id) {
        updateUseStatus(id, SealUseStatusEnum.OVERDUE.getStatus());
    }

    @Override
    public boolean checkTimeConflict(SealApplyBillSaveReqVO checkVO) {
        try {
            validateTimeConflict(checkVO);
            return false; // 无冲突
        } catch (cn.dh.oa.framework.common.exception.ServiceException e) {
            return true; // 仅业务异常（时间冲突）才算冲突
        }
        // 其他异常（DB错误等）向上抛出，不被吞掉
    }

    /**
     * 校验印章使用时间冲突（现场用章和外借用章都需要校验）
     * - 现场用章：检查预计用章时间是否落在其他申请的时间段内
     * - 外借用章：检查预计用章时间~预计归还时间段是否与其他借用时间段重叠，或与已有现场用印时间点冲突
     *
     * @param saveReqVO 保存请求VO
     */
    private void validateTimeConflict(SealApplyBillSaveReqVO saveReqVO) {
        // 防御检查：sealId 必须有效
        if (saveReqVO.getSealId() == null || saveReqVO.getSealId() <= 0
                || saveReqVO.getExpectedUseTime() == null) {
            log.info("[validateTimeConflict] 跳过校验，sealId={}, expectedUseTime={}",
                    saveReqVO.getSealId(), saveReqVO.getExpectedUseTime());
            return;
        }

        // 外借用章时，校验预计用章时间不能晚于预计归还时间
        if (saveReqVO.getUseMode() == 2) {
            if (saveReqVO.getExpectedReturnTime() == null
                    || saveReqVO.getExpectedUseTime().isAfter(saveReqVO.getExpectedReturnTime())) {
                throw exception(SEAL_TIME_CONFLICT);
            }
        }

        // 查询同一印章在相同时间段内的申请单（排除当前记录）
        LambdaQueryWrapper<SealApplyBillDO> queryWrapper = new LambdaQueryWrapperX<SealApplyBillDO>()
                .eq(SealApplyBillDO::getSealId, saveReqVO.getSealId())
                .ne(saveReqVO.getId() != null, SealApplyBillDO::getId, saveReqVO.getId())
                .and(wrapper -> wrapper
                        // 场景1：存在审批中的申请单
                        .and(subWrapper -> {
                            subWrapper.eq(SealApplyBillDO::getProcessStatus, RUNNING.getStatus());
                            addTimeOverlapCondition(subWrapper, saveReqVO);
                        })
                        // 场景2：存在审批通过且用印状态为外借中的申请单
                        .or(subWrapper -> {
                            subWrapper.eq(SealApplyBillDO::getProcessStatus, APPROVE.getStatus())
                                    .eq(SealApplyBillDO::getUseStatus, SealUseStatusEnum.BORROWED.getStatus());
                            addTimeOverlapCondition(subWrapper, saveReqVO);
                        })
                );

        log.info("[validateTimeConflict] 开始校验，sealId={}, useMode={}, expectedUseTime={}, expectedReturnTime={}, excludeId={}",
                saveReqVO.getSealId(), saveReqVO.getUseMode(), saveReqVO.getExpectedUseTime(),
                saveReqVO.getExpectedReturnTime(), saveReqVO.getId());

        List<SealApplyBillDO> conflictBills = sealApplyBillMapper.selectList(queryWrapper);

        if (!conflictBills.isEmpty()) {
            log.warn("[validateTimeConflict] 发现冲突记录：{}", conflictBills.stream()
                    .map(b -> "id=" + b.getId() + ",processStatus=" + b.getProcessStatus() + ",useMode=" + b.getUseMode()
                            + ",useTime=" + b.getExpectedUseTime() + ",returnTime=" + b.getExpectedReturnTime())
                    .toList());
            throw exception(SEAL_TIME_CONFLICT);
        }
        log.info("[validateTimeConflict] 校验通过，无冲突");
    }

    /**
     * 添加时间重叠条件
     * - 现场用章(useMode==1)：只有一个时间点(expectedUseTime)，检查是否落在已有时间段内
     * - 外借用章(useMode==2)：检查两个时间段是否重叠，或已有现场用印时间点是否落在当前借用时段内
     */
    private void addTimeOverlapCondition(LambdaQueryWrapper<SealApplyBillDO> wrapper, SealApplyBillSaveReqVO saveReqVO) {

        if (saveReqVO.getUseMode() == 1) {
            // 现场用章：检查时间点是否落在已有申请的时间段 [expectedUseTime, expectedReturnTime] 内
            wrapper.and(timeWrapper -> timeWrapper
                    .le(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedUseTime())
                    .ge(SealApplyBillDO::getExpectedReturnTime, saveReqVO.getExpectedUseTime())
            );
        } else {
            // 外借用章：与已有借用时段重叠，或已有现场用印时间点落在当前借用时段内
            wrapper.and(timeWrapper -> timeWrapper
                    .and(tw -> tw
                            .le(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedUseTime())
                            .ge(SealApplyBillDO::getExpectedReturnTime, saveReqVO.getExpectedUseTime())
                    )
                    .or(tw -> tw
                            .le(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedReturnTime())
                            .ge(SealApplyBillDO::getExpectedReturnTime, saveReqVO.getExpectedReturnTime())
                    )
                    .or(tw -> tw
                            .ge(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedUseTime())
                            .le(SealApplyBillDO::getExpectedReturnTime, saveReqVO.getExpectedReturnTime())
                    )
                    .or(tw -> tw
                            .eq(SealApplyBillDO::getUseMode, 1)
                            .ge(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedUseTime())
                            .le(SealApplyBillDO::getExpectedUseTime, saveReqVO.getExpectedReturnTime())
                    )
            );
        }
    }

}
