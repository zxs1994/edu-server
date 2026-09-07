-- 专项活动执行实例：status 仅入库待反馈/已结项/已取消，运行时四态不落库
ALTER TABLE `edu_activity_instance`
  MODIFY COLUMN `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL
  COMMENT '状态（待反馈/已结项/已取消入库；待报名/报名中/待执行/执行中按时间计算）';

-- 历史数据：清除已入库的运行时状态
UPDATE `edu_activity_instance`
SET `status` = NULL
WHERE `status` IN ('PENDING_ENROLL', 'ENROLLING', 'PENDING_EXEC', 'EXECUTING')
  AND `deleted` = b'0';
