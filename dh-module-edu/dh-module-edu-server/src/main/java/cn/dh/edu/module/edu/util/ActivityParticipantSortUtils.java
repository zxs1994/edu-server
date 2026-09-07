package cn.dh.edu.module.edu.util;

import cn.hutool.core.collection.CollUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 活动参与人排序：教培在前，学生在后
 */
public final class ActivityParticipantSortUtils {

    private ActivityParticipantSortUtils() {
    }

    public static List<Long> sortTeachersFirst(List<Long> userIds,
                                                 Set<Long> teacherUserIds,
                                                 Set<Long> studentUserIds) {
        if (CollUtil.isEmpty(userIds)) {
            return userIds;
        }
        return userIds.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparingInt((Long userId) -> resolveRoleOrder(userId, teacherUserIds, studentUserIds))
                        .thenComparingLong(Long::longValue))
                .toList();
    }

    private static int resolveRoleOrder(Long userId, Set<Long> teacherUserIds, Set<Long> studentUserIds) {
        if (teacherUserIds != null && teacherUserIds.contains(userId)) {
            return 0;
        }
        if (studentUserIds != null && studentUserIds.contains(userId)) {
            return 1;
        }
        return 2;
    }

}
