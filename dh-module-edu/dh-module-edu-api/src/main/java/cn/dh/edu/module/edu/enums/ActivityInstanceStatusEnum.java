package cn.dh.edu.module.edu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 专项活动执行实例状态
 *
 * @author 鼎衡
 */
@Getter
@AllArgsConstructor
public enum ActivityInstanceStatusEnum {

    PENDING_ENROLL("PENDING_ENROLL", "待报名"),
    ENROLLING("ENROLLING", "报名中"),
    PENDING_EXEC("PENDING_EXEC", "待执行"),
    EXECUTING("EXECUTING", "执行中"),
    PENDING_FEEDBACK("PENDING_FEEDBACK", "待反馈"),
    COMPLETED("COMPLETED", "已结项"),
    CANCELLED("CANCELLED", "已取消");

    private final String status;
    private final String name;

    public static boolean isPersisted(String status) {
        return PENDING_FEEDBACK.status.equals(status)
                || COMPLETED.status.equals(status)
                || CANCELLED.status.equals(status);
    }

}
