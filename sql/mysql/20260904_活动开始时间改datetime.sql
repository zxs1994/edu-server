-- 专项活动：开始日期 / 实例计划与实际执行日期 → 含时分秒
ALTER TABLE `edu_activity`
    MODIFY COLUMN `start_date` datetime NULL DEFAULT NULL COMMENT '活动开始时间';

ALTER TABLE `edu_activity_instance`
    MODIFY COLUMN `planned_date` datetime NOT NULL COMMENT '计划执行时间',
    MODIFY COLUMN `actual_date` datetime NULL DEFAULT NULL COMMENT '实际执行时间';
