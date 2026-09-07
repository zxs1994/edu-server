package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.util.json.JsonUtils;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.edu.module.edu.controller.admin.activity.vo.*;
import cn.dh.edu.module.edu.dal.dataobject.activity.*;
import cn.dh.edu.module.edu.dal.mysql.activity.*;
import cn.dh.edu.module.edu.util.ActivityInstanceStatusUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.dh.edu.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.dh.edu.module.edu.enums.ActivityInstanceStatusEnum.*;
import static cn.dh.edu.module.edu.enums.ErrorCodeConstants.*;

@Slf4j
@Service
@Validated
public class ActivityInstanceFeedbackServiceImpl implements ActivityInstanceFeedbackService {

    @Resource
    private ActivityInstanceMapper activityInstanceMapper;
    @Resource
    private ActivityInstanceRecordMapper activityInstanceRecordMapper;
    @Resource
    private ActivityInstanceEnrollmentMapper activityInstanceEnrollmentMapper;
    @Resource
    private ActivityParticipantMapper activityParticipantMapper;
    @Resource
    private ActivityInstanceStudentFeedbackMapper studentFeedbackMapper;
    @Resource
    private ActivityInstanceTeacherFeedbackMapper teacherFeedbackMapper;
    @Resource
    private ActivityInstanceAdminFeedbackMapper adminFeedbackMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityOwnerMapper activityOwnerMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private RoleApi roleApi;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private ActivityInstanceFeeService activityInstanceFeeService;

    @Override
    public ActivityInstanceFeedbackRespVO getFeedbackDetail(Long instanceId) {
        ActivityInstanceDO instance = validateInstanceExists(instanceId);
        ActivityDO activity = activityMapper.selectById(instance.getActivityId());
        fillRuntimeStatus(instance, activity);

        List<Long> expectedStudentIds = resolveExpectedStudentUserIds(instanceId);
        List<Long> expectedTeacherIds = resolveExpectedTeacherUserIds(instance.getActivityId());
        List<ActivityInstanceStudentFeedbackDO> studentFeedbackList =
                studentFeedbackMapper.selectListByInstanceId(instanceId);
        List<ActivityInstanceTeacherFeedbackDO> teacherFeedbackList =
                teacherFeedbackMapper.selectListByInstanceId(instanceId);
        ActivityInstanceAdminFeedbackDO adminFeedbackDO = adminFeedbackMapper.selectByInstanceId(instanceId);

        Map<Long, ActivityInstanceStudentFeedbackDO> studentFeedbackMap = studentFeedbackList.stream()
                .collect(Collectors.toMap(ActivityInstanceStudentFeedbackDO::getUserId, item -> item, (a, b) -> a));
        Map<Long, ActivityInstanceTeacherFeedbackDO> teacherFeedbackMap = teacherFeedbackList.stream()
                .collect(Collectors.toMap(ActivityInstanceTeacherFeedbackDO::getUserId, item -> item, (a, b) -> a));

        Set<Long> allUserIds = new HashSet<>();
        allUserIds.addAll(expectedStudentIds);
        allUserIds.addAll(expectedTeacherIds);
        if (adminFeedbackDO != null && adminFeedbackDO.getSubmitterUserId() != null) {
            allUserIds.add(adminFeedbackDO.getSubmitterUserId());
        }
        Map<Long, AdminUserRespDTO> userMap = CollUtil.isEmpty(allUserIds)
                ? Collections.emptyMap()
                : adminUserApi.getUserMap(allUserIds);

        ActivityInstanceFeedbackRespVO respVO = new ActivityInstanceFeedbackRespVO();
        boolean feedbackEditable = ActivityInstanceStatusUtils.isFeedbackEditable(instance.getStatus());
        respVO.setFeedbackEditable(feedbackEditable);
        respVO.setExpectedStudentCount(expectedStudentIds.size());
        respVO.setExpectedTeacherCount(expectedTeacherIds.size());
        respVO.setStudentFeedbacks(buildStudentFeedbackList(expectedStudentIds, studentFeedbackMap, userMap));
        respVO.setTeacherFeedbacks(buildTeacherFeedbackList(expectedTeacherIds, teacherFeedbackMap, userMap));
        respVO.setAdminFeedback(buildAdminFeedbackResp(adminFeedbackDO, userMap));

        int submittedStudentCount = (int) respVO.getStudentFeedbacks().stream()
                .filter(item -> Boolean.TRUE.equals(item.getSubmitted())).count();
        int submittedTeacherCount = (int) respVO.getTeacherFeedbacks().stream()
                .filter(item -> Boolean.TRUE.equals(item.getSubmitted())).count();
        boolean adminSubmitted = respVO.getAdminFeedback() != null
                && Boolean.TRUE.equals(respVO.getAdminFeedback().getSubmitted());

        respVO.setSubmittedStudentCount(submittedStudentCount);
        respVO.setSubmittedTeacherCount(submittedTeacherCount);
        respVO.setAdminFeedbackSubmitted(adminSubmitted);
        respVO.setAllFeedbackCompleted(isAllFeedbackCompleted(
                expectedStudentIds.size(), submittedStudentCount,
                expectedTeacherIds.size(), submittedTeacherCount, adminSubmitted));

        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        respVO.setCanSubmitStudentFeedback(feedbackEditable && loginUserId != null
                && expectedStudentIds.contains(loginUserId)
                && isStudentUser(loginUserId)
                && !studentFeedbackMap.containsKey(loginUserId));
        respVO.setCanSubmitTeacherFeedback(feedbackEditable && loginUserId != null
                && expectedTeacherIds.contains(loginUserId)
                && isTeacherUser(loginUserId)
                && !teacherFeedbackMap.containsKey(loginUserId));
        respVO.setCanSubmitAdminFeedback(feedbackEditable && loginUserId != null
                && isActivityOwner(instance.getActivityId(), loginUserId)
                && adminFeedbackDO == null);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveStudentFeedback(ActivityInstanceStudentFeedbackSaveReqVO saveReqVO) {
        Long userId = requireLoginUserId();
        ActivityInstanceDO instance = validateFeedbackEditable(saveReqVO.getInstanceId());
        validateRecordExists(saveReqVO.getInstanceId());
        if (!isStudentUser(userId)) {
            throw exception(ACTIVITY_INSTANCE_FEEDBACK_STUDENT_ONLY);
        }
        List<Long> expectedStudentIds = resolveExpectedStudentUserIds(saveReqVO.getInstanceId());
        if (!expectedStudentIds.contains(userId)) {
            throw exception(ACTIVITY_INSTANCE_STUDENT_FEEDBACK_NOT_REQUIRED);
        }

        ActivityInstanceStudentFeedbackDO existing =
                studentFeedbackMapper.selectByInstanceIdAndUserId(saveReqVO.getInstanceId(), userId);
        if (existing != null) {
            throw exception(ACTIVITY_INSTANCE_FEEDBACK_ALREADY_SUBMITTED);
        }
        ActivityInstanceStudentFeedbackDO feedbackDO = new ActivityInstanceStudentFeedbackDO();
        feedbackDO.setInstanceId(saveReqVO.getInstanceId());
        feedbackDO.setUserId(userId);
        feedbackDO.setSatisfaction(saveReqVO.getSatisfaction());
        feedbackDO.setHarvest(saveReqVO.getHarvest());
        feedbackDO.setContent(saveReqVO.getContent());
        feedbackDO.setSubmitTime(LocalDateTime.now());
        feedbackDO.setTenantId(instance.getTenantId());
        studentFeedbackMapper.insert(feedbackDO);
        tryCompleteLegacyPendingFeedback(saveReqVO.getInstanceId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTeacherFeedback(ActivityInstanceTeacherFeedbackSaveReqVO saveReqVO) {
        Long userId = requireLoginUserId();
        ActivityInstanceDO instance = validateFeedbackEditable(saveReqVO.getInstanceId());
        validateRecordExists(saveReqVO.getInstanceId());
        if (!isTeacherUser(userId)) {
            throw exception(ACTIVITY_INSTANCE_FEEDBACK_TEACHER_ONLY);
        }
        List<Long> expectedTeacherIds = resolveExpectedTeacherUserIds(instance.getActivityId());
        if (!expectedTeacherIds.contains(userId)) {
            throw exception(ACTIVITY_INSTANCE_TEACHER_FEEDBACK_NOT_REQUIRED);
        }

        ActivityInstanceTeacherFeedbackDO existing =
                teacherFeedbackMapper.selectByInstanceIdAndUserId(saveReqVO.getInstanceId(), userId);
        if (existing != null) {
            throw exception(ACTIVITY_INSTANCE_FEEDBACK_ALREADY_SUBMITTED);
        }
        ActivityInstanceTeacherFeedbackDO feedbackDO = new ActivityInstanceTeacherFeedbackDO();
        feedbackDO.setInstanceId(saveReqVO.getInstanceId());
        feedbackDO.setUserId(userId);
        feedbackDO.setSummary(saveReqVO.getSummary());
        feedbackDO.setProblem(saveReqVO.getProblem());
        feedbackDO.setSuggestion(saveReqVO.getSuggestion());
        feedbackDO.setSubmitTime(LocalDateTime.now());
        feedbackDO.setTenantId(instance.getTenantId());
        teacherFeedbackMapper.insert(feedbackDO);
        tryCompleteLegacyPendingFeedback(saveReqVO.getInstanceId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAdminFeedback(ActivityInstanceAdminFeedbackSaveReqVO saveReqVO) {
        Long userId = requireLoginUserId();
        ActivityInstanceDO instance = validateFeedbackEditable(saveReqVO.getInstanceId());
        if (!isActivityOwner(instance.getActivityId(), userId)) {
            throw exception(ACTIVITY_INSTANCE_ADMIN_FEEDBACK_OWNER_ONLY);
        }
        validateRecordExists(saveReqVO.getInstanceId());

        ActivityInstanceAdminFeedbackDO existing = adminFeedbackMapper.selectByInstanceId(saveReqVO.getInstanceId());
        if (existing != null) {
            throw exception(ACTIVITY_INSTANCE_FEEDBACK_ALREADY_SUBMITTED);
        }
        ActivityInstanceAdminFeedbackDO feedbackDO = new ActivityInstanceAdminFeedbackDO();
        feedbackDO.setInstanceId(saveReqVO.getInstanceId());
        feedbackDO.setSubmitterUserId(userId);
        feedbackDO.setQualityScore(saveReqVO.getQualityScore());
        feedbackDO.setClosingOpinion(saveReqVO.getClosingOpinion());
        feedbackDO.setSubmitTime(LocalDateTime.now());
        feedbackDO.setTenantId(instance.getTenantId());
        adminFeedbackMapper.insert(feedbackDO);
        tryCompleteLegacyPendingFeedback(saveReqVO.getInstanceId());
    }

    @Override
    public boolean canSubmitStudentFeedback(Long instanceId, Long userId) {
        if (instanceId == null || userId == null) {
            return false;
        }
        ActivityInstanceDO instance = activityInstanceMapper.selectById(instanceId);
        if (instance == null) {
            return false;
        }
        ActivityDO activity = activityMapper.selectById(instance.getActivityId());
        fillRuntimeStatus(instance, activity);
        if (!ActivityInstanceStatusUtils.isFeedbackEditable(instance.getStatus())) {
            return false;
        }
        if (activityInstanceRecordMapper.selectByInstanceId(instanceId) == null) {
            return false;
        }
        if (!isStudentUser(userId)) {
            return false;
        }
        if (!resolveExpectedStudentUserIds(instanceId).contains(userId)) {
            return false;
        }
        return studentFeedbackMapper.selectByInstanceIdAndUserId(instanceId, userId) == null;
    }

    @Override
    public ActivityInstanceStudentFeedbackRespVO getMyStudentFeedback(Long instanceId, Long userId) {
        if (instanceId == null || userId == null) {
            return null;
        }
        ActivityInstanceStudentFeedbackDO feedback =
                studentFeedbackMapper.selectByInstanceIdAndUserId(instanceId, userId);
        if (feedback == null) {
            return null;
        }
        ActivityInstanceStudentFeedbackRespVO vo =
                BeanUtils.toBean(feedback, ActivityInstanceStudentFeedbackRespVO.class);
        vo.setSubmitted(true);
        vo.setUserId(userId);
        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user != null) {
            vo.setUserName(formatUserName(user));
        }
        return vo;
    }

    /**
     * 兼容历史「待反馈」实例：反馈齐套后结项并生成费用（新流程已在保存记录时结项）。
     */
    private void tryCompleteLegacyPendingFeedback(Long instanceId) {
        ActivityInstanceDO instance = activityInstanceMapper.selectById(instanceId);
        if (instance == null || !PENDING_FEEDBACK.getStatus().equals(instance.getStatus())) {
            return;
        }
        List<Long> expectedStudentIds = resolveExpectedStudentUserIds(instanceId);
        List<Long> expectedTeacherIds = resolveExpectedTeacherUserIds(instance.getActivityId());
        int submittedStudentCount = studentFeedbackMapper.selectListByInstanceId(instanceId).size();
        int submittedTeacherCount = teacherFeedbackMapper.selectListByInstanceId(instanceId).size();
        ActivityInstanceAdminFeedbackDO adminFeedback = adminFeedbackMapper.selectByInstanceId(instanceId);
        boolean adminSubmitted = adminFeedback != null;

        if (!isAllFeedbackCompleted(expectedStudentIds.size(), submittedStudentCount,
                expectedTeacherIds.size(), submittedTeacherCount, adminSubmitted)) {
            return;
        }

        ActivityInstanceDO update = new ActivityInstanceDO();
        update.setId(instanceId);
        update.setStatus(COMPLETED.getStatus());
        activityInstanceMapper.updateById(update);
        log.info("[tryCompleteLegacyPendingFeedback] 历史待反馈实例反馈齐套，已结项，instanceId: {}", instanceId);
        activityInstanceFeeService.generateFeeItems(instanceId);
    }

    private boolean isAllFeedbackCompleted(int expectedStudentCount, int submittedStudentCount,
                                           int expectedTeacherCount, int submittedTeacherCount,
                                           boolean adminSubmitted) {
        if (expectedStudentCount > 0 && submittedStudentCount < expectedStudentCount) {
            return false;
        }
        if (expectedTeacherCount > 0 && submittedTeacherCount < expectedTeacherCount) {
            return false;
        }
        return adminSubmitted;
    }

    /** 应反馈学生：已报名且未缺席 */
    private List<Long> resolveExpectedStudentUserIds(Long instanceId) {
        List<Long> enrolledUserIds = activityInstanceEnrollmentMapper.selectListByInstanceId(instanceId).stream()
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

    /** 应反馈教培：活动参与人中的教培角色 */
    private List<Long> resolveExpectedTeacherUserIds(Long activityId) {
        List<ActivityParticipantDO> participants = activityParticipantMapper.selectListByActivityId(activityId);
        if (CollUtil.isEmpty(participants)) {
            return Collections.emptyList();
        }
        Set<Long> teacherUserIds = resolveUserIdsByRoleCode(RoleCodeEnum.TEACHER.getCode());
        return participants.stream()
                .map(ActivityParticipantDO::getUserId)
                .filter(Objects::nonNull)
                .filter(teacherUserIds::contains)
                .distinct()
                .toList();
    }

    private Set<Long> resolveAbsentUserIds(Long instanceId) {
        ActivityInstanceRecordDO record = activityInstanceRecordMapper.selectByInstanceId(instanceId);
        if (record == null || StringUtils.isBlank(record.getAbsentUserIds())) {
            return Collections.emptySet();
        }
        List<Long> absentUserIds = JsonUtils.parseObject(record.getAbsentUserIds(), new TypeReference<List<Long>>() {});
        return absentUserIds != null ? new HashSet<>(absentUserIds) : Collections.emptySet();
    }

    private List<ActivityInstanceStudentFeedbackRespVO> buildStudentFeedbackList(
            List<Long> expectedStudentIds,
            Map<Long, ActivityInstanceStudentFeedbackDO> feedbackMap,
            Map<Long, AdminUserRespDTO> userMap) {
        List<ActivityInstanceStudentFeedbackRespVO> result = new ArrayList<>();
        for (Long userId : expectedStudentIds) {
            ActivityInstanceStudentFeedbackDO feedback = feedbackMap.get(userId);
            ActivityInstanceStudentFeedbackRespVO vo = feedback != null
                    ? BeanUtils.toBean(feedback, ActivityInstanceStudentFeedbackRespVO.class)
                    : new ActivityInstanceStudentFeedbackRespVO();
            vo.setUserId(userId);
            vo.setUserName(formatUserName(userMap.get(userId)));
            vo.setSubmitted(feedback != null);
            result.add(vo);
        }
        return result;
    }

    private List<ActivityInstanceTeacherFeedbackRespVO> buildTeacherFeedbackList(
            List<Long> expectedTeacherIds,
            Map<Long, ActivityInstanceTeacherFeedbackDO> feedbackMap,
            Map<Long, AdminUserRespDTO> userMap) {
        List<ActivityInstanceTeacherFeedbackRespVO> result = new ArrayList<>();
        for (Long userId : expectedTeacherIds) {
            ActivityInstanceTeacherFeedbackDO feedback = feedbackMap.get(userId);
            ActivityInstanceTeacherFeedbackRespVO vo = feedback != null
                    ? BeanUtils.toBean(feedback, ActivityInstanceTeacherFeedbackRespVO.class)
                    : new ActivityInstanceTeacherFeedbackRespVO();
            vo.setUserId(userId);
            vo.setUserName(formatUserName(userMap.get(userId)));
            vo.setSubmitted(feedback != null);
            result.add(vo);
        }
        return result;
    }

    private ActivityInstanceAdminFeedbackRespVO buildAdminFeedbackResp(
            ActivityInstanceAdminFeedbackDO adminFeedbackDO,
            Map<Long, AdminUserRespDTO> userMap) {
        ActivityInstanceAdminFeedbackRespVO vo = new ActivityInstanceAdminFeedbackRespVO();
        if (adminFeedbackDO == null) {
            vo.setSubmitted(false);
            return vo;
        }
        BeanUtils.copyProperties(adminFeedbackDO, vo);
        vo.setSubmitted(true);
        vo.setSubmitterUserName(formatUserName(userMap.get(adminFeedbackDO.getSubmitterUserId())));
        return vo;
    }

    private ActivityInstanceDO validateFeedbackEditable(Long instanceId) {
        ActivityInstanceDO instance = validateInstanceExists(instanceId);
        ActivityDO activity = activityMapper.selectById(instance.getActivityId());
        fillRuntimeStatus(instance, activity);
        if (!ActivityInstanceStatusUtils.isFeedbackEditable(instance.getStatus())) {
            throw exception(ACTIVITY_INSTANCE_FEEDBACK_NOT_EDITABLE);
        }
        return instance;
    }

    private void validateRecordExists(Long instanceId) {
        if (activityInstanceRecordMapper.selectByInstanceId(instanceId) == null) {
            throw exception(ACTIVITY_INSTANCE_FEEDBACK_RECORD_REQUIRED);
        }
    }

    private ActivityInstanceDO validateInstanceExists(Long instanceId) {
        ActivityInstanceDO instance = activityInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw exception(ACTIVITY_INSTANCE_NOT_EXISTS);
        }
        return instance;
    }

    private void fillRuntimeStatus(ActivityInstanceDO instance, ActivityDO activity) {
        if (instance == null) {
            return;
        }
        String resolved = ActivityInstanceStatusUtils.resolveStatus(
                instance.getStatus(),
                instance.getPlannedDate(),
                activity != null ? activity.getEnrollStartTime() : null,
                activity != null ? activity.getEnrollEndTime() : null);
        instance.setStatus(resolved);
    }

    private boolean isStudentUser(Long userId) {
        return Boolean.TRUE.equals(
                permissionApi.hasAnyRoles(userId, RoleCodeEnum.STUDENT.getCode()).getCheckedData());
    }

    private boolean isTeacherUser(Long userId) {
        return Boolean.TRUE.equals(
                permissionApi.hasAnyRoles(userId, RoleCodeEnum.TEACHER.getCode()).getCheckedData());
    }

    private boolean isActivityOwner(Long activityId, Long userId) {
        if (activityId == null || userId == null) {
            return false;
        }
        return resolveOwnerUserIds(activityId).contains(userId);
    }

    private List<Long> resolveOwnerUserIds(Long activityId) {
        List<ActivityOwnerDO> owners = activityOwnerMapper.selectListByActivityId(activityId);
        if (CollUtil.isEmpty(owners)) {
            return Collections.emptyList();
        }
        return owners.stream()
                .map(ActivityOwnerDO::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
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

    private Long requireLoginUserId() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw new IllegalStateException("请先登录");
        }
        return userId;
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
