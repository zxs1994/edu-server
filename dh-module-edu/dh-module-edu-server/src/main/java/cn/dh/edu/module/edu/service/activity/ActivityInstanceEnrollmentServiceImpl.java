package cn.dh.edu.module.edu.service.activity;

import cn.dh.edu.framework.common.pojo.PageResult;
import cn.dh.edu.framework.common.util.object.BeanUtils;
import cn.dh.edu.framework.security.core.util.SecurityFrameworkUtils;
import cn.dh.edu.framework.tenant.core.util.TenantUtils;
import cn.dh.edu.module.edu.controller.admin.activity.vo.*;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceEnrollmentDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityInstanceStudentFeedbackDO;
import cn.dh.edu.module.edu.dal.dataobject.activity.ActivityParticipantDO;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityInstanceEnrollmentMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityInstanceMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityInstanceStudentFeedbackMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityMapper;
import cn.dh.edu.module.edu.dal.mysql.activity.ActivityParticipantMapper;
import cn.dh.edu.module.edu.util.ActivityEnrollTokenHelper;
import cn.dh.edu.module.edu.util.ActivityInstanceStatusUtils;
import cn.dh.edu.module.system.api.permission.PermissionApi;
import cn.dh.edu.module.system.api.permission.RoleApi;
import cn.dh.edu.module.system.api.user.AdminUserApi;
import cn.dh.edu.module.system.api.user.dto.AdminUserRespDTO;
import cn.dh.edu.module.system.enums.permission.RoleCodeEnum;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
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

@Service
@Validated
public class ActivityInstanceEnrollmentServiceImpl implements ActivityInstanceEnrollmentService {

    @Resource
    private ActivityInstanceMapper activityInstanceMapper;
    @Resource
    private ActivityInstanceEnrollmentMapper activityInstanceEnrollmentMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Resource
    private ActivityParticipantMapper activityParticipantMapper;
    @Resource
    private ActivityInstanceStudentFeedbackMapper studentFeedbackMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private RoleApi roleApi;
    @Resource
    private PermissionApi permissionApi;
    @Resource
    private ActivityInstanceFeedbackService activityInstanceFeedbackService;
    @Resource
    private ActivityEnrollTokenHelper activityEnrollTokenHelper;

    @Override
    public void fillRuntimeStatus(ActivityInstanceDO instance, ActivityDO activity) {
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

    @Override
    public PageResult<ActivityInstanceEnrollableRespVO> getMyEnrollablePage(
            ActivityInstanceMyPageReqVO pageReqVO) {
        Long userId = requireLoginUserId();
        validateStudentUser(userId);
        List<ActivityParticipantDO> participations = activityParticipantMapper.selectListByUserId(userId);
        if (CollUtil.isEmpty(participations)) {
            return PageResult.empty();
        }
        Set<Long> activityIds = participations.stream()
                .map(ActivityParticipantDO::getActivityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(activityIds)) {
            return PageResult.empty();
        }

        List<ActivityInstanceDO> allInstances = activityInstanceMapper.selectList(
                ActivityInstanceDO::getActivityId, activityIds);
        if (CollUtil.isEmpty(allInstances)) {
            return PageResult.empty();
        }

        Map<Long, ActivityDO> activityMap = activityMapper.selectBatchIds(activityIds).stream()
                .collect(Collectors.toMap(ActivityDO::getId, item -> item, (a, b) -> a));

        List<Long> instanceIds = allInstances.stream()
                .map(ActivityInstanceDO::getId)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, Integer> feedbackCountMap = buildFeedbackCountMap(instanceIds);

        List<ActivityInstanceEnrollableRespVO> matched = new ArrayList<>();
        for (ActivityInstanceDO instance : allInstances) {
            ActivityDO activity = activityMap.get(instance.getActivityId());
            if (activity == null || activity.getEnrollStartTime() == null || activity.getEnrollEndTime() == null) {
                continue;
            }
            fillRuntimeStatus(instance, activity);
            ActivityInstanceEnrollmentDO enrollment = activityInstanceEnrollmentMapper
                    .selectByInstanceIdAndUserId(instance.getId(), userId);
            boolean enrolled = enrollment != null;
            boolean enrolling = ENROLLING.getStatus().equals(instance.getStatus());
            ActivityInstanceEnrollableRespVO vo = BeanUtils.toBean(instance, ActivityInstanceEnrollableRespVO.class);
            fillActivityBrief(vo, activity);
            vo.setEnrollStartTime(activity.getEnrollStartTime());
            vo.setEnrollEndTime(activity.getEnrollEndTime());
            vo.setEnrolled(enrolled);
            vo.setCanEnroll(enrolling && !enrolled && !isEnrollFull(instance));
            vo.setCanCancel(enrolling && enrolled);
            boolean canSubmitStudentFeedback =
                    activityInstanceFeedbackService.canSubmitStudentFeedback(instance.getId(), userId);
            vo.setCanSubmitStudentFeedback(canSubmitStudentFeedback);
            vo.setStudentFeedbackSubmitted(
                    activityInstanceFeedbackService.getMyStudentFeedback(instance.getId(), userId) != null);
            vo.setFeedbackCount(feedbackCountMap.getOrDefault(instance.getId(), 0));
            fillMyFeedbackStatus(vo);
            if (!matchMyPageFilter(vo, pageReqVO)) {
                continue;
            }
            matched.add(vo);
        }

        matched.sort(Comparator.comparing(ActivityInstanceEnrollableRespVO::getId, Comparator.nullsLast(Long::compareTo))
                .reversed());
        return buildPage(matched, pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<ActivityInstanceEnrollmentRespVO> getEnrollmentList(Long instanceId) {
        ActivityInstanceDO instance = activityInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw exception(ACTIVITY_INSTANCE_NOT_EXISTS);
        }
        List<ActivityInstanceEnrollmentDO> list = activityInstanceEnrollmentMapper.selectListByInstanceId(instanceId);
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<Long> userIds = list.stream().map(ActivityInstanceEnrollmentDO::getUserId).filter(Objects::nonNull).toList();
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(userIds);
        List<ActivityInstanceEnrollmentRespVO> result = new ArrayList<>();
        for (ActivityInstanceEnrollmentDO item : list) {
            ActivityInstanceEnrollmentRespVO vo = BeanUtils.toBean(item, ActivityInstanceEnrollmentRespVO.class);
            AdminUserRespDTO user = userMap.get(item.getUserId());
            if (user != null) {
                vo.setUserName(formatUserName(user));
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<ActivityInstanceEnrollmentParticipantRespVO> getEnrollmentParticipantList(Long instanceId) {
        ActivityInstanceDO instance = activityInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw exception(ACTIVITY_INSTANCE_NOT_EXISTS);
        }
        List<ActivityParticipantDO> participants = activityParticipantMapper
                .selectListByActivityId(instance.getActivityId());
        if (CollUtil.isEmpty(participants)) {
            return Collections.emptyList();
        }
        List<Long> userIds = participants.stream()
                .map(ActivityParticipantDO::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(userIds);
        Map<Long, ActivityInstanceEnrollmentDO> enrollmentMap = activityInstanceEnrollmentMapper
                .selectListByInstanceId(instanceId).stream()
                .collect(Collectors.toMap(ActivityInstanceEnrollmentDO::getUserId, item -> item, (a, b) -> a));
        Set<Long> studentUserIds = resolveUserIdsByRoleCode(RoleCodeEnum.STUDENT.getCode());

        List<ActivityInstanceEnrollmentParticipantRespVO> result = new ArrayList<>();
        for (Long userId : userIds) {
            if (!studentUserIds.contains(userId)) {
                continue;
            }
            ActivityInstanceEnrollmentParticipantRespVO vo = new ActivityInstanceEnrollmentParticipantRespVO();
            vo.setUserId(userId);
            AdminUserRespDTO user = userMap.get(userId);
            if (user != null) {
                vo.setUserName(formatUserName(user));
            }
            ActivityInstanceEnrollmentDO enrollment = enrollmentMap.get(userId);
            vo.setRoleType("student");
            vo.setRoleLabel("学生");
            vo.setEnrolled(enrollment != null);
            vo.setEnrollTime(enrollment != null ? enrollment.getEnrollTime() : null);
            result.add(vo);
        }
        result.sort(Comparator.comparing(ActivityInstanceEnrollmentParticipantRespVO::getUserName,
                Comparator.nullsLast(String::compareTo)));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enroll(ActivityInstanceEnrollReqVO reqVO) {
        Long userId = requireLoginUserId();
        doEnroll(userId, reqVO.getInstanceId());
    }

    @Override
    public ActivityInstanceH5EnrollInfoRespVO getH5EnrollInfo(String token) {
        ActivityEnrollTokenHelper.Payload payload = activityEnrollTokenHelper.parseToken(token);
        java.util.concurrent.atomic.AtomicReference<ActivityInstanceH5EnrollInfoRespVO> holder =
                new java.util.concurrent.atomic.AtomicReference<>();
        TenantUtils.execute(payload.getT(), () -> holder.set(buildH5EnrollInfo(payload)));
        return holder.get();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enrollByToken(String token) {
        ActivityEnrollTokenHelper.Payload payload = activityEnrollTokenHelper.parseToken(token);
        TenantUtils.execute(payload.getT(), () -> doEnroll(payload.getU(), payload.getI()));
    }

    private ActivityInstanceH5EnrollInfoRespVO buildH5EnrollInfo(ActivityEnrollTokenHelper.Payload payload) {
        Long userId = payload.getU();
        Long instanceId = payload.getI();
        ActivityInstanceDO instance = activityInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw exception(ACTIVITY_INSTANCE_NOT_EXISTS);
        }
        ActivityDO activity = activityMapper.selectById(instance.getActivityId());
        if (activity == null) {
            throw exception(ACTIVITY_NOT_EXISTS);
        }
        fillRuntimeStatus(instance, activity);

        ActivityInstanceH5EnrollInfoRespVO vo = new ActivityInstanceH5EnrollInfoRespVO();
        vo.setTenantId(payload.getT());
        vo.setInstanceId(instance.getId());
        vo.setInstanceCode(instance.getInstanceCode());
        vo.setPeriodNo(instance.getPeriodNo());
        vo.setPlannedDate(instance.getPlannedDate());
        vo.setActivityName(activity.getName());
        vo.setContent(activity.getContent());
        vo.setActivityType(activity.getActivityType());
        vo.setEnrollStartTime(activity.getEnrollStartTime());
        vo.setEnrollEndTime(activity.getEnrollEndTime());
        vo.setEnrollCount(instance.getEnrollCount());
        vo.setEnrollLimit(instance.getEnrollLimit());

        AdminUserRespDTO user = adminUserApi.getUser(userId).getCheckedData();
        if (user != null) {
            vo.setUserName(formatUserName(user));
        }

        boolean enrolled = activityInstanceEnrollmentMapper.selectByInstanceIdAndUserId(instanceId, userId) != null;
        vo.setEnrolled(enrolled);
        boolean enrolling = ENROLLING.getStatus().equals(instance.getStatus());
        boolean inWindow = isInEnrollWindow(activity);
        boolean participant = isParticipant(userId, instance.getActivityId());
        boolean isStudent = Boolean.TRUE.equals(
                permissionApi.hasAnyRoles(userId, RoleCodeEnum.STUDENT.getCode()).getCheckedData());
        boolean full = isEnrollFull(instance);

        boolean canEnroll = enrolling && inWindow && participant && isStudent && !enrolled && !full;
        vo.setCanEnroll(canEnroll);
        if (enrolled) {
            vo.setMessage("您已报名成功");
        } else if (!isStudent) {
            vo.setMessage("仅学生可报名");
        } else if (!participant) {
            vo.setMessage("您不在本活动参与人范围内");
        } else if (!inWindow) {
            vo.setMessage("当前不在报名时间范围内");
        } else if (!enrolling) {
            vo.setMessage("当前实例状态不允许报名");
        } else if (full) {
            vo.setMessage("报名人数已满");
        } else {
            vo.setMessage("点击下方按钮完成报名");
        }
        return vo;
    }

    private void doEnroll(Long userId, Long instanceId) {
        validateStudentUser(userId);
        ActivityInstanceDO instance = activityInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw exception(ACTIVITY_INSTANCE_NOT_EXISTS);
        }
        ActivityDO activity = activityMapper.selectById(instance.getActivityId());
        if (activity == null) {
            throw exception(ACTIVITY_NOT_EXISTS);
        }
        validateParticipant(userId, instance.getActivityId());
        fillRuntimeStatus(instance, activity);
        validateEnrollWindow(activity);
        if (!ENROLLING.getStatus().equals(instance.getStatus())) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_STATUS_INVALID);
        }
        if (activityInstanceEnrollmentMapper.selectByInstanceIdAndUserId(instance.getId(), userId) != null) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_ALREADY);
        }
        if (isEnrollFull(instance)) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_LIMIT);
        }

        ActivityInstanceEnrollmentDO enrollment = ActivityInstanceEnrollmentDO.builder()
                .instanceId(instance.getId())
                .userId(userId)
                .enrollTime(LocalDateTime.now())
                .build();
        enrollment.setTenantId(instance.getTenantId());
        activityInstanceEnrollmentMapper.insert(enrollment);
        refreshEnrollCount(instance.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelEnroll(ActivityInstanceEnrollReqVO reqVO) {
        Long userId = requireLoginUserId();
        validateStudentUser(userId);
        ActivityInstanceDO instance = activityInstanceMapper.selectById(reqVO.getInstanceId());
        if (instance == null) {
            throw exception(ACTIVITY_INSTANCE_NOT_EXISTS);
        }
        ActivityDO activity = activityMapper.selectById(instance.getActivityId());
        if (activity == null) {
            throw exception(ACTIVITY_NOT_EXISTS);
        }
        fillRuntimeStatus(instance, activity);
        if (!ENROLLING.getStatus().equals(instance.getStatus())) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_CANCEL_INVALID);
        }
        ActivityInstanceEnrollmentDO enrollment = activityInstanceEnrollmentMapper
                .selectByInstanceIdAndUserId(instance.getId(), userId);
        if (enrollment == null) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_NOT_FOUND);
        }
        activityInstanceEnrollmentMapper.deleteById(enrollment.getId());
        refreshEnrollCount(instance.getId());
    }

    private void validateParticipant(Long userId, Long activityId) {
        if (!isParticipant(userId, activityId)) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_NOT_PARTICIPANT);
        }
    }

    private boolean isParticipant(Long userId, Long activityId) {
        List<ActivityParticipantDO> participants = activityParticipantMapper.selectListByActivityId(activityId);
        return participants.stream().anyMatch(item -> Objects.equals(item.getUserId(), userId));
    }

    /** 仅学生角色可自助报名 */
    private void validateStudentUser(Long userId) {
        Boolean isStudent = permissionApi.hasAnyRoles(userId, RoleCodeEnum.STUDENT.getCode()).getCheckedData();
        if (!Boolean.TRUE.equals(isStudent)) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_STUDENT_ONLY);
        }
    }

    private void validateEnrollWindow(ActivityDO activity) {
        if (!isInEnrollWindow(activity)) {
            throw exception(ACTIVITY_INSTANCE_ENROLL_NOT_OPEN);
        }
    }

    private boolean isInEnrollWindow(ActivityDO activity) {
        LocalDateTime now = LocalDateTime.now();
        return activity.getEnrollStartTime() != null && activity.getEnrollEndTime() != null
                && !now.isBefore(activity.getEnrollStartTime()) && !now.isAfter(activity.getEnrollEndTime());
    }

    private void fillMyFeedbackStatus(ActivityInstanceEnrollableRespVO vo) {
        if (Boolean.TRUE.equals(vo.getCanSubmitStudentFeedback())) {
            vo.setMyFeedbackStatus("pending");
        } else if (Boolean.TRUE.equals(vo.getStudentFeedbackSubmitted())) {
            vo.setMyFeedbackStatus("submitted");
        } else if (Boolean.TRUE.equals(vo.getEnrolled())) {
            vo.setMyFeedbackStatus("waiting");
        } else {
            vo.setMyFeedbackStatus("none");
        }
    }

    private Map<Long, Integer> buildFeedbackCountMap(Collection<Long> instanceIds) {
        if (CollUtil.isEmpty(instanceIds)) {
            return Collections.emptyMap();
        }
        return studentFeedbackMapper.selectListByInstanceIds(instanceIds).stream()
                .collect(Collectors.groupingBy(
                        ActivityInstanceStudentFeedbackDO::getInstanceId,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
    }

    private boolean matchMyPageFilter(ActivityInstanceEnrollableRespVO vo, ActivityInstanceMyPageReqVO req) {
        if (StringUtils.isNotBlank(req.getInstanceCode())
                && (vo.getInstanceCode() == null || !vo.getInstanceCode().contains(req.getInstanceCode().trim()))) {
            return false;
        }
        if (StringUtils.isNotBlank(req.getActivityName())
                && (vo.getActivityName() == null || !vo.getActivityName().contains(req.getActivityName().trim()))) {
            return false;
        }
        if (req.getEnrolled() != null && !req.getEnrolled().equals(vo.getEnrolled())) {
            return false;
        }
        if (req.getCanEnroll() != null && !req.getCanEnroll().equals(vo.getCanEnroll())) {
            return false;
        }
        if (StringUtils.isNotBlank(req.getMyFeedback())
                && !req.getMyFeedback().equals(vo.getMyFeedbackStatus())) {
            return false;
        }
        return true;
    }

    private boolean isEnrollFull(ActivityInstanceDO instance) {
        if (instance.getEnrollLimit() == null || instance.getEnrollLimit() <= 0) {
            return false;
        }
        Long count = activityInstanceEnrollmentMapper.selectCountByInstanceId(instance.getId());
        return count != null && count >= instance.getEnrollLimit();
    }

    private void refreshEnrollCount(Long instanceId) {
        Long count = activityInstanceEnrollmentMapper.selectCountByInstanceId(instanceId);
        ActivityInstanceDO update = new ActivityInstanceDO();
        update.setId(instanceId);
        update.setEnrollCount(count != null ? count.intValue() : 0);
        activityInstanceMapper.updateById(update);
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

    private void fillActivityBrief(ActivityInstanceEnrollableRespVO vo, ActivityDO activity) {
        vo.setActivityBillCode(activity.getBillCode());
        vo.setActivityName(activity.getName());
        vo.setActivityType(activity.getActivityType());
    }

    private String formatUserName(AdminUserRespDTO user) {
        if (user.getNickname() != null && user.getUsername() != null) {
            return user.getNickname() + "(" + user.getUsername() + ")";
        }
        return user.getNickname() != null ? user.getNickname() : user.getUsername();
    }

    private <T> PageResult<T> buildPage(List<T> list, int pageNo, int pageSize) {
        int total = list.size();
        int from = Math.max(0, (pageNo - 1) * pageSize);
        if (from >= total) {
            return new PageResult<>(Collections.emptyList(), (long) total);
        }
        int to = Math.min(total, from + pageSize);
        return new PageResult<>(list.subList(from, to), (long) total);
    }

}
