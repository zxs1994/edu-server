package cn.dh.edu.module.edu.util;

import cn.dh.edu.module.edu.enums.ActivityInstanceStatusEnum;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * 专项活动执行实例状态工具：待报名/报名中/待执行/执行中按时间计算，不落库。
 */
public final class ActivityInstanceStatusUtils {

    private ActivityInstanceStatusUtils() {
    }

    /**
     * 是否为需要入库的终态/业务态
     */
    public static boolean isPersistedStatus(String status) {
        return ActivityInstanceStatusEnum.PENDING_FEEDBACK.getStatus().equals(status)
                || ActivityInstanceStatusEnum.COMPLETED.getStatus().equals(status)
                || ActivityInstanceStatusEnum.CANCELLED.getStatus().equals(status);
    }

    /**
     * 是否为按时间计算的运行时状态
     */
    public static boolean isRuntimeStatus(String status) {
        return ActivityInstanceStatusEnum.PENDING_ENROLL.getStatus().equals(status)
                || ActivityInstanceStatusEnum.ENROLLING.getStatus().equals(status)
                || ActivityInstanceStatusEnum.PENDING_EXEC.getStatus().equals(status)
                || ActivityInstanceStatusEnum.EXECUTING.getStatus().equals(status);
    }

    /**
     * 解析实例有效状态：已入库状态直接返回，否则按时间计算
     */
    public static String resolveStatus(String storedStatus, LocalDateTime plannedDate,
                                       LocalDateTime enrollStartTime, LocalDateTime enrollEndTime) {
        if (isPersistedStatus(storedStatus)) {
            return storedStatus;
        }
        return computeRuntimeStatus(plannedDate, enrollStartTime, enrollEndTime);
    }

    /**
     * 按报名时间 + 计划执行时间计算运行时状态
     */
    public static String computeRuntimeStatus(LocalDateTime plannedDate,
                                              LocalDateTime enrollStartTime, LocalDateTime enrollEndTime) {
        LocalDateTime now = LocalDateTime.now();
        if (enrollStartTime != null && enrollEndTime != null) {
            if (now.isBefore(enrollStartTime)) {
                return ActivityInstanceStatusEnum.PENDING_ENROLL.getStatus();
            }
            if (!now.isAfter(enrollEndTime)) {
                return ActivityInstanceStatusEnum.ENROLLING.getStatus();
            }
        }
        return resolveExecutionPhaseStatus(plannedDate);
    }

    private static String resolveExecutionPhaseStatus(LocalDateTime plannedDate) {
        if (plannedDate == null) {
            return ActivityInstanceStatusEnum.PENDING_EXEC.getStatus();
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(plannedDate)) {
            return ActivityInstanceStatusEnum.PENDING_EXEC.getStatus();
        }
        return ActivityInstanceStatusEnum.EXECUTING.getStatus();
    }

    /**
     * 活动记录是否处于可编辑状态（运行时四态；具体角色权限在 Service 层校验）
     */
    public static boolean isRecordEditable(String status) {
        return StringUtils.isNotBlank(status) && isRuntimeStatus(status);
    }

    /**
     * 活动反馈是否可提交（已结项；兼容历史「待反馈」）
     */
    public static boolean isFeedbackEditable(String status) {
        return ActivityInstanceStatusEnum.COMPLETED.getStatus().equals(status)
                || ActivityInstanceStatusEnum.PENDING_FEEDBACK.getStatus().equals(status);
    }

}
