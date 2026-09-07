package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.enums.CommonStatusEnum;
import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.json.JsonUtils;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.edu.framework.tenant.core.context.TenantContextHolder;
import cn.dh.edu.framework.tenant.core.util.TenantUtils;
import cn.dh.edu.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
import cn.dh.edu.module.edu.controller.admin.activity.vo.*;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceEnrollmentDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceRecordDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityOwnerDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityParticipantDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityInstanceEnrollmentMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityInstanceMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityInstanceRecordMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityOwnerMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityParticipantMapper;
import cn.dh.edu.module.edu.enums.ActivityCycleTypeEnum;
import cn.dh.edu.module.edu.enums.activity.EduActivityNoticeConstants;
import cn.dh.edu.module.edu.util.ActivityEnrollTokenHelper;
import cn.dh.edu.module.edu.util.ActivityInstanceStatusUtils;
import cn.dh.edu.module.system.api.notice.NoticeApi;
import cn.dh.edu.module.system.api.notice.dto.NoticeCreateReqDTO;
import cn.dh.edu.module.system.api.notify.NotifyMessageSendApi;
import cn.dh.edu.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import cn.dh.edu.module.system.api.permission.PermissionApi;
import cn.dh.edu.module.system.api.permission.RoleApi;
import cn.dh.edu.module.system.api.sms.SmsSendApi;
import cn.dh.edu.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.dh.edu.module.system.api.user.AdminUserApi;
import cn.dh.edu.module.system.api.user.dto.AdminUserRespDTO;
import cn.dh.edu.module.system.enums.permission.RoleCodeEnum;
import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.edu.enums.ActivityInstanceStatusEnum.*;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.*;

@Slf4j
@Service
@Validated
public class ActivityInstanceServiceImpl implements ActivityInstanceService {

    private static final int MAX_ATTACHMENT_COUNT = 5;
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private ActivityInstanceMapper activityInstanceMapper;
    @Resource
    private ActivityInstanceRecordMapper activityInstanceRecordMapper;
    @Resource
    private ActivityInstanceEnrollmentMapper activityInstanceEnrollmentMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityOwnerMapper activityOwnerMapper;
    @Resource
    private ActivityParticipantMapper activityParticipantMapper;
    @Resource
    @Lazy
    private ActivityInstanceService self;
    @Resource
    @Lazy
    private ActivityService activityService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private NoticeApi noticeApi;
    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;
    @Resource
    private SmsSendApi smsSendApi;
    @Resource
    private ActivityEnrollTokenHelper activityEnrollTokenHelper;
    @Resource
    private ActivityInstanceEnrollmentService activityInstanceEnrollmentService;
    @Resource
    private ActivityInstanceFeedbackService activityInstanceFeedbackService;
    @Resource
    @Lazy
    private ActivityInstanceFeeService activityInstanceFeeService;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private RoleApi roleApi;

    private static final String PERMISSION_INSTANCE_UPDATE = "edu:activity-instance:update";

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void generateInstancesForActivity(Long activityId) {
        ActivityDO activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw exception(ACTIVITY_NOT_EXISTS);
        }

        Long existingCount = activityInstanceMapper.selectCountByActivityId(activityId);
        if (existingCount != null && existingCount > 0) {
            log.warn("[generateInstancesForActivity] 活动实例已存在，跳过，activityId: {}", activityId);
            return;
        }

        if (activity.getStartDate() == null) {
            throw exception(ACTIVITY_START_DATE_REQUIRED);
        }

        String cycleType = StringUtils.defaultIfBlank(activity.getCycleType(), ActivityCycleTypeEnum.ONCE.getType());
        if (!ActivityCycleTypeEnum.isOnce(cycleType)) {
            throw exception(ACTIVITY_CYCLE_TYPE_NOT_SUPPORTED);
        }

        createOnceInstance(activity);
    }

    @Override
    public void ensureInstancesForApprovedActivity(Long activityId) {
        ActivityDO activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return;
        }
        if (!BpmProcessInstanceStatusEnum.APPROVE.getStatus().equals(activity.getProcessStatus())) {
            return;
        }
        Long existingCount = activityInstanceMapper.selectCountByActivityId(activityId);
        if (existingCount != null && existingCount > 0) {
            return;
        }
        try {
            self.generateInstancesForActivity(activityId);
        } catch (Exception ex) {
            log.error("[ensureInstancesForApprovedActivity] 补偿生成活动实例失败，activityId: {}", activityId, ex);
        }
    }

    @Override
    public ActivityInstanceDetailRespVO getActivityInstanceDetail(Long id) {
        ActivityInstanceDO instance = validateExists(id);
        promoteLegacyPendingFeedback(instance);
        ActivityDO activity = activityMapper.selectById(instance.getActivityId());
        fillRuntimeStatus(instance, activity);

        ActivityInstanceDetailRespVO respVO = BeanUtils.toBean(instance, ActivityInstanceDetailRespVO.class);
        fillActivityInfo(respVO, instance.getActivityId());
        respVO.setActivity(activityService.getActivity(instance.getActivityId()));
        respVO.setRecord(buildRecordRespVO(instance.getId()));
        respVO.setRecordEditable(resolveRecordEditable(instance));
        respVO.setEnrollmentParticipants(
                activityInstanceEnrollmentService.getEnrollmentParticipantList(instance.getId()));
        respVO.setEnrolling(ENROLLING.getStatus().equals(instance.getStatus()));
        respVO.setFeedback(activityInstanceFeedbackService.getFeedbackDetail(instance.getId()));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveActivityInstanceRecord(ActivityInstanceRecordSaveReqVO saveReqVO) {
        ActivityInstanceDO instance = validateExists(saveReqVO.getInstanceId());
        ActivityDO activity = activityMapper.selectById(instance.getActivityId());
        fillRuntimeStatus(instance, activity);
        validateRecordSavable(instance);
        if (CollUtil.isNotEmpty(saveReqVO.getAttachments())
                && saveReqVO.getAttachments().size() > MAX_ATTACHMENT_COUNT) {
            throw exception(ACTIVITY_INSTANCE_RECORD_ATTACHMENT_LIMIT);
        }
        if (saveReqVO.getActualStartTime() != null && saveReqVO.getActualEndTime() != null
                && saveReqVO.getActualEndTime().isBefore(saveReqVO.getActualStartTime())) {
            throw exception(ACTIVITY_INSTANCE_RECORD_TIME_INVALID);
        }

        List<Long> expectedUserIds = resolveEnrolledUserIds(instance.getId());
        List<Long> absentUserIds = normalizeUserIds(saveReqVO.getAbsentUserIds());
        validateAbsentUsers(expectedUserIds, absentUserIds);
        int expectedCount = instance.getEnrollCount() != null ? instance.getEnrollCount() : expectedUserIds.size();
        int attendanceCount = Math.max(0, expectedCount - absentUserIds.size());

        ActivityInstanceRecordDO existing = activityInstanceRecordMapper.selectByInstanceId(instance.getId());
        ActivityInstanceRecordDO recordDO = existing != null ? existing : new ActivityInstanceRecordDO();
        recordDO.setInstanceId(instance.getId());
        recordDO.setActualStartTime(saveReqVO.getActualStartTime());
        recordDO.setActualEndTime(saveReqVO.getActualEndTime());
        recordDO.setSummary(saveReqVO.getSummary());
        recordDO.setAbsentUserIds(encodeLongList(absentUserIds));
        recordDO.setAttachments(encodeAttachments(saveReqVO.getAttachments()));
        if (existing == null) {
            recordDO.setTenantId(instance.getTenantId());
            activityInstanceRecordMapper.insert(recordDO);
        } else {
            activityInstanceRecordMapper.updateById(recordDO);
        }

        ActivityInstanceDO updateInstance = new ActivityInstanceDO();
        updateInstance.setId(instance.getId());
        LocalDateTime actualDate = saveReqVO.getActualDate() != null
                ? saveReqVO.getActualDate() : saveReqVO.getActualStartTime();
        updateInstance.setActualDate(actualDate);
        updateInstance.setAttendanceCount(attendanceCount);
        // 保存活动记录后直接结项，不再进入待反馈
        updateInstance.setStatus(COMPLETED.getStatus());
        activityInstanceMapper.updateById(updateInstance);
        activityInstanceFeeService.generateFeeItems(instance.getId());
    }

    @Override
    public PageResult<ActivityInstanceDO> getActivityInstancePage(ActivityInstancePageReqVO pageReqVO) {
        String filterStatus = pageReqVO.getStatus();
        if (ActivityInstanceStatusUtils.isRuntimeStatus(filterStatus)) {
            pageReqVO.setStatus(null);
        }
        List<Long> filterActivityIds = resolveFilterActivityIds(pageReqVO);
        if (filterActivityIds != null && filterActivityIds.isEmpty()) {
            return PageResult.empty();
        }
        boolean keywordMode = StringUtils.isNotBlank(pageReqVO.getKeyword());
        List<Long> keywordActivityIds = null;
        if (keywordMode) {
            keywordActivityIds = activityMapper.selectIdListByNameLike(pageReqVO.getKeyword().trim());
        }
        PageResult<ActivityInstanceDO> pageResult = activityInstanceMapper.selectPageByReq(
                pageReqVO, filterActivityIds, keywordActivityIds, keywordMode);
        fillRuntimeStatus(pageResult.getList());
        if (!ActivityInstanceStatusUtils.isRuntimeStatus(filterStatus)) {
            return pageResult;
        }
        List<ActivityInstanceDO> filtered = pageResult.getList().stream()
                .filter(item -> filterStatus.equals(item.getStatus()))
                .toList();
        return new PageResult<>(filtered, (long) filtered.size());
    }

    @Override
    public void fillRuntimeStatus(ActivityInstanceDO instance, ActivityDO activity) {
        activityInstanceEnrollmentService.fillRuntimeStatus(instance, activity);
    }

    public void fillRuntimeStatus(List<ActivityInstanceDO> instances) {
        if (CollUtil.isEmpty(instances)) {
            return;
        }
        Set<Long> activityIds = instances.stream()
                .map(ActivityInstanceDO::getActivityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, ActivityDO> activityMap = CollUtil.isEmpty(activityIds)
                ? Collections.emptyMap()
                : activityMapper.selectBatchIds(activityIds).stream()
                        .collect(Collectors.toMap(ActivityDO::getId, item -> item, (a, b) -> a));
        for (ActivityInstanceDO instance : instances) {
            fillRuntimeStatus(instance, activityMap.get(instance.getActivityId()));
        }
    }

    private ActivityInstanceRecordRespVO buildRecordRespVO(Long instanceId) {
        ActivityInstanceRecordDO recordDO = activityInstanceRecordMapper.selectByInstanceId(instanceId);
        if (recordDO == null) {
            return null;
        }

        // absentUserIds / attachments 在 DO 中为 JSON 字符串，不可直接 Bean 拷贝到 List 字段
        ActivityInstanceRecordRespVO respVO = new ActivityInstanceRecordRespVO();
        respVO.setId(recordDO.getId());
        respVO.setInstanceId(recordDO.getInstanceId());
        respVO.setActualStartTime(recordDO.getActualStartTime());
        respVO.setActualEndTime(recordDO.getActualEndTime());
        respVO.setSummary(recordDO.getSummary());

        List<Long> absentUserIds = decodeLongList(recordDO.getAbsentUserIds());
        respVO.setAbsentUserIds(absentUserIds);
        respVO.setAbsentUserNames(resolveUserNames(absentUserIds));
        respVO.setAttachments(decodeAttachments(recordDO.getAttachments()));
        return respVO;
    }

    private String encodeLongList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return null;
        }
        return JsonUtils.toJsonString(ids);
    }

    private List<Long> normalizeUserIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return ids.stream().filter(Objects::nonNull).distinct().toList();
    }

    private List<Long> resolveEnrolledUserIds(Long instanceId) {
        List<ActivityInstanceEnrollmentDO> enrollments =
                activityInstanceEnrollmentMapper.selectListByInstanceId(instanceId);
        if (CollUtil.isEmpty(enrollments)) {
            return Collections.emptyList();
        }
        return enrollments.stream()
                .map(ActivityInstanceEnrollmentDO::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private void validateAbsentUsers(List<Long> expectedUserIds, List<Long> absentUserIds) {
        if (CollUtil.isEmpty(absentUserIds)) {
            return;
        }
        Set<Long> expectedSet = new HashSet<>(expectedUserIds);
        for (Long absentUserId : absentUserIds) {
            if (!expectedSet.contains(absentUserId)) {
                throw exception(ACTIVITY_INSTANCE_RECORD_ABSENT_INVALID);
            }
        }
    }

    private List<Long> decodeLongList(String json) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        return JsonUtils.parseObject(json, new TypeReference<List<Long>>() {});
    }

    private String encodeAttachments(List<ActivityInstanceRecordAttachmentVO> attachments) {
        if (CollUtil.isEmpty(attachments)) {
            return null;
        }
        return JsonUtils.toJsonString(attachments);
    }

    private List<ActivityInstanceRecordAttachmentVO> decodeAttachments(String json) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        return JsonUtils.parseObject(json, new TypeReference<List<ActivityInstanceRecordAttachmentVO>>() {});
    }

    private String resolveUserNames(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return null;
        }
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(userIds);
        return userIds.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .map(this::formatUserDisplayName)
                .collect(Collectors.joining("、"));
    }

    private String formatUserDisplayName(AdminUserRespDTO user) {
        if (StringUtils.isNotBlank(user.getNickname())) {
            return user.getNickname();
        }
        return StringUtils.defaultString(user.getUsername());
    }

    private void createOnceInstance(ActivityDO activity) {
        int periodNo = resolveNextPeriodNo(activity.getId());
        Long tenantId = resolveActivityTenantId(activity.getId());
        ActivityInstanceDO instance = ActivityInstanceDO.builder()
                .activityId(activity.getId())
                .periodNo(periodNo)
                .instanceCode(formatInstanceCode(activity.getBillCode(), periodNo))
                .plannedDate(activity.getStartDate())
                .enrollCount(0)
                .attendanceCount(0)
                .build();
        instance.setTenantId(tenantId);
        activityInstanceMapper.insert(instance);
        publishInstanceNotice(activity, instance);
        log.info("[createOnceInstance] 创建活动实例成功，activityId: {}, periodNo: {}, instanceId: {}, code: {}",
                activity.getId(), periodNo, instance.getId(), instance.getInstanceCode());
    }

    private void publishInstanceNotice(ActivityDO activity, ActivityInstanceDO instance) {
        try {
            String plannedDate = instance.getPlannedDate() != null
                    ? instance.getPlannedDate().format(DATE_TIME_FORMATTER) : "-";
            String title = buildNoticeTitle(activity.getName(), instance.getPeriodNo());
            String content = buildNoticeContent(activity, instance, plannedDate);

            NoticeCreateReqDTO reqDTO = new NoticeCreateReqDTO();
            reqDTO.setTitle(title);
            reqDTO.setType(EduActivityNoticeConstants.NOTICE_TYPE);
            reqDTO.setContent(content);
            reqDTO.setStatus(CommonStatusEnum.ENABLE.getStatus());
            reqDTO.setIsImportant(Boolean.FALSE);
            reqDTO.setPush(Boolean.TRUE);
            reqDTO.setTenantId(instance.getTenantId());
            // 发布人 = 专项活动单据创建人
            reqDTO.setCreator(activity.getCreator());

            Long noticeId = publishNotice(reqDTO, instance.getTenantId());
            log.info("[publishInstanceNotice] 活动实例公告发布成功，instanceId: {}, noticeId: {}, creator: {}",
                    instance.getId(), noticeId, reqDTO.getCreator());
        } catch (Exception ex) {
            log.error("[publishInstanceNotice] 活动实例公告发布失败，instanceId: {}, code: {}",
                    instance.getId(), instance.getInstanceCode(), ex);
        }
        // 站内信：仅通知参与人中的学生，附报名链接
        sendInstanceCreatedNotify(activity, instance);
    }

    /**
     * 向活动参与人中的学生发送「实例已生成」站内信（每人一条），内容含报名页链接。
     */
    private void sendInstanceCreatedNotify(ActivityDO activity, ActivityInstanceDO instance) {
        Long tenantId = instance.getTenantId() != null && instance.getTenantId() > 0
                ? instance.getTenantId()
                : TenantContextHolder.getTenantId();
        if (tenantId == null || tenantId <= 0) {
            log.warn("[sendInstanceCreatedNotify] 租户为空，跳过站内信，instanceId: {}", instance.getId());
            return;
        }
        TenantUtils.execute(tenantId, () -> doSendInstanceCreatedNotify(activity, instance, tenantId));
    }

    private void doSendInstanceCreatedNotify(ActivityDO activity, ActivityInstanceDO instance, Long tenantId) {
        List<ActivityParticipantDO> participants =
                activityParticipantMapper.selectListByActivityId(activity.getId());
        if (CollUtil.isEmpty(participants)) {
            log.info("[sendInstanceCreatedNotify] 无参与人，跳过，instanceId: {}", instance.getId());
            return;
        }
        Set<Long> studentUserIds = resolveUserIdsByRoleCode(RoleCodeEnum.STUDENT.getCode());
        Set<Long> receiverIds = participants.stream()
                .map(ActivityParticipantDO::getUserId)
                .filter(Objects::nonNull)
                .filter(studentUserIds::contains)
                .collect(Collectors.toCollection(HashSet::new));
        if (receiverIds.isEmpty()) {
            log.info("[sendInstanceCreatedNotify] 无学生参与人，跳过，instanceId: {}", instance.getId());
            return;
        }

        String plannedDate = instance.getPlannedDate() != null
                ? instance.getPlannedDate().format(DATE_TIME_FORMATTER) : "-";
        Map<String, Object> params = new HashMap<>();
        params.put("activityName", StringUtils.defaultIfBlank(activity.getName(), "未命名活动"));
        params.put("periodNo", String.valueOf(instance.getPeriodNo()));
        params.put("instanceCode", StringUtils.defaultString(instance.getInstanceCode()));
        params.put("plannedDate", plannedDate);

        int success = 0;
        int smsSuccess = 0;
        for (Long userId : receiverIds) {
            String enrollUrl = activityEnrollTokenHelper.buildEnrollUrl(userId, instance.getId(), tenantId);
            log.info("[sendInstanceCreatedNotify] 报名短链，instanceId: {}, userId: {}, enrollUrl: {}",
                    instance.getId(), userId, enrollUrl);
            Map<String, Object> userParams = new HashMap<>(params);
            userParams.put("enrollUrl", enrollUrl);
            try {
                NotifySendSingleToUserReqDTO req = new NotifySendSingleToUserReqDTO();
                req.setUserId(userId);
                req.setTemplateCode(EduActivityNoticeConstants.NOTIFY_TEMPLATE_INSTANCE_CREATED);
                req.setTemplateParams(userParams);
                Long messageId = notifyMessageSendApi.sendSingleMessageToAdmin(req).getCheckedData();
                if (messageId != null) {
                    success++;
                } else {
                    log.warn("[sendInstanceCreatedNotify] 返回空消息ID，可能模板未启用，instanceId: {}, userId: {}, tenantId: {}",
                            instance.getId(), userId, tenantId);
                }
            } catch (Exception ex) {
                log.warn("[sendInstanceCreatedNotify] 发送失败，instanceId: {}, userId: {}, tenantId: {}",
                        instance.getId(), userId, tenantId, ex);
            }
            try {
                SmsSendSingleToUserReqDTO smsReq = new SmsSendSingleToUserReqDTO();
                smsReq.setUserId(userId);
                smsReq.setTemplateCode(EduActivityNoticeConstants.SMS_TEMPLATE_INSTANCE_ENROLL);
                smsReq.setTemplateParams(userParams);
                Long smsLogId = smsSendApi.sendSingleSmsToAdmin(smsReq).getCheckedData();
                if (smsLogId != null) {
                    smsSuccess++;
                }
            } catch (Exception ex) {
                log.warn("[sendInstanceCreatedNotify] 短信发送失败（可稍后配置模板），instanceId: {}, userId: {}, tenantId: {}",
                        instance.getId(), userId, tenantId, ex);
            }
        }
        log.info("[sendInstanceCreatedNotify] 站内信/短信发送完成，instanceId: {}, tenantId: {}, receivers: {}, notifySuccess: {}, smsSuccess: {}",
                instance.getId(), tenantId, receiverIds.size(), success, smsSuccess);
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

    private Long publishNotice(NoticeCreateReqDTO reqDTO, Long tenantId) {
        if (tenantId != null && tenantId > 0) {
            return TenantUtils.execute(tenantId, () -> noticeApi.createAndPushNotice(reqDTO).getCheckedData());
        }
        return noticeApi.createAndPushNotice(reqDTO).getCheckedData();
    }

    private String buildNoticeTitle(String activityName, Integer periodNo) {
        String suffix = " 第" + periodNo + "期";
        String name = StringUtils.defaultIfBlank(activityName, "未命名活动");
        int maxNameLen = 50 - suffix.length();
        if (maxNameLen < 1) {
            return StringUtils.abbreviate(name, 50);
        }
        if (name.length() > maxNameLen) {
            name = name.substring(0, maxNameLen);
        }
        return name + suffix;
    }

    private String buildNoticeContent(ActivityDO activity, ActivityInstanceDO instance, String plannedDate) {
        StringBuilder content = new StringBuilder();
        content.append("<p>专项活动「").append(StringUtils.defaultString(activity.getName())).append("」已生成活动实例。</p>");
        content.append("<p>实例编号：").append(instance.getInstanceCode()).append("</p>");
        content.append("<p>期次：第 ").append(instance.getPeriodNo()).append(" 期</p>");
        content.append("<p>计划执行时间：").append(plannedDate).append("</p>");
        if (StringUtils.isNotBlank(activity.getContent())) {
            content.append("<p>活动内容：").append(activity.getContent()).append("</p>");
        }
        return content.toString();
    }

    private int resolveNextPeriodNo(Long activityId) {
        return activityInstanceMapper.selectMaxPeriodNoByActivityId(activityId) + 1;
    }

    private Long resolveActivityTenantId(Long activityId) {
        Long tenantId = activityMapper.selectTenantIdById(activityId);
        if (tenantId != null && tenantId > 0) {
            return tenantId;
        }
        Long contextTenantId = TenantContextHolder.getTenantId();
        if (contextTenantId != null && contextTenantId > 0) {
            return contextTenantId;
        }
        throw exception(ACTIVITY_NOT_EXISTS);
    }

    private String formatInstanceCode(String billCode, int periodNo) {
        return "INST-" + billCode + "-" + String.format("%03d", periodNo);
    }

    private ActivityInstanceDO validateExists(Long id) {
        ActivityInstanceDO instance = activityInstanceMapper.selectById(id);
        if (instance == null) {
            throw exception(ACTIVITY_INSTANCE_NOT_EXISTS);
        }
        return instance;
    }

    private void fillActivityInfo(ActivityInstanceRespVO respVO, Long activityId) {
        ActivityDO activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return;
        }
        respVO.setActivityBillCode(activity.getBillCode());
        respVO.setActivityName(activity.getName());
    }

    /**
     * 按活动名称模糊筛选：先查活动 ID 列表，再过滤实例
     */
    private List<Long> resolveFilterActivityIds(ActivityInstancePageReqVO pageReqVO) {
        if (StringUtils.isBlank(pageReqVO.getActivityName())) {
            return null;
        }
        return activityMapper.selectIdListByNameLike(pageReqVO.getActivityName().trim());
    }

    private boolean resolveRecordEditable(ActivityInstanceDO instance) {
        if (!ActivityInstanceStatusUtils.isRecordEditable(instance.getStatus())) {
            return false;
        }
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            return false;
        }
        if (isStudentUser(loginUserId)) {
            return false;
        }
        if (!Boolean.TRUE.equals(
                permissionApi.hasAnyPermissions(loginUserId, PERMISSION_INSTANCE_UPDATE).getCheckedData())) {
            return false;
        }
        return canManageActivityRecord(instance.getActivityId(), loginUserId);
    }

    /**
     * 兼容历史数据：已有活动记录但仍停留在「待反馈」的实例，升级为已结项并生成费用明细。
     */
    private void promoteLegacyPendingFeedback(ActivityInstanceDO instance) {
        if (instance == null || !PENDING_FEEDBACK.getStatus().equals(instance.getStatus())) {
            return;
        }
        if (activityInstanceRecordMapper.selectByInstanceId(instance.getId()) == null) {
            return;
        }
        ActivityInstanceDO update = new ActivityInstanceDO();
        update.setId(instance.getId());
        update.setStatus(COMPLETED.getStatus());
        activityInstanceMapper.updateById(update);
        instance.setStatus(COMPLETED.getStatus());
        activityInstanceFeeService.generateFeeItems(instance.getId());
        log.info("[promoteLegacyPendingFeedback] 历史待反馈实例已升级为已结项，instanceId: {}", instance.getId());
    }

    private void validateRecordSavable(ActivityInstanceDO instance) {
        if (!ActivityInstanceStatusUtils.isRecordEditable(instance.getStatus())) {
            throw exception(ACTIVITY_INSTANCE_RECORD_NOT_EDITABLE);
        }
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            throw exception(ACTIVITY_INSTANCE_RECORD_NOT_EDITABLE);
        }
        if (isStudentUser(loginUserId)) {
            throw exception(ACTIVITY_INSTANCE_RECORD_STUDENT_FORBIDDEN);
        }
        if (!Boolean.TRUE.equals(
                permissionApi.hasAnyPermissions(loginUserId, PERMISSION_INSTANCE_UPDATE).getCheckedData())) {
            throw exception(ACTIVITY_INSTANCE_RECORD_NOT_EDITABLE);
        }
        if (!canManageActivityRecord(instance.getActivityId(), loginUserId)) {
            throw exception(ACTIVITY_INSTANCE_RECORD_OWNER_OR_ADMIN_ONLY);
        }
    }

    /** 活动负责人或管理员（超管 / 租户管理员）可维护活动记录 */
    private boolean canManageActivityRecord(Long activityId, Long userId) {
        if (activityId == null || userId == null) {
            return false;
        }
        if (isAdminUser(userId)) {
            return true;
        }
        return isActivityOwner(activityId, userId);
    }

    private boolean isActivityOwner(Long activityId, Long userId) {
        List<ActivityOwnerDO> owners = activityOwnerMapper.selectListByActivityId(activityId);
        if (CollUtil.isEmpty(owners)) {
            return false;
        }
        return owners.stream()
                .map(ActivityOwnerDO::getUserId)
                .filter(Objects::nonNull)
                .anyMatch(userId::equals);
    }

    private boolean isAdminUser(Long userId) {
        return Boolean.TRUE.equals(permissionApi.hasAnyRoles(
                userId,
                RoleCodeEnum.SUPER_ADMIN.getCode(),
                RoleCodeEnum.TENANT_ADMIN.getCode()).getCheckedData());
    }

    private boolean isStudentUser(Long userId) {
        return Boolean.TRUE.equals(
                permissionApi.hasAnyRoles(userId, RoleCodeEnum.STUDENT.getCode()).getCheckedData());
    }

}
