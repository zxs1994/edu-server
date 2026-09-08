package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.service.FlowBillService;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.edu.module.bpm.api.task.BpmProcessInstanceApi;
import cn.dh.edu.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.edu.module.bpm.enums.BpmProcessVariableConstants;
import cn.dh.edu.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.edu.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.edu.module.bpm.util.BpmProcessInstanceCancelUtils;
import cn.dh.edu.module.bpm.util.BpmProcessVariableUtils;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentFeeActualReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentFeeItemRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentRequestPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentRequestRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPaymentRequestSaveReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceFeeItemDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityPaymentRequestDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityInstanceFeeItemMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityInstanceMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityPaymentRequestMapper;
import cn.dh.edu.module.edu.enums.EduBillTypeEnum;
import cn.dh.edu.module.edu.enums.activity.EduFeeItemStatusEnum;
import cn.dh.edu.module.system.api.dept.DeptApi;
import cn.dh.edu.module.system.api.dept.dto.DeptRespDTO;
import cn.dh.edu.module.system.api.user.AdminUserApi;
import cn.dh.edu.module.system.api.user.dto.AdminUserRespDTO;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.*;

@Slf4j
@Service
@Validated
public class ActivityPaymentRequestServiceImpl
        implements ActivityPaymentRequestService, FlowBillService<EduBillTypeEnum> {

    private static final String PAYMENT_CODE_PREFIX = "PAY";
    private static final String PAYMENT_CODE_REDIS_PREFIX = "edu:activity-payment:bill_code";

    @Resource
    private ActivityPaymentRequestMapper paymentRequestMapper;
    @Resource
    private ActivityInstanceFeeItemMapper feeItemMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityInstanceMapper activityInstanceMapper;
    @Resource
    private BpmProcessInstanceApi processInstanceApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private DeptApi deptApi;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private EduExchangeRateService exchangeRateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long savePaymentRequest(ActivityPaymentRequestSaveReqVO saveReqVO) {
        return saveOrUpdateDraft(saveReqVO, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitPaymentRequest(ActivityPaymentRequestSaveReqVO saveReqVO) {
        Long id = saveOrUpdateDraft(saveReqVO, true);
        ActivityPaymentRequestDO request = validateExists(id);
        if (!isEditableProcessStatus(request.getProcessStatus())) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_NOT_EDITABLE);
        }

        List<ActivityInstanceFeeItemDO> feeItems = feeItemMapper.selectListByPaymentRequestId(id);
        if (CollUtil.isEmpty(feeItems)) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_FEE_REQUIRED);
        }
        List<Long> instanceIds = feeItems.stream()
                .map(ActivityInstanceFeeItemDO::getInstanceId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (CollUtil.isEmpty(instanceIds) && request.getInstanceId() != null) {
            instanceIds = List.of(request.getInstanceId());
        }
        validateFeeItemsForSubmit(feeItems, instanceIds, id);

        // 明细锁定为已提交
        for (ActivityInstanceFeeItemDO item : feeItems) {
            ActivityInstanceFeeItemDO update = new ActivityInstanceFeeItemDO();
            update.setId(item.getId());
            update.setStatus(EduFeeItemStatusEnum.SUBMITTED.getStatus());
            update.setPaymentRequestId(id);
            feeItemMapper.updateById(update);
        }

        Long userId = SecurityFrameworkUtils.getLoginUserId();
        // 单号在服务端生成，需显式写入流程变量，待办/已办列表才能展示单据编号
        saveReqVO.setBillCode(request.getBillCode());
        Map<String, Object> variables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        variables.put(BpmProcessVariableConstants.BILL_CODE, request.getBillCode());
        variables.put(BpmProcessVariableConstants.CAUSE,
                StringUtils.defaultIfBlank(request.getTitle(), "专项活动付款申请"));
        String processInstanceId = processInstanceApi.submitProcessInstance(userId,
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(EduBillTypeEnum.ACTIVITY_PAYMENT_REQUEST.getProcessDefinitionKey())
                        .setVariables(variables)
                        .setBusinessKey(String.valueOf(id))
        ).getCheckedData();

        ActivityPaymentRequestDO update = new ActivityPaymentRequestDO();
        update.setId(id);
        update.setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        update.setProcessInstanceId(processInstanceId);
        paymentRequestMapper.updateById(update);
        log.info("[submitPaymentRequest] 付款申请已提交，id={}, processInstanceId={}, feeCount={}",
                id, processInstanceId, feeItems.size());
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePaymentRequest(Long id) {
        ActivityPaymentRequestDO request = validateExists(id);
        if (!isEditableProcessStatus(request.getProcessStatus())) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_NOT_EDITABLE);
        }
        // 与专项活动等单据一致：先取消流程 / 清抄送，再删业务单
        BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, request.getProcessInstanceId());
        unlinkDraftFeeItems(id);
        paymentRequestMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePaymentRequestListByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        for (Long id : ids) {
            deletePaymentRequest(id);
        }
    }

    @Override
    public ActivityPaymentRequestRespVO getPaymentRequest(Long id) {
        ActivityPaymentRequestDO request = validateExists(id);
        ActivityPaymentRequestRespVO respVO = BeanUtils.toBean(request, ActivityPaymentRequestRespVO.class);
        fillActivityBrief(respVO, request.getActivityId());
        fillInstanceBrief(respVO, request.getInstanceId());
        fillApplicantName(respVO);
        List<ActivityInstanceFeeItemDO> feeItems = loadFeeItemsForRequest(request);
        List<ActivityPaymentFeeItemRespVO> feeVos = convertFeeItems(feeItems);
        respVO.setFeeItems(feeVos);
        // 优先返回单据上的快照 ID（即使明细已改挂到新单，历史仍可对上）
        if (CollUtil.isNotEmpty(request.getFeeItemIds())) {
            respVO.setFeeItemIds(request.getFeeItemIds());
        } else {
            respVO.setFeeItemIds(feeVos.stream().map(ActivityPaymentFeeItemRespVO::getId).toList());
        }
        return respVO;
    }

    @Override
    public PageResult<ActivityPaymentRequestDO> getPaymentRequestPage(ActivityPaymentRequestPageReqVO pageReqVO) {
        return paymentRequestMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ActivityPaymentFeeItemRespVO> getSelectableFeeItems(Long activityId, List<Long> instanceIds,
                                                                    Long paymentRequestId) {
        if (CollUtil.isEmpty(instanceIds)) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_INSTANCE_REQUIRED);
        }
        List<Long> distinctIds = instanceIds.stream().filter(Objects::nonNull).distinct().toList();
        if (CollUtil.isEmpty(distinctIds)) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_INSTANCE_REQUIRED);
        }
        List<ActivityInstanceDO> instances = activityInstanceMapper.selectBatchIds(distinctIds);
        if (instances.size() != distinctIds.size()) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_INSTANCE_INVALID);
        }
        // activityId 可选过滤：不传则按实例跨活动汇总；传入则仅保留该活动下实例的明细
        List<Long> queryInstanceIds = distinctIds;
        if (activityId != null) {
            queryInstanceIds = instances.stream()
                    .filter(item -> Objects.equals(item.getActivityId(), activityId))
                    .map(ActivityInstanceDO::getId)
                    .filter(Objects::nonNull)
                    .toList();
            if (CollUtil.isEmpty(queryInstanceIds)) {
                return List.of();
            }
        }
        List<ActivityInstanceFeeItemDO> list =
                feeItemMapper.selectSelectableForPayment(queryInstanceIds, paymentRequestId);
        return convertFeeItems(list);
    }

    // ==================== FlowBillService ====================

    @Override
    public EduBillTypeEnum getSupportedBillType() {
        return EduBillTypeEnum.ACTIVITY_PAYMENT_REQUEST;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        ActivityPaymentRequestDO request = validateExists(id);
        ActivityPaymentRequestDO update = new ActivityPaymentRequestDO();
        update.setId(id);
        update.setProcessStatus(status);
        if (BpmProcessInstanceStatusEnum.APPROVE.getStatus().equals(status)) {
            update.setApproveTime(LocalDateTime.now());
            // 审批通过瞬间锁定汇率与折合人民币，后续预算执行不再随实时汇率漂移
            BigDecimal rate = exchangeRateService.getCnyRate(request.getCurrency());
            BigDecimal amountCny = exchangeRateService.toCny(request.getTotalAmount(), request.getCurrency());
            update.setExchangeRate(rate);
            update.setAmountCny(amountCny);
            paymentRequestMapper.updateById(update);
        } else if (BpmProcessInstanceStatusEnum.REJECT.getStatus().equals(status)
                || BpmProcessInstanceStatusEnum.CANCEL.getStatus().equals(status)) {
            paymentRequestMapper.update(null, new LambdaUpdateWrapper<ActivityPaymentRequestDO>()
                    .eq(ActivityPaymentRequestDO::getId, id)
                    .set(ActivityPaymentRequestDO::getProcessStatus, status)
                    .set(ActivityPaymentRequestDO::getApproveTime, null)
                    .set(ActivityPaymentRequestDO::getExchangeRate, null)
                    .set(ActivityPaymentRequestDO::getAmountCny, null));
        } else {
            paymentRequestMapper.updateById(update);
        }

        List<ActivityInstanceFeeItemDO> feeItems = feeItemMapper.selectListByPaymentRequestId(id);
        if (CollUtil.isEmpty(feeItems) && CollUtil.isNotEmpty(request.getFeeItemIds())) {
            feeItems = feeItemMapper.selectListByIds(request.getFeeItemIds());
        }
        if (CollUtil.isEmpty(feeItems)) {
            return;
        }
        if (BpmProcessInstanceStatusEnum.APPROVE.getStatus().equals(status)) {
            LocalDateTime payTime = LocalDateTime.now();
            for (ActivityInstanceFeeItemDO item : feeItems) {
                ActivityInstanceFeeItemDO feeUpdate = new ActivityInstanceFeeItemDO();
                feeUpdate.setId(item.getId());
                feeUpdate.setStatus(EduFeeItemStatusEnum.PAID.getStatus());
                feeUpdate.setPayTime(payTime);
                feeUpdate.setPaymentRequestId(id);
                feeItemMapper.updateById(feeUpdate);
            }
            log.info("[updateProcessStatus] 付款申请审批通过，id={}, feeCount={}", id, feeItems.size());
        } else if (BpmProcessInstanceStatusEnum.REJECT.getStatus().equals(status)
                || BpmProcessInstanceStatusEnum.CANCEL.getStatus().equals(status)) {
            // 明细置为已驳回，保留 payment_request_id：仅本单可再选，别的单不可选
            List<Long> feeIds = feeItems.stream().map(ActivityInstanceFeeItemDO::getId).toList();
            feeItemMapper.update(null, new LambdaUpdateWrapper<ActivityInstanceFeeItemDO>()
                    .in(ActivityInstanceFeeItemDO::getId, feeIds)
                    .set(ActivityInstanceFeeItemDO::getStatus, EduFeeItemStatusEnum.REJECTED.getStatus())
                    .set(ActivityInstanceFeeItemDO::getPaymentRequestId, id));
            if (CollUtil.isEmpty(request.getFeeItemIds())) {
                ActivityPaymentRequestDO snapshotUpdate = new ActivityPaymentRequestDO();
                snapshotUpdate.setId(id);
                snapshotUpdate.setFeeItemIds(feeIds);
                paymentRequestMapper.updateById(snapshotUpdate);
            }
            log.info("[updateProcessStatus] 付款申请驳回/取消，id={}, status={}, feeCount={}",
                    id, status, feeItems.size());
        }
    }

    // ==================== private ====================

    private Long saveOrUpdateDraft(ActivityPaymentRequestSaveReqVO saveReqVO, boolean forSubmit) {
        List<Long> instanceIds = resolveInstanceIds(saveReqVO);
        if (CollUtil.isEmpty(instanceIds)) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_INSTANCE_REQUIRED);
        }
        saveReqVO.setInstanceIds(instanceIds);
        saveReqVO.setInstanceId(instanceIds.get(0));

        List<ActivityInstanceDO> instances = activityInstanceMapper.selectBatchIds(instanceIds);
        if (instances.size() != instanceIds.size()) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_INSTANCE_INVALID);
        }
        // 单据头 activityId：多活动时取首个实例所属活动，仅作展示兼容
        Long activityId = instances.get(0).getActivityId();
        saveReqVO.setActivityId(activityId);
        if (forSubmit && StringUtils.isBlank(saveReqVO.getTitle())) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_TITLE_REQUIRED);
        }
        if (CollUtil.isEmpty(saveReqVO.getFeeItemIds())) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_FEE_REQUIRED);
        }

        List<ActivityInstanceFeeItemDO> feeItems = feeItemMapper.selectListByIds(saveReqVO.getFeeItemIds());
        validateFeeItemsForSubmit(feeItems, instanceIds, saveReqVO.getId());
        Map<Long, BigDecimal> actualAmountMap = resolveActualAmountMap(saveReqVO);
        applyActualAmounts(feeItems, actualAmountMap);
        AmountCurrency amountCurrency = summarizeByActualAmount(feeItems);
        // 按请求顺序保留快照，避免 selectListByIds 打乱顺序
        List<Long> feeItemIdSnapshot = new ArrayList<>(saveReqVO.getFeeItemIds());

        ActivityPaymentRequestDO request;
        if (saveReqVO.getId() == null) {
            request = new ActivityPaymentRequestDO();
            request.setBillCode(generatePaymentBillCode());
            request.setActivityId(activityId);
            request.setInstanceId(saveReqVO.getInstanceId());
            request.setTitle(saveReqVO.getTitle());
            request.setRemark(saveReqVO.getRemark());
            request.setTotalAmount(amountCurrency.totalAmount());
            request.setCurrency(amountCurrency.currency());
            request.setFeeItemIds(feeItemIdSnapshot);
            request.setProcessStatus(BpmTaskStatusEnum.NOT_START.getStatus());
            request.setApplicantUserId(SecurityFrameworkUtils.getLoginUserId());
            paymentRequestMapper.insert(request);
        } else {
            request = validateExists(saveReqVO.getId());
            if (!isEditableProcessStatus(request.getProcessStatus())) {
                throw exception(ACTIVITY_PAYMENT_REQUEST_NOT_EDITABLE);
            }
            unlinkDraftFeeItems(request.getId());
            ActivityPaymentRequestDO update = new ActivityPaymentRequestDO();
            update.setId(request.getId());
            update.setActivityId(activityId);
            update.setInstanceId(saveReqVO.getInstanceId());
            update.setTitle(saveReqVO.getTitle());
            update.setRemark(saveReqVO.getRemark());
            update.setTotalAmount(amountCurrency.totalAmount());
            update.setCurrency(amountCurrency.currency());
            update.setFeeItemIds(feeItemIdSnapshot);
            // 驳回/取消后再次保存：回到未提交，便于重新走审批
            if (!Objects.equals(request.getProcessStatus(), BpmTaskStatusEnum.NOT_START.getStatus())) {
                update.setProcessStatus(BpmTaskStatusEnum.NOT_START.getStatus());
            }
            paymentRequestMapper.updateById(update);
            request.setActivityId(activityId);
            request.setInstanceId(saveReqVO.getInstanceId());
            request.setTitle(saveReqVO.getTitle());
            request.setTotalAmount(amountCurrency.totalAmount());
            request.setCurrency(amountCurrency.currency());
            request.setFeeItemIds(feeItemIdSnapshot);
            request.setProcessStatus(BpmTaskStatusEnum.NOT_START.getStatus());
        }

        // 草稿绑定：写 payment_request_id + 实际金额
        for (ActivityInstanceFeeItemDO item : feeItems) {
            ActivityInstanceFeeItemDO update = new ActivityInstanceFeeItemDO();
            update.setId(item.getId());
            update.setPaymentRequestId(request.getId());
            update.setActualAmount(item.getActualAmount());
            feeItemMapper.updateById(update);
        }
        return request.getId();
    }

    /** 未提交 / 审批拒绝 / 已取消：允许修改并重新提交 */
    private boolean isEditableProcessStatus(Integer processStatus) {
        return Objects.equals(processStatus, BpmProcessInstanceStatusEnum.NOT_START.getStatus())
                || Objects.equals(processStatus, BpmProcessInstanceStatusEnum.REJECT.getStatus())
                || Objects.equals(processStatus, BpmProcessInstanceStatusEnum.CANCEL.getStatus());
    }

    /**
     * 优先按单据 fee_item_ids 快照加载；无快照时回退按 payment_request_id 关联
     */
    private List<ActivityInstanceFeeItemDO> loadFeeItemsForRequest(ActivityPaymentRequestDO request) {
        if (CollUtil.isNotEmpty(request.getFeeItemIds())) {
            List<ActivityInstanceFeeItemDO> list = feeItemMapper.selectListByIds(request.getFeeItemIds());
            Map<Long, ActivityInstanceFeeItemDO> byId = list.stream()
                    .collect(Collectors.toMap(ActivityInstanceFeeItemDO::getId, item -> item, (a, b) -> a));
            List<ActivityInstanceFeeItemDO> ordered = new ArrayList<>();
            for (Long feeId : request.getFeeItemIds()) {
                ActivityInstanceFeeItemDO item = byId.get(feeId);
                if (item != null) {
                    ordered.add(item);
                }
            }
            return ordered;
        }
        return feeItemMapper.selectListByPaymentRequestId(request.getId());
    }

    private void unlinkDraftFeeItems(Long requestId) {
        List<ActivityInstanceFeeItemDO> bound = feeItemMapper.selectListByPaymentRequestId(requestId);
        if (CollUtil.isEmpty(bound)) {
            return;
        }
        List<Long> pendingIds = bound.stream()
                .filter(item -> EduFeeItemStatusEnum.PENDING_REQUEST.getStatus().equals(item.getStatus()))
                .map(ActivityInstanceFeeItemDO::getId)
                .toList();
        if (CollUtil.isNotEmpty(pendingIds)) {
            feeItemMapper.update(null, new LambdaUpdateWrapper<ActivityInstanceFeeItemDO>()
                    .in(ActivityInstanceFeeItemDO::getId, pendingIds)
                    .set(ActivityInstanceFeeItemDO::getPaymentRequestId, null)
                    .set(ActivityInstanceFeeItemDO::getActualAmount, null));
        }
        // 本单解绑的已驳回明细：释放为待申请，否则会永久锁死
        List<Long> rejectedIds = bound.stream()
                .filter(item -> EduFeeItemStatusEnum.REJECTED.getStatus().equals(item.getStatus()))
                .map(ActivityInstanceFeeItemDO::getId)
                .toList();
        if (CollUtil.isNotEmpty(rejectedIds)) {
            feeItemMapper.update(null, new LambdaUpdateWrapper<ActivityInstanceFeeItemDO>()
                    .in(ActivityInstanceFeeItemDO::getId, rejectedIds)
                    .set(ActivityInstanceFeeItemDO::getPaymentRequestId, null)
                    .set(ActivityInstanceFeeItemDO::getActualAmount, null)
                    .set(ActivityInstanceFeeItemDO::getStatus, EduFeeItemStatusEnum.PENDING_REQUEST.getStatus()));
        }
    }

    private void validateFeeItemsForSubmit(List<ActivityInstanceFeeItemDO> feeItems,
                                           List<Long> instanceIds, Long currentRequestId) {
        if (CollUtil.isEmpty(feeItems)) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_FEE_REQUIRED);
        }
        Set<Long> allowedInstanceIds = new HashSet<>(instanceIds);
        Set<Long> idSet = new HashSet<>();
        String currency = null;
        for (ActivityInstanceFeeItemDO item : feeItems) {
            if (item == null || !idSet.add(item.getId())) {
                throw exception(ACTIVITY_PAYMENT_REQUEST_FEE_INVALID);
            }
            if (item.getInstanceId() == null || !allowedInstanceIds.contains(item.getInstanceId())) {
                throw exception(ACTIVITY_PAYMENT_REQUEST_FEE_INVALID);
            }
            boolean pending = EduFeeItemStatusEnum.PENDING_REQUEST.getStatus().equals(item.getStatus());
            boolean rejectedOnThisBill = EduFeeItemStatusEnum.REJECTED.getStatus().equals(item.getStatus())
                    && currentRequestId != null
                    && Objects.equals(item.getPaymentRequestId(), currentRequestId);
            if (!pending && !rejectedOnThisBill) {
                throw exception(ACTIVITY_PAYMENT_REQUEST_FEE_INVALID);
            }
            if (pending
                    && item.getPaymentRequestId() != null
                    && !Objects.equals(item.getPaymentRequestId(), currentRequestId)) {
                throw exception(ACTIVITY_PAYMENT_REQUEST_FEE_INVALID);
            }
            if (currency == null) {
                currency = item.getCurrency();
            } else if (!Objects.equals(currency, item.getCurrency())) {
                throw exception(ACTIVITY_PAYMENT_REQUEST_CURRENCY_MISMATCH);
            }
        }
    }

    /** 合并 instanceIds / instanceId，去空去重并保持顺序 */
    private List<Long> resolveInstanceIds(ActivityPaymentRequestSaveReqVO saveReqVO) {
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        if (CollUtil.isNotEmpty(saveReqVO.getInstanceIds())) {
            for (Long id : saveReqVO.getInstanceIds()) {
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        if (saveReqVO.getInstanceId() != null) {
            ids.add(saveReqVO.getInstanceId());
        }
        return new ArrayList<>(ids);
    }

    private Map<Long, BigDecimal> resolveActualAmountMap(ActivityPaymentRequestSaveReqVO saveReqVO) {
        Map<Long, BigDecimal> map = new HashMap<>();
        if (CollUtil.isEmpty(saveReqVO.getFeeActualAmounts())) {
            return map;
        }
        for (ActivityPaymentFeeActualReqVO item : saveReqVO.getFeeActualAmounts()) {
            if (item == null || item.getFeeItemId() == null) {
                continue;
            }
            map.put(item.getFeeItemId(), item.getActualAmount());
        }
        return map;
    }

    private void applyActualAmounts(List<ActivityInstanceFeeItemDO> feeItems,
                                    Map<Long, BigDecimal> actualAmountMap) {
        for (ActivityInstanceFeeItemDO item : feeItems) {
            BigDecimal actualAmount = actualAmountMap.get(item.getId());
            if (actualAmount == null) {
                throw exception(ACTIVITY_PAYMENT_REQUEST_ACTUAL_AMOUNT_REQUIRED);
            }
            if (actualAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw exception(ACTIVITY_PAYMENT_REQUEST_ACTUAL_AMOUNT_INVALID);
            }
            item.setActualAmount(actualAmount);
        }
    }

    private AmountCurrency summarizeByActualAmount(List<ActivityInstanceFeeItemDO> feeItems) {
        BigDecimal total = BigDecimal.ZERO;
        String currency = feeItems.get(0).getCurrency();
        for (ActivityInstanceFeeItemDO item : feeItems) {
            if (item.getActualAmount() != null) {
                total = total.add(item.getActualAmount());
            }
        }
        return new AmountCurrency(total, currency);
    }

    /**
     * 生成付款申请单号：PAY-{YYYY}-{流水号}
     * 例：PAY-2026-001；超过 999 自动变长
     */
    private String generatePaymentBillCode() {
        String year = String.valueOf(LocalDate.now().getYear());
        String prefix = PAYMENT_CODE_PREFIX + "-" + year + "-";
        String redisKey = PAYMENT_CODE_REDIS_PREFIX + ":" + year;
        long dbMaxSeq = resolveMaxSequenceFromDb(prefix);
        try {
            Long sequenceNo = stringRedisTemplate.opsForValue().increment(redisKey);
            if (sequenceNo == null) {
                sequenceNo = 1L;
            }
            if (sequenceNo <= dbMaxSeq) {
                sequenceNo = dbMaxSeq + 1;
                stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(sequenceNo));
            }
            stringRedisTemplate.expire(redisKey, Duration.ofDays(400));
            return prefix + String.format("%03d", sequenceNo);
        } catch (Exception ex) {
            log.warn("[generatePaymentBillCode] Redis 发号失败，回退数据库序号，prefix={}", prefix, ex);
            return prefix + String.format("%03d", dbMaxSeq + 1);
        }
    }

    private long resolveMaxSequenceFromDb(String prefix) {
        Long maxSeq = paymentRequestMapper.selectMaxSequenceByPrefix(prefix);
        return maxSeq == null ? 0L : maxSeq;
    }

    private ActivityPaymentRequestDO validateExists(Long id) {
        ActivityPaymentRequestDO request = paymentRequestMapper.selectById(id);
        if (request == null) {
            throw exception(ACTIVITY_PAYMENT_REQUEST_NOT_EXISTS);
        }
        return request;
    }

    private void validateActivityExists(Long activityId) {
        ActivityDO activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw exception(ACTIVITY_NOT_EXISTS);
        }
    }

    private ActivityInstanceDO validateInstance(Long instanceId) {
        ActivityInstanceDO instance = activityInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw exception(ACTIVITY_INSTANCE_NOT_EXISTS);
        }
        return instance;
    }

    private void fillActivityBrief(ActivityPaymentRequestRespVO respVO, Long activityId) {
        ActivityDO activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return;
        }
        respVO.setActivityName(activity.getName());
        respVO.setActivityBillCode(activity.getBillCode());
    }

    private void fillInstanceBrief(ActivityPaymentRequestRespVO respVO, Long instanceId) {
        if (instanceId == null) {
            return;
        }
        ActivityInstanceDO instance = activityInstanceMapper.selectById(instanceId);
        if (instance != null) {
            respVO.setInstanceId(instance.getId());
            respVO.setInstanceCode(instance.getInstanceCode());
        }
    }

    private void fillApplicantName(ActivityPaymentRequestRespVO respVO) {
        if (respVO.getApplicantUserId() == null) {
            return;
        }
        AdminUserRespDTO user = adminUserApi.getUser(respVO.getApplicantUserId()).getCheckedData();
        if (user == null) {
            return;
        }
        respVO.setApplicantUserName(user.getNickname());
        // HeaderForm 展示字段为 creatorName
        respVO.setCreatorName(user.getNickname());
        if (user.getDeptId() == null) {
            return;
        }
        try {
            DeptRespDTO dept = deptApi.getDept(user.getDeptId()).getCheckedData();
            if (dept != null) {
                respVO.setDeptName(dept.getName());
            }
        } catch (Exception ex) {
            log.warn("[fillApplicantName] 回填部门失败，applicantUserId={}",
                    respVO.getApplicantUserId(), ex);
        }
    }

    private List<ActivityPaymentFeeItemRespVO> convertFeeItems(List<ActivityInstanceFeeItemDO> feeItems) {
        if (CollUtil.isEmpty(feeItems)) {
            return List.of();
        }
        Set<Long> instanceIds = feeItems.stream()
                .map(ActivityInstanceFeeItemDO::getInstanceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, ActivityInstanceDO> instanceMap = new HashMap<>();
        if (CollUtil.isNotEmpty(instanceIds)) {
            List<ActivityInstanceDO> instances = activityInstanceMapper.selectBatchIds(instanceIds);
            if (CollUtil.isNotEmpty(instances)) {
                for (ActivityInstanceDO instance : instances) {
                    instanceMap.put(instance.getId(), instance);
                }
            }
        }
        Set<Long> payeeIds = feeItems.stream()
                .map(ActivityInstanceFeeItemDO::getPayeeUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, AdminUserRespDTO> userMap = CollUtil.isEmpty(payeeIds)
                ? Map.of()
                : adminUserApi.getUserMap(new ArrayList<>(payeeIds));

        List<ActivityPaymentFeeItemRespVO> result = new ArrayList<>(feeItems.size());
        for (ActivityInstanceFeeItemDO item : feeItems) {
            ActivityPaymentFeeItemRespVO vo = BeanUtils.toBean(item, ActivityPaymentFeeItemRespVO.class);
            ActivityInstanceDO instance = instanceMap.get(item.getInstanceId());
            if (instance != null) {
                vo.setInstanceCode(instance.getInstanceCode());
            }
            if (item.getPayeeUserId() != null) {
                AdminUserRespDTO user = userMap.get(item.getPayeeUserId());
                if (user != null) {
                    vo.setPayeeUserName(user.getNickname());
                }
            }
            result.add(vo);
        }
        return result;
    }

    private record AmountCurrency(BigDecimal totalAmount, String currency) {
    }

}
