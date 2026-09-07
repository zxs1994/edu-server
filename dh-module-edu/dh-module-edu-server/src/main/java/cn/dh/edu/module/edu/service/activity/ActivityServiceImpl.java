package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.service.FlowBillService;
import cn.dh.edu.framework.common.util.json.JsonUtils;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.edu.module.bpm.api.task.BpmProcessInstanceApi;
import cn.dh.edu.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import cn.dh.edu.module.bpm.enums.BpmProcessVariableConstants;
import cn.dh.edu.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.edu.module.bpm.enums.task.BpmTaskStatusEnum;
import cn.dh.edu.module.bpm.util.BpmProcessInstanceCancelUtils;
import cn.dh.edu.module.bpm.util.BpmProcessVariableUtils;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityAttachmentVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityFeeStandardVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityPageReqVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivityRespVO;
import cn.dh.edu.module.edu.controller.admin.activity.vo.ActivitySaveReqVO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityFeeStandardDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityOwnerDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityParticipantDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityFeeStandardMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityOwnerMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityParticipantMapper;
import cn.dh.edu.module.edu.enums.EduBillTypeEnum;
import cn.dh.edu.module.edu.util.ActivityParticipantSortUtils;
import cn.dh.edu.module.system.api.permission.PermissionApi;
import cn.dh.edu.module.system.api.permission.RoleApi;
import cn.dh.edu.module.system.api.user.AdminUserApi;
import cn.dh.edu.module.system.api.user.dto.AdminUserRespDTO;
import cn.dh.edu.module.system.enums.permission.RoleCodeEnum;
import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.*;

/**
 * 专项活动 Service 实现类
 *
 * @author 鼎衡
 */
@Slf4j
@Service
@Validated
public class ActivityServiceImpl implements ActivityService, FlowBillService<EduBillTypeEnum> {

    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityOwnerMapper activityOwnerMapper;
    @Resource
    private ActivityParticipantMapper activityParticipantMapper;
    @Resource
    private ActivityFeeStandardMapper activityFeeStandardMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private RoleApi roleApi;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private BpmProcessInstanceApi processInstanceApi;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ActivityInstanceService activityInstanceService;
    @Resource
    private EduExchangeRateService exchangeRateService;

    /** 专项活动单号：ACT-{YYYY}-{类型2位}-{3位自增} */
    private static final String ACTIVITY_CODE_PREFIX = "ACT";
    private static final String ACTIVITY_CODE_REDIS_PREFIX = "edu:activity:bill_code";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveActivity(ActivitySaveReqVO saveReqVO) {
        // 草稿也至少要求名称、类型（用于展示与单据号分段）
        if (StringUtils.isBlank(saveReqVO.getName())) {
            throw exception(ACTIVITY_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(saveReqVO.getActivityType())) {
            throw exception(ACTIVITY_TYPE_REQUIRED);
        }
        fillBillCode(saveReqVO);
        if (saveReqVO.getProcessStatus() == null) {
            saveReqVO.setProcessStatus(BpmProcessInstanceStatusEnum.NOT_START.getStatus());
        }
        if (StringUtils.isBlank(saveReqVO.getCycleType())) {
            saveReqVO.setCycleType("ONCE");
        }
        // 预算总额由费用标准自动汇总
        applyBudgetFromFeeStandards(saveReqVO);
        // 草稿允许字段为空；空串统一成 null，避免脏数据
        normalizeBlankToNull(saveReqVO);
        List<ActivityAttachmentVO> attachmentList = saveReqVO.getAttachments();
        saveReqVO.setAttachments(null);
        ActivityDO activity = BeanUtils.toBean(saveReqVO, ActivityDO.class);
        saveReqVO.setAttachments(attachmentList);
        activity.setAttachmentsJson(encodeAttachments(attachmentList));
        activityMapper.insertOrUpdate(activity);
        saveOwnersAndFees(activity.getId(), saveReqVO, false);
        return activity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitActivity(ActivitySaveReqVO saveReqVO) {
        fillBillCode(saveReqVO);
        if (StringUtils.isBlank(saveReqVO.getCycleType())) {
            saveReqVO.setCycleType("ONCE");
        }
        normalizeBlankToNull(saveReqVO);
        validateForSubmit(saveReqVO);

        List<ActivityAttachmentVO> attachmentList = saveReqVO.getAttachments();
        saveReqVO.setAttachments(null);
        ActivityDO activity = BeanUtils.toBean(saveReqVO, ActivityDO.class)
                .setProcessStatus(BpmTaskStatusEnum.RUNNING.getStatus());
        saveReqVO.setAttachments(attachmentList);
        activity.setAttachmentsJson(encodeAttachments(attachmentList));
        activityMapper.insertOrUpdate(activity);
        saveOwnersAndFees(activity.getId(), saveReqVO, true);

        Long userId = resolveCreatorUserId(saveReqVO);
        Map<String, Object> processInstanceVariables = BpmProcessVariableUtils.buildBillVariables(saveReqVO);
        processInstanceVariables.put(BpmProcessVariableConstants.CAUSE, activity.getName() + "专项活动申请");
        String processInstanceId = processInstanceApi.submitProcessInstance(userId,
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(EduBillTypeEnum.ACTIVITY.getProcessDefinitionKey())
                        .setVariables(processInstanceVariables)
                        .setBusinessKey(String.valueOf(activity.getId()))
        ).getCheckedData();

        activityMapper.updateById(new ActivityDO().setId(activity.getId()).setProcessInstanceId(processInstanceId));
        return activity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id) {
        ActivityDO activity = activityMapper.selectById(id);
        if (activity == null) {
            throw exception(ACTIVITY_NOT_EXISTS);
        }
        BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, activity.getProcessInstanceId());
        activityOwnerMapper.deleteByActivityId(id);
        activityParticipantMapper.deleteByActivityId(id);
        activityFeeStandardMapper.deleteByActivityId(id);
        activityMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivityListByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<ActivityDO> activities = activityMapper.selectByIds(ids);
        for (ActivityDO activity : activities) {
            BpmProcessInstanceCancelUtils.cancelIfExists(processInstanceApi, activity.getProcessInstanceId());
            activityOwnerMapper.deleteByActivityId(activity.getId());
            activityParticipantMapper.deleteByActivityId(activity.getId());
            activityFeeStandardMapper.deleteByActivityId(activity.getId());
        }
        activityMapper.deleteByIds(ids);
    }

    @Override
    public ActivityRespVO getActivity(Long id) {
        ActivityDO activity = activityMapper.selectById(id);
        if (activity == null) {
            return null;
        }
        ActivityRespVO respVO = BeanUtils.toBean(activity, ActivityRespVO.class);

        List<ActivityOwnerDO> owners = activityOwnerMapper.selectListByActivityId(id);
        List<Long> ownerIds = owners.stream().map(ActivityOwnerDO::getUserId).filter(Objects::nonNull).toList();
        respVO.setOwnerUserIds(ownerIds);
        respVO.setOwnerUserNames(resolveUserNames(ownerIds));
        fillParticipantFields(respVO, id);

        List<ActivityFeeStandardDO> feeDos = activityFeeStandardMapper.selectListByActivityId(id);
        List<ActivityFeeStandardVO> feeVos = BeanUtils.toBean(feeDos, ActivityFeeStandardVO.class);
        List<Long> payeeUserIds = feeVos.stream()
                .map(ActivityFeeStandardVO::getPayeeUserId)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, AdminUserRespDTO> payeeUserMap = CollUtil.isEmpty(payeeUserIds)
                ? Collections.emptyMap()
                : adminUserApi.getUserMap(payeeUserIds);
        for (ActivityFeeStandardVO feeVo : feeVos) {
            if (feeVo.getPayeeUserId() != null) {
                AdminUserRespDTO user = payeeUserMap.get(feeVo.getPayeeUserId());
                if (user != null) {
                    feeVo.setPayeeUserName(formatParticipantDisplayName(user));
                }
            }
        }
        respVO.setFeeStandards(feeVos);
        respVO.setAttachments(decodeAttachments(activity.getAttachmentsJson()));
        activityInstanceService.ensureInstancesForApprovedActivity(id);
        return respVO;
    }

    @Override
    public PageResult<ActivityDO> getActivityPage(ActivityPageReqVO pageReqVO) {
        return activityMapper.selectPage(pageReqVO);
    }

    // ==================== FlowBillService ====================

    @Override
    public EduBillTypeEnum getSupportedBillType() {
        return EduBillTypeEnum.ACTIVITY;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProcessStatus(String businessKey, Integer status) {
        Long id = Long.parseLong(businessKey);
        log.info("[updateProcessStatus] 更新专项活动流程状态，id: {}, status: {}", id, status);
        validateExists(id);

        ActivityDO updateObj = new ActivityDO();
        updateObj.setId(id);
        updateObj.setProcessStatus(status);
        activityMapper.updateById(updateObj);

        if (BpmProcessInstanceStatusEnum.APPROVE.getStatus().equals(status)) {
            try {
                activityInstanceService.generateInstancesForActivity(id);
                log.info("[updateProcessStatus] 活动实例生成完成，activityId: {}", id);
            } catch (Exception ex) {
                log.error("[updateProcessStatus] 活动实例生成失败，activityId: {}", id, ex);
            }
        }
    }

    @Override
    public void onProcessApproved(String businessKey) {
        // 实例生成已在 updateProcessStatus 中处理
        log.info("[onProcessApproved] 专项活动审批通过，id: {}", businessKey);
    }

    // ==================== private ====================

    private void fillBillCode(ActivitySaveReqVO saveReqVO) {
        if (StringUtils.isNotBlank(saveReqVO.getBillCode())) {
            return;
        }
        saveReqVO.setBillCode(generateActivityBillCode(saveReqVO.getActivityType()));
    }

    /**
     * 生成专项活动单据编号：ACT-{YYYY}-{类型2位}-{流水号}
     * 例：ACT-2026-FX-001；超过 999 自动变为 1000（最少 3 位补零）
     * <p>
     * 优先 Redis INCR；若 Redis 不可用或序号落后于库中历史号，则按库最大流水续号。
     */
    private String generateActivityBillCode(String activityType) {
        String year = String.valueOf(LocalDate.now().getYear());
        String typeCode = normalizeActivityTypeCode(activityType);
        String prefix = ACTIVITY_CODE_PREFIX + "-" + year + "-" + typeCode + "-";
        String redisKey = ACTIVITY_CODE_REDIS_PREFIX + ":" + year + ":" + typeCode;

        long dbMaxSeq = resolveMaxSequenceFromDb(prefix);
        try {
            Long sequenceNo = stringRedisTemplate.opsForValue().increment(redisKey);
            if (sequenceNo == null) {
                sequenceNo = 1L;
            }
            // Redis 被清空或落后时，对齐到库最大号之后
            if (sequenceNo <= dbMaxSeq) {
                sequenceNo = dbMaxSeq + 1;
                stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(sequenceNo));
            }
            stringRedisTemplate.expire(redisKey, Duration.ofDays(400));
            return formatBillCode(prefix, sequenceNo);
        } catch (Exception ex) {
            log.warn("[generateActivityBillCode] Redis 发号失败，回退数据库序号，prefix={}", prefix, ex);
            return formatBillCode(prefix, dbMaxSeq + 1);
        }
    }

    /** 流水号至少 3 位补零；超过 999 自然变长 */
    private String formatBillCode(String prefix, long sequenceNo) {
        return prefix + String.format("%03d", sequenceNo);
    }

    /** 从库中按数字取最大流水号；无数据返回 0 */
    private long resolveMaxSequenceFromDb(String prefix) {
        Long maxSeq = activityMapper.selectMaxSequenceByPrefix(prefix);
        return maxSeq == null ? 0L : maxSeq;
    }

    /** 类型字典值规范为 2 位大写（如 FX、XB） */
    private String normalizeActivityTypeCode(String activityType) {
        String typeCode = StringUtils.defaultIfBlank(activityType, "XX").trim().toUpperCase();
        if (typeCode.length() > 2) {
            return typeCode.substring(0, 2);
        }
        if (typeCode.length() < 2) {
            return StringUtils.rightPad(typeCode, 2, 'X');
        }
        return typeCode;
    }

    private void validateExists(Long id) {
        if (activityMapper.selectById(id) == null) {
            throw exception(ACTIVITY_NOT_EXISTS);
        }
    }

    private void validateForSubmit(ActivitySaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getName())) {
            throw exception(ACTIVITY_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(saveReqVO.getActivityType())) {
            throw exception(ACTIVITY_TYPE_REQUIRED);
        }
        if (saveReqVO.getStartDate() == null) {
            throw exception(ACTIVITY_START_DATE_REQUIRED);
        }
        if (StringUtils.isBlank(saveReqVO.getContent())) {
            throw exception(ACTIVITY_CONTENT_REQUIRED);
        }
        if (saveReqVO.getEnrollStartTime() == null || saveReqVO.getEnrollEndTime() == null) {
            throw exception(ACTIVITY_ENROLL_TIME_REQUIRED);
        }
        if (saveReqVO.getEnrollEndTime().isBefore(saveReqVO.getEnrollStartTime())) {
            throw exception(ACTIVITY_ENROLL_TIME_INVALID);
        }
        if (!saveReqVO.getEnrollEndTime().isBefore(saveReqVO.getStartDate())) {
            throw exception(ACTIVITY_ENROLL_END_AFTER_START_DATE);
        }
        if (CollUtil.isEmpty(saveReqVO.getOwnerUserIds())) {
            throw exception(ACTIVITY_OWNER_REQUIRED);
        }
        if (CollUtil.isEmpty(saveReqVO.getParticipantUserIds())) {
            throw exception(ACTIVITY_PARTICIPANT_REQUIRED);
        }
        validateParticipantTeacherAndStudentRequired(saveReqVO.getParticipantUserIds());
        if (CollUtil.isEmpty(saveReqVO.getFeeStandards())) {
            throw exception(ACTIVITY_FEE_STANDARD_REQUIRED);
        }
        for (ActivityFeeStandardVO fee : saveReqVO.getFeeStandards()) {
            if (StringUtils.isBlank(fee.getFeeType())
                    || StringUtils.isBlank(fee.getFeeMode())
                    || StringUtils.isBlank(fee.getFeeSide())
                    || StringUtils.isBlank(fee.getCurrency())
                    || fee.getAmount() == null) {
                throw exception(ACTIVITY_FEE_INCOMPLETE);
            }
        }
        // 预算总额 = 费用标准合计（按人头×参与人数，外币折算人民币）
        applyBudgetFromFeeStandards(saveReqVO);
        if (saveReqVO.getBudgetAmount() == null
                || saveReqVO.getBudgetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw exception(ACTIVITY_BUDGET_INVALID);
        }
    }

    /**
     * 按费用标准汇总预算总额（人民币）：教培侧、学生侧均按金额计入（无积分）。
     * 包干取金额；按人头 × 对应侧参与人数；外币按汇率折算。
     */
    private void applyBudgetFromFeeStandards(ActivitySaveReqVO saveReqVO) {
        if (CollUtil.isEmpty(saveReqVO.getFeeStandards())) {
            if (saveReqVO.getBudgetAmount() == null) {
                saveReqVO.setBudgetAmount(BigDecimal.ZERO);
            }
            return;
        }
        saveReqVO.setBudgetAmount(calcFeeStandardsBudgetCny(saveReqVO));
    }

    private BigDecimal calcFeeStandardsBudgetCny(ActivitySaveReqVO saveReqVO) {
        int teacherCount = 0;
        int studentCount = 0;
        if (CollUtil.isNotEmpty(saveReqVO.getParticipantUserIds())) {
            Set<Long> teacherUserIds = resolveUserIdsByRoleCode(RoleCodeEnum.TEACHER.getCode());
            Set<Long> studentUserIds = resolveUserIdsByRoleCode(RoleCodeEnum.STUDENT.getCode());
            for (Long userId : saveReqVO.getParticipantUserIds()) {
                if (userId == null) {
                    continue;
                }
                if (teacherUserIds.contains(userId)) {
                    teacherCount++;
                } else if (studentUserIds.contains(userId)) {
                    studentCount++;
                }
            }
        }

        List<String> currencies = saveReqVO.getFeeStandards().stream()
                .map(ActivityFeeStandardVO::getCurrency)
                .filter(StringUtils::isNotBlank)
                .toList();
        exchangeRateService.prefetchCnyRates(currencies);

        BigDecimal feeTotal = BigDecimal.ZERO;
        for (ActivityFeeStandardVO fee : saveReqVO.getFeeStandards()) {
            if (fee.getAmount() == null || StringUtils.isBlank(fee.getCurrency())) {
                continue;
            }
            BigDecimal lineAmount = resolveFeeLineEstimatedAmount(fee, teacherCount, studentCount);
            feeTotal = feeTotal.add(exchangeRateService.toCny(lineAmount, fee.getCurrency()));
        }
        return feeTotal;
    }

    private BigDecimal resolveFeeLineEstimatedAmount(ActivityFeeStandardVO fee,
                                                     int teacherCount,
                                                     int studentCount) {
        BigDecimal amount = fee.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (!"head".equals(fee.getFeeMode())) {
            return amount;
        }
        if ("teacher".equals(fee.getFeeSide())) {
            return amount.multiply(BigDecimal.valueOf(teacherCount));
        }
        if ("student".equals(fee.getFeeSide())) {
            return amount.multiply(BigDecimal.valueOf(studentCount));
        }
        return BigDecimal.ZERO;
    }

    /** 草稿空串转 null，避免写入空字符串 */
    private void normalizeBlankToNull(ActivitySaveReqVO saveReqVO) {
        if (StringUtils.isBlank(saveReqVO.getName())) {
            saveReqVO.setName(null);
        }
        if (StringUtils.isBlank(saveReqVO.getActivityType())) {
            saveReqVO.setActivityType(null);
        }
        if (StringUtils.isBlank(saveReqVO.getActivitySubtype())) {
            saveReqVO.setActivitySubtype(null);
        }
        if (StringUtils.isBlank(saveReqVO.getContent())) {
            saveReqVO.setContent(null);
        }
        if (StringUtils.isBlank(saveReqVO.getRemark())) {
            saveReqVO.setRemark(null);
        }
    }

    private String encodeAttachments(List<ActivityAttachmentVO> attachments) {
        if (CollUtil.isEmpty(attachments)) {
            return null;
        }
        List<ActivityAttachmentVO> list = attachments.stream()
                .filter(item -> item != null && StringUtils.isNotBlank(item.getFileUrl()))
                .peek(item -> {
                    if (StringUtils.isBlank(item.getFileName())) {
                        item.setFileName("附件");
                    }
                })
                .toList();
        return CollUtil.isEmpty(list) ? null : JsonUtils.toJsonString(list);
    }

    private List<ActivityAttachmentVO> decodeAttachments(String json) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        // 兼容旧数据：纯 URL 字符串数组
        String trimmed = json.trim();
        if (trimmed.startsWith("[") && trimmed.contains("\"http") && !trimmed.contains("fileUrl")) {
            List<String> urls = JsonUtils.parseObject(trimmed, new TypeReference<List<String>>() {});
            if (urls == null) {
                return Collections.emptyList();
            }
            return urls.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(url -> {
                        ActivityAttachmentVO vo = new ActivityAttachmentVO();
                        int slash = url.lastIndexOf('/');
                        String name = slash >= 0 ? url.substring(slash + 1) : url;
                        vo.setFileName(StringUtils.isNotBlank(name) ? name : "附件");
                        vo.setFileUrl(url);
                        return vo;
                    })
                    .toList();
        }
        List<ActivityAttachmentVO> list =
                JsonUtils.parseObject(json, new TypeReference<List<ActivityAttachmentVO>>() {});
        return list != null ? list : Collections.emptyList();
    }

    private void saveOwnersAndFees(Long activityId, ActivitySaveReqVO saveReqVO, boolean forSubmit) {
        activityOwnerMapper.deleteByActivityId(activityId);
        activityParticipantMapper.deleteByActivityId(activityId);
        activityFeeStandardMapper.deleteByActivityId(activityId);

        if (CollUtil.isNotEmpty(saveReqVO.getOwnerUserIds())) {
            validateOwnerUserIds(saveReqVO.getOwnerUserIds());
            for (Long userId : saveReqVO.getOwnerUserIds()) {
                if (userId == null) {
                    continue;
                }
                ActivityOwnerDO owner = new ActivityOwnerDO();
                owner.setActivityId(activityId);
                owner.setUserId(userId);
                activityOwnerMapper.insert(owner);
            }
        }

        saveParticipants(activityId, saveReqVO);

        if (CollUtil.isNotEmpty(saveReqVO.getFeeStandards())) {
            for (ActivityFeeStandardVO feeVo : saveReqVO.getFeeStandards()) {
                // 草稿：完全空行跳过；提交：由 validateForSubmit 已校验完整性
                if (!forSubmit && StringUtils.isBlank(feeVo.getFeeType())
                        && StringUtils.isBlank(feeVo.getFeeMode())
                        && StringUtils.isBlank(feeVo.getFeeSide())
                        && StringUtils.isBlank(feeVo.getCurrency())
                        && feeVo.getAmount() == null
                        && feeVo.getPayeeUserId() == null) {
                    continue;
                }
                ActivityFeeStandardDO feeDo = BeanUtils.toBean(feeVo, ActivityFeeStandardDO.class);
                feeDo.setId(null);
                feeDo.setActivityId(activityId);
                // 未填金额保持 null，回显时不显示 0.00
                activityFeeStandardMapper.insert(feeDo);
            }
        }
    }

    private Long resolveCreatorUserId(ActivitySaveReqVO saveReqVO) {
        if (StringUtils.isNotBlank(saveReqVO.getCreator())) {
            return Long.valueOf(saveReqVO.getCreator());
        }
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            throw new IllegalStateException("无法获取提交人用户ID");
        }
        return loginUserId;
    }

    private void validateOwnerUserIds(List<Long> ownerUserIds) {
        if (CollUtil.isEmpty(ownerUserIds)) {
            return;
        }
        adminUserApi.validateUserList(ownerUserIds).checkError();
        Long studentRoleId = roleApi.getRoleIdByCode(RoleCodeEnum.STUDENT.getCode()).getCheckedData();
        if (studentRoleId == null) {
            return;
        }
        Set<Long> studentUserIds = permissionApi
                .getUserRoleIdListByRoleIds(Collections.singleton(studentRoleId))
                .getCheckedData();
        if (CollUtil.isEmpty(studentUserIds)) {
            return;
        }
        for (Long userId : ownerUserIds) {
            if (userId != null && studentUserIds.contains(userId)) {
                throw exception(ACTIVITY_OWNER_STUDENT_FORBIDDEN);
            }
        }
    }

    private String resolveUserNames(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return "";
        }
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(userIds);
        List<String> names = new ArrayList<>();
        for (Long id : userIds) {
            AdminUserRespDTO user = userMap.get(id);
            if (user != null && StringUtils.isNotBlank(user.getNickname())) {
                names.add(user.getNickname());
            }
        }
        return String.join("、", names);
    }

    private String resolveParticipantNames(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return "";
        }
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(userIds);
        List<String> names = new ArrayList<>();
        for (Long id : userIds) {
            AdminUserRespDTO user = userMap.get(id);
            String displayName = formatParticipantDisplayName(user);
            if (StringUtils.isNotBlank(displayName)) {
                names.add(displayName);
            }
        }
        return String.join("、", names);
    }

    private String formatParticipantDisplayName(AdminUserRespDTO user) {
        if (user == null) {
            return "";
        }
        if (StringUtils.isNotBlank(user.getNickname()) && StringUtils.isNotBlank(user.getUsername())) {
            return user.getNickname() + "(" + user.getUsername() + ")";
        }
        if (StringUtils.isNotBlank(user.getNickname())) {
            return user.getNickname();
        }
        return StringUtils.defaultString(user.getUsername());
    }

    private void saveParticipants(Long activityId, ActivitySaveReqVO saveReqVO) {
        if (CollUtil.isEmpty(saveReqVO.getParticipantUserIds())) {
            return;
        }
        validateParticipantUserIds(saveReqVO.getParticipantUserIds());
        for (Long userId : saveReqVO.getParticipantUserIds()) {
            if (userId == null) {
                continue;
            }
            ActivityParticipantDO participant = new ActivityParticipantDO();
            participant.setActivityId(activityId);
            participant.setUserId(userId);
            activityParticipantMapper.insert(participant);
        }
    }

    private void fillParticipantFields(ActivityRespVO respVO, Long activityId) {
        List<ActivityParticipantDO> participants = activityParticipantMapper.selectListByActivityId(activityId);
        List<Long> userIds = participants.stream()
                .map(ActivityParticipantDO::getUserId)
                .filter(Objects::nonNull)
                .toList();
        userIds = sortParticipantUserIds(userIds);
        respVO.setParticipantUserIds(userIds);
        respVO.setParticipantNames(resolveParticipantNames(userIds));
    }

    @Override
    public List<Long> sortParticipantUserIds(List<Long> userIds) {
        Set<Long> teacherUserIds = resolveUserIdsByRoleCode(RoleCodeEnum.TEACHER.getCode());
        Set<Long> studentUserIds = resolveUserIdsByRoleCode(RoleCodeEnum.STUDENT.getCode());
        return ActivityParticipantSortUtils.sortTeachersFirst(userIds, teacherUserIds, studentUserIds);
    }

    private void validateParticipantUserIds(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        adminUserApi.validateUserList(userIds).checkError();
        Set<Long> allowedUserIds = resolveStudentAndTeacherUserIds();
        for (Long userId : userIds) {
            if (userId != null && !allowedUserIds.contains(userId)) {
                throw exception(ACTIVITY_PARTICIPANT_ROLE_INVALID);
            }
        }
    }

    private void validateParticipantTeacherAndStudentRequired(List<Long> participantUserIds) {
        validateParticipantUserIds(participantUserIds);
        Set<Long> studentUserIds = resolveUserIdsByRoleCode(RoleCodeEnum.STUDENT.getCode());
        Set<Long> teacherUserIds = resolveUserIdsByRoleCode(RoleCodeEnum.TEACHER.getCode());
        boolean hasTeacher = participantUserIds.stream()
                .filter(Objects::nonNull)
                .anyMatch(teacherUserIds::contains);
        boolean hasStudent = participantUserIds.stream()
                .filter(Objects::nonNull)
                .anyMatch(studentUserIds::contains);
        if (!hasTeacher) {
            throw exception(ACTIVITY_PARTICIPANT_TEACHER_REQUIRED);
        }
        if (!hasStudent) {
            throw exception(ACTIVITY_PARTICIPANT_STUDENT_REQUIRED);
        }
    }

    private Set<Long> resolveUserIdsByRoleCode(String roleCode) {
        Long roleId = roleApi.getRoleIdByCode(roleCode).getCheckedData();
        if (roleId == null) {
            return Collections.emptySet();
        }
        Set<Long> userIds = permissionApi.getUserRoleIdListByRoleIds(Collections.singleton(roleId))
                .getCheckedData();
        return userIds != null ? userIds : Collections.emptySet();
    }

    private Set<Long> resolveStudentAndTeacherUserIds() {
        Set<Long> roleIds = new HashSet<>();
        Long studentRoleId = roleApi.getRoleIdByCode(RoleCodeEnum.STUDENT.getCode()).getCheckedData();
        if (studentRoleId != null) {
            roleIds.add(studentRoleId);
        }
        Long teacherRoleId = roleApi.getRoleIdByCode(RoleCodeEnum.TEACHER.getCode()).getCheckedData();
        if (teacherRoleId != null) {
            roleIds.add(teacherRoleId);
        }
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptySet();
        }
        Set<Long> userIds = permissionApi.getUserRoleIdListByRoleIds(roleIds).getCheckedData();
        return userIds != null ? userIds : Collections.emptySet();
    }

}
