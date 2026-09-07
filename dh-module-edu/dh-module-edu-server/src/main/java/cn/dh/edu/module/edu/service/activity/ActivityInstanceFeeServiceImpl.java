package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.json.JsonUtils;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceFeeItemPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityInstanceFeeItemRespVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.*;
import cn.dh.edu.module.edu.dal.mysql.activity.*;
import cn.dh.edu.module.edu.enums.activity.EduFeeItemStatusEnum;
import cn.dh.edu.module.system.api.user.AdminUserApi;
import cn.dh.edu.module.system.api.user.dto.AdminUserRespDTO;
import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.edu.enums.ActivityInstanceStatusEnum.COMPLETED;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.ACTIVITY_INSTANCE_NOT_EXISTS;

@Slf4j
@Service
@Validated
public class ActivityInstanceFeeServiceImpl implements ActivityInstanceFeeService {

    private static final String FEE_MODE_FIXED = "fixed";
    private static final String FEE_MODE_HEAD = "head";
    private static final String FEE_SIDE_STUDENT = "student";

    @Resource
    private ActivityInstanceMapper activityInstanceMapper;
    @Resource
    private ActivityInstanceFeeItemMapper feeItemMapper;
    @Resource
    private ActivityFeeStandardMapper feeStandardMapper;
    @Resource
    private ActivityInstanceEnrollmentMapper enrollmentMapper;
    @Resource
    private ActivityInstanceRecordMapper recordMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityPaymentRequestMapper paymentRequestMapper;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public PageResult<ActivityInstanceFeeItemRespVO> getFeeItemPage(ActivityInstanceFeeItemPageReqVO pageReqVO) {
        Collection<Long> activityIds = null;
        if (StringUtils.isNotBlank(pageReqVO.getActivityName())) {
            activityIds = activityMapper.selectList(new cn.dh.edu.framework.mybatis.core.query.LambdaQueryWrapperX<ActivityDO>()
                            .like(ActivityDO::getName, pageReqVO.getActivityName().trim())
                            .select(ActivityDO::getId))
                    .stream()
                    .map(ActivityDO::getId)
                    .toList();
            if (CollUtil.isEmpty(activityIds)) {
                return PageResult.empty();
            }
        }
        Collection<Long> instanceIds = null;
        if (StringUtils.isNotBlank(pageReqVO.getInstanceCode())) {
            instanceIds = activityInstanceMapper.selectIdListByInstanceCodeLike(pageReqVO.getInstanceCode().trim());
            if (CollUtil.isEmpty(instanceIds)) {
                return PageResult.empty();
            }
        }

        PageResult<ActivityInstanceFeeItemDO> page = feeItemMapper.selectPage(pageReqVO, activityIds, instanceIds);
        if (CollUtil.isEmpty(page.getList())) {
            return PageResult.empty(page.getTotal());
        }
        return new PageResult<>(buildFeeItemRespList(page.getList()), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ActivityInstanceFeeItemRespVO> getFeeItemList(Long instanceId) {
        ActivityInstanceDO instance = validateInstanceExists(instanceId);
        tryGenerateFeeItems(instance);
        List<ActivityInstanceFeeItemDO> items = feeItemMapper.selectListByInstanceId(instanceId);
        if (CollUtil.isEmpty(items)) {
            return Collections.emptyList();
        }
        return buildFeeItemRespList(items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateFeeItems(Long instanceId) {
        ActivityInstanceDO instance = validateInstanceExists(instanceId);
        tryGenerateFeeItems(instance);
    }

    private void tryGenerateFeeItems(ActivityInstanceDO instance) {
        Long instanceId = instance.getId();
        if (!COMPLETED.getStatus().equals(instance.getStatus())) {
            return;
        }
        List<ActivityFeeStandardDO> standards = feeStandardMapper.selectListByActivityId(instance.getActivityId());
        if (CollUtil.isEmpty(standards)) {
            log.info("[generateFeeItems] 活动无费用标准，跳过，instanceId: {}", instanceId);
            return;
        }

        List<ActivityInstanceFeeItemDO> existingItems = feeItemMapper.selectListByInstanceId(instanceId);
        // 兼容旧数据：学生侧按人头曾生成「汇总一行且无收款人」，需拆成一人一条
        repairAggregatedStudentHeadItems(instance, standards, existingItems);
        existingItems = feeItemMapper.selectListByInstanceId(instanceId);

        if (CollUtil.isNotEmpty(existingItems)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        int insertCount = 0;
        for (ActivityFeeStandardDO standard : standards) {
            insertCount += insertItemsForStandard(instance, standard, now);
        }
        log.info("[generateFeeItems] 费用明细已生成，instanceId: {}, count: {}", instanceId, insertCount);
    }

    /**
     * 将旧版「学生+按人头」汇总明细拆成一人一条。
     * 条件：fee_side=student、fee_mode=head、payee_user_id 为空，且尚未进入付款流程。
     */
    private void repairAggregatedStudentHeadItems(ActivityInstanceDO instance,
                                                   List<ActivityFeeStandardDO> standards,
                                                   List<ActivityInstanceFeeItemDO> existingItems) {
        if (CollUtil.isEmpty(existingItems) || CollUtil.isEmpty(standards)) {
            return;
        }
        Set<Long> studentHeadStandardIds = standards.stream()
                .filter(s -> FEE_MODE_HEAD.equals(s.getFeeMode()) && FEE_SIDE_STUDENT.equals(s.getFeeSide()))
                .map(ActivityFeeStandardDO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(studentHeadStandardIds)) {
            return;
        }

        List<ActivityInstanceFeeItemDO> aggregated = existingItems.stream()
                .filter(item -> studentHeadStandardIds.contains(item.getFeeStandardId()))
                .filter(item -> item.getPayeeUserId() == null)
                .filter(item -> FEE_MODE_HEAD.equals(item.getFeeMode()))
                .filter(item -> FEE_SIDE_STUDENT.equals(item.getFeeSide()))
                .filter(item -> EduFeeItemStatusEnum.CREDITED.getStatus().equals(item.getStatus())
                        || EduFeeItemStatusEnum.PAID.getStatus().equals(item.getStatus()))
                .toList();
        if (CollUtil.isEmpty(aggregated)) {
            return;
        }

        List<Long> studentUserIds = resolveAttendanceStudentUserIds(instance.getId());
        LocalDateTime now = LocalDateTime.now();
        Map<Long, ActivityFeeStandardDO> standardMap = standards.stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toMap(ActivityFeeStandardDO::getId, s -> s, (a, b) -> a));

        for (ActivityInstanceFeeItemDO old : aggregated) {
            feeItemMapper.deleteById(old.getId());
            ActivityFeeStandardDO standard = standardMap.get(old.getFeeStandardId());
            if (standard == null) {
                continue;
            }
            if (CollUtil.isEmpty(studentUserIds)) {
                continue;
            }
            for (Long studentUserId : studentUserIds) {
                insertOneItem(instance, standard, studentUserId, 1, now);
            }
        }
        log.info("[repairAggregatedStudentHeadItems] 已拆分学生按人头汇总明细，instanceId: {}, oldCount: {}, studentCount: {}",
                instance.getId(), aggregated.size(), studentUserIds.size());
    }

    private int insertItemsForStandard(ActivityInstanceDO instance,
                                       ActivityFeeStandardDO standard,
                                       LocalDateTime now) {
        // 与活动计划费用标准对齐：统一按包干生成一条明细，收款人取费用标准配置
        BigDecimal amount = standard.getAmount() != null ? standard.getAmount() : BigDecimal.ZERO;
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return 0;
        }
        insertOneItem(instance, standard, standard.getPayeeUserId(), 1, now);
        return 1;
    }

    private void insertOneItem(ActivityInstanceDO instance,
                               ActivityFeeStandardDO standard,
                               Long payeeUserId,
                               int quantity,
                               LocalDateTime now) {
        BigDecimal unitPrice = standard.getAmount() != null ? standard.getAmount() : BigDecimal.ZERO;
        BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(quantity));

        ActivityInstanceFeeItemDO item = ActivityInstanceFeeItemDO.builder()
                .feeCode(generateFeeCode())
                .instanceId(instance.getId())
                .activityId(instance.getActivityId())
                .feeStandardId(standard.getId())
                .feeType(standard.getFeeType())
                .feeMode(FEE_MODE_FIXED)
                .currency(standard.getCurrency())
                .unitPrice(unitPrice)
                .quantity(quantity)
                .amount(amount)
                .feeSide(standard.getFeeSide())
                .payeeUserId(payeeUserId)
                .status(EduFeeItemStatusEnum.PENDING_REQUEST.getStatus())
                .generateTime(now)
                .payTime(null)
                .remark(standard.getRemark())
                .build();
        item.setTenantId(instance.getTenantId());
        feeItemMapper.insert(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markStudentFeeItemPaid(Long feeItemId) {
        ActivityInstanceFeeItemDO item = feeItemMapper.selectById(feeItemId);
        if (item == null) {
            return;
        }
        if (!FEE_SIDE_STUDENT.equals(item.getFeeSide())) {
            return;
        }
        if (!EduFeeItemStatusEnum.CREDITED.getStatus().equals(item.getStatus())) {
            return;
        }
        ActivityInstanceFeeItemDO update = new ActivityInstanceFeeItemDO();
        update.setId(feeItemId);
        update.setStatus(EduFeeItemStatusEnum.PAID.getStatus());
        update.setPayTime(LocalDateTime.now());
        feeItemMapper.updateById(update);
    }

    /** 出勤学生：已报名且未缺席 */
    private List<Long> resolveAttendanceStudentUserIds(Long instanceId) {
        List<Long> enrolledUserIds = enrollmentMapper.selectListByInstanceId(instanceId).stream()
                .map(ActivityInstanceEnrollmentDO::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (CollUtil.isEmpty(enrolledUserIds)) {
            return Collections.emptyList();
        }
        Set<Long> absentUserIds = resolveAbsentUserIds(instanceId);
        return enrolledUserIds.stream()
                .filter(userId -> !absentUserIds.contains(userId))
                .toList();
    }

    private Set<Long> resolveAbsentUserIds(Long instanceId) {
        ActivityInstanceRecordDO record = recordMapper.selectByInstanceId(instanceId);
        if (record == null || StringUtils.isBlank(record.getAbsentUserIds())) {
            return Collections.emptySet();
        }
        List<Long> absentUserIds = JsonUtils.parseObject(record.getAbsentUserIds(), new TypeReference<List<Long>>() {});
        return absentUserIds != null ? new HashSet<>(absentUserIds) : Collections.emptySet();
    }

    private String generateFeeCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String prefix = "FEE-" + datePart;
        Long count = feeItemMapper.selectCountByFeeCodePrefix(prefix);
        return prefix + "-" + String.format("%03d", count + 1);
    }

    private List<ActivityInstanceFeeItemRespVO> buildFeeItemRespList(List<ActivityInstanceFeeItemDO> items) {
        Set<Long> payeeUserIds = items.stream()
                .map(ActivityInstanceFeeItemDO::getPayeeUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, AdminUserRespDTO> userMap = CollUtil.isEmpty(payeeUserIds)
                ? Collections.emptyMap()
                : adminUserApi.getUserMap(payeeUserIds);

        Set<Long> activityIds = items.stream()
                .map(ActivityInstanceFeeItemDO::getActivityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, ActivityDO> activityMap = CollUtil.isEmpty(activityIds)
                ? Collections.emptyMap()
                : activityMapper.selectBatchIds(activityIds).stream()
                .collect(Collectors.toMap(ActivityDO::getId, a -> a, (a, b) -> a));

        Set<Long> instanceIds = items.stream()
                .map(ActivityInstanceFeeItemDO::getInstanceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, ActivityInstanceDO> instanceMap = CollUtil.isEmpty(instanceIds)
                ? Collections.emptyMap()
                : activityInstanceMapper.selectBatchIds(instanceIds).stream()
                .collect(Collectors.toMap(ActivityInstanceDO::getId, a -> a, (a, b) -> a));

        Set<Long> paymentIds = items.stream()
                .map(ActivityInstanceFeeItemDO::getPaymentRequestId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, ActivityPaymentRequestDO> paymentMap = CollUtil.isEmpty(paymentIds)
                ? Collections.emptyMap()
                : paymentRequestMapper.selectBatchIds(paymentIds).stream()
                .collect(Collectors.toMap(ActivityPaymentRequestDO::getId, a -> a, (a, b) -> a));

        List<ActivityInstanceFeeItemRespVO> result = new ArrayList<>(items.size());
        for (ActivityInstanceFeeItemDO item : items) {
            ActivityInstanceFeeItemRespVO vo = buildFeeItemResp(item, userMap);
            ActivityDO activity = activityMap.get(item.getActivityId());
            if (activity != null) {
                vo.setActivityName(activity.getName());
            }
            ActivityInstanceDO instance = instanceMap.get(item.getInstanceId());
            if (instance != null) {
                vo.setInstanceCode(instance.getInstanceCode());
            }
            ActivityPaymentRequestDO payment = paymentMap.get(item.getPaymentRequestId());
            if (payment != null) {
                vo.setPaymentBillCode(payment.getBillCode());
            }
            result.add(vo);
        }
        return result;
    }

    private ActivityInstanceFeeItemRespVO buildFeeItemResp(
            ActivityInstanceFeeItemDO item,
            Map<Long, AdminUserRespDTO> userMap) {
        ActivityInstanceFeeItemRespVO vo = BeanUtils.toBean(item, ActivityInstanceFeeItemRespVO.class);
        if (item.getPayeeUserId() != null) {
            vo.setPayeeUserName(formatUserName(userMap.get(item.getPayeeUserId())));
        }
        return vo;
    }

    private ActivityInstanceDO validateInstanceExists(Long instanceId) {
        ActivityInstanceDO instance = activityInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw exception(ACTIVITY_INSTANCE_NOT_EXISTS);
        }
        return instance;
    }

    private String formatUserName(AdminUserRespDTO user) {
        if (user == null) {
            return null;
        }
        if (user.getNickname() != null && user.getUsername() != null) {
            return user.getNickname() + "(" + user.getUsername() + ")";
        }
        return user.getNickname() != null ? user.getNickname() : user.getUsername();
    }

}
