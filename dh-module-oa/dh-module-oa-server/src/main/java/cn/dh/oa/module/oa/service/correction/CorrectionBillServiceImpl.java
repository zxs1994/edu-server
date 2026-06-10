package cn.dh.oa.module.oa.service.correction;

import cn.dh.oa.framework.common.enums.SystemEnum;
import cn.dh.oa.framework.common.util.bill.BillCodeUtils;
import cn.dh.oa.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.oa.module.bpm.api.task.BpmProcessInstanceApi;
import cn.dh.oa.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.oa.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.oa.module.oa.enums.OaBillTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.dh.oa.module.oa.controller.admin.correction.vo.*;
import cn.dh.oa.module.oa.dal.dataobject.correction.CorrectionBillDO;
import cn.dh.oa.framework.common.pojo.PageResult;
import cn.dh.oa.framework.common.util.object.BeanUtils;

import cn.dh.oa.module.oa.dal.mysql.correction.CorrectionBillMapper;
import cn.dh.oa.common.server.attachment.service.AttachmentService;
import cn.dh.oa.common.server.attachment.controller.vo.AttachmentRespVO;

import static cn.dh.oa.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.oa.module.oa.enums.ErrorCodeConstants.*;

/**
 * 纠错申请单 Service 实现类
 * 
 * 注意：纠错模块是一个特殊的模块，它不实现 FlowBillService 接口。
 * 纠错模块是一个后置审批纠错机制，用于对已审批通过的单据进行纠错处理。
 *
 * @author 鼎衡
 */
@Slf4j
@Service
@Validated
public class CorrectionBillServiceImpl implements CorrectionBillService {

    @Resource
    private CorrectionBillMapper correctionBillMapper;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Override
    public Long saveCorrectionBill(CorrectionBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CORRECTION_BILL));
        }

        // 插入或更新
        CorrectionBillDO correctionBill = BeanUtils.toBean(saveReqVO, CorrectionBillDO.class);
        correctionBillMapper.insertOrUpdate(correctionBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_CORRECTION_BILL.getTypeCode(), correctionBill.getId(), saveReqVO.getAttachments());
        }

        // 返回
        return correctionBill.getId();
    }

    @Override
    public Long submitCorrectionBill(CorrectionBillSaveReqVO saveReqVO) {
        // 如果单号为空，需要生成
        if (StringUtils.isBlank(saveReqVO.getBillCode())) {
            saveReqVO.setBillCode(BillCodeUtils.generateBillCode(SystemEnum.OA, OaBillTypeEnum.OA_CORRECTION_BILL));
        }

        // 保存或更新，设置流程状态为运行中，冻结状态为已冻结
        CorrectionBillDO correctionBill = BeanUtils.toBean(saveReqVO, CorrectionBillDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus())
                .setFreezeStatus(1); // 提交时自动冻结原单据
        correctionBillMapper.insertOrUpdate(correctionBill);

        // 保存附件信息
        if (saveReqVO.getAttachments() != null) {
            attachmentService.saveAttachmentList(OaBillTypeEnum.OA_CORRECTION_BILL.getTypeCode(), correctionBill.getId(), saveReqVO.getAttachments());
        }

        // 冻结原单据
        freezeSourceBill(correctionBill);

        log.info("[submitCorrectionBill] 纠错申请单提交成功，id: {}, 原单据类型: {}, 原单据ID: {}",
                correctionBill.getId(), correctionBill.getSourceBillType(), correctionBill.getSourceBillId());

        // 返回
        return correctionBill.getId();
    }

    @Override
    public void updateCorrectionBill(CorrectionBillSaveReqVO updateReqVO) {
        // 校验存在
        validateCorrectionBillExists(updateReqVO.getId());
        // 更新
        CorrectionBillDO updateObj = BeanUtils.toBean(updateReqVO, CorrectionBillDO.class);
        correctionBillMapper.updateById(updateObj);
    }

    @Override
    public void deleteCorrectionBill(Long id) {
        // 校验存在
        validateCorrectionBillExists(id);
        // 删除
        correctionBillMapper.deleteById(id);
    }

    @Override
    public void deleteCorrectionBillListByIds(List<Long> ids) {
        // 删除
        correctionBillMapper.deleteByIds(ids);
    }

    private void validateCorrectionBillExists(Long id) {
        if (correctionBillMapper.selectById(id) == null) {
            throw exception(CORRECTION_BILL_NOT_EXISTS);
        }
    }

    @Override
    public CorrectionBillDO getCorrectionBill(Long id) {
        return correctionBillMapper.selectById(id);
    }

    @Override
    public CorrectionBillRespVO getCorrectionBillInfo(Long id) {
        CorrectionBillDO correctionBill = correctionBillMapper.selectById(id);
        if (correctionBill == null) {
            return null;
        }

        CorrectionBillRespVO respVO = BeanUtils.toBean(correctionBill, CorrectionBillRespVO.class);

        // 获取附件信息
        respVO.setAttachments(BeanUtils.toBean(
            attachmentService.getAttachmentListByBusiness(OaBillTypeEnum.OA_CORRECTION_BILL.getTypeCode(), id),
            AttachmentRespVO.class
        ));

        return respVO;
    }

    @Override
    public PageResult<CorrectionBillDO> getCorrectionBillPage(CorrectionBillPageReqVO pageReqVO) {
        // 自动添加创建人过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setCreator(String.valueOf(currentUserId));
        }
        return correctionBillMapper.selectPage(pageReqVO);
    }

    // ==================== 纠错特殊方法实现 ====================

    @Override
    public void freezeSource(String businessKey) {
        Long id = Long.parseLong(businessKey);
        log.info("[freezeSource] 冻结原单据，纠错申请单ID: {}", id);

        // 校验纠错申请单存在
        CorrectionBillDO correctionBill = correctionBillMapper.selectById(id);
        if (correctionBill == null) {
            throw exception(CORRECTION_BILL_NOT_EXISTS);
        }

        // 更新冻结状态为已冻结
        CorrectionBillDO updateObj = new CorrectionBillDO();
        updateObj.setId(id);
        updateObj.setFreezeStatus(1); // 1已冻结
        correctionBillMapper.updateById(updateObj);

        // 冻结原单据
        freezeSourceBill(correctionBill);

        log.info("[freezeSource] 原单据冻结成功，纠错申请单ID: {}, 原单据类型: {}, 原单据ID: {}",
                id, correctionBill.getSourceBillType(), correctionBill.getSourceBillId());
    }

    @Override
    public void unfreezeSource(String businessKey) {
        Long id = Long.parseLong(businessKey);
        log.info("[unfreezeSource] 解冻原单据，纠错申请单ID: {}", id);

        // 校验纠错申请单存在
        CorrectionBillDO correctionBill = correctionBillMapper.selectById(id);
        if (correctionBill == null) {
            throw exception(CORRECTION_BILL_NOT_EXISTS);
        }

        // 更新冻结状态为已解冻
        CorrectionBillDO updateObj = new CorrectionBillDO();
        updateObj.setId(id);
        updateObj.setFreezeStatus(2); // 2已解冻
        correctionBillMapper.updateById(updateObj);

        log.info("[unfreezeSource] 原单据解冻成功，纠错申请单ID: {}, 原单据类型: {}, 原单据ID: {}",
                id, correctionBill.getSourceBillType(), correctionBill.getSourceBillId());
    }

    @Override
    public void startReApproval(Long id) {
        log.info("[startReApproval] 发起重审流程，纠错申请单ID: {}", id);

        // 校验纠错申请单存在
        CorrectionBillDO correctionBill = correctionBillMapper.selectById(id);
        if (correctionBill == null) {
            throw exception(CORRECTION_BILL_NOT_EXISTS);
        }

        // 校验原单据信息
        if (correctionBill.getSourceBillId() == null || StringUtils.isBlank(correctionBill.getSourceBillType())) {
            throw exception(CORRECTION_SOURCE_BILL_NOT_EXISTS);
        }

        // 根据原单据类型获取对应的流程定义Key
        String processDefinitionKey = getProcessDefinitionKeyByBillType(correctionBill.getSourceBillType());

        // 创建新的BPM流程实例用于重审
        Map<String, Object> processInstanceVariables = new HashMap<>();
        processInstanceVariables.put("isReApproval", true); // 标记为重审流程
        processInstanceVariables.put("correctionBillId", correctionBill.getId());
        processInstanceVariables.put("sourceBillId", correctionBill.getSourceBillId());
        processInstanceVariables.put("sourceBillType", correctionBill.getSourceBillType());
        processInstanceVariables.put("correctionReason", correctionBill.getCorrectionReason());

        String newProcessInstanceId = processInstanceApi.submitProcessInstance(
                Long.valueOf(correctionBill.getCreator()),
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(processDefinitionKey)
                        .setVariables(processInstanceVariables)
                        .setBusinessKey(String.valueOf(correctionBill.getSourceBillId()))
        ).getCheckedData();

        // 更新纠错申请单，设置重审流程实例ID和纠错状态为重审中
        CorrectionBillDO updateObj = new CorrectionBillDO();
        updateObj.setId(id);
        updateObj.setNewProcessInstanceId(newProcessInstanceId);
        updateObj.setCorrectionStatus(1); // 1重审中
        correctionBillMapper.updateById(updateObj);

        log.info("[startReApproval] 重审流程发起成功，纠错申请单ID: {}, 新流程实例ID: {}", id, newProcessInstanceId);
    }

    // ==================== 私有方法 ====================

    /**
     * 冻结原单据
     *
     * @param correctionBill 纠错申请单
     */
    private void freezeSourceBill(CorrectionBillDO correctionBill) {
        log.info("[freezeSourceBill] 冻结原单据，原单据类型: {}, 原单据ID: {}",
                correctionBill.getSourceBillType(), correctionBill.getSourceBillId());
        // 此处预留原单据冻结逻辑
        // 根据原单据类型，调用对应模块的Service将原单据标记为冻结状态
        // 例如：如果是用印申请单，调用 SealApplyBillService 的相关方法
        // 目前仅更新纠错申请单的冻结状态字段，具体原单据冻结逻辑需要根据各模块实现
    }

    /**
     * 根据单据类型代码获取流程定义Key
     *
     * @param billTypeCode 单据类型代码
     * @return 流程定义Key
     */
    private String getProcessDefinitionKeyByBillType(String billTypeCode) {
        for (OaBillTypeEnum billType : OaBillTypeEnum.values()) {
            if (billType.getTypeCode().equals(billTypeCode)) {
                return billType.getProcessDefinitionKey();
            }
        }
        throw exception(CORRECTION_SOURCE_BILL_NOT_EXISTS);
    }

}
