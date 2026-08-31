-- ============================================================
-- 专项活动负责人：teacher_id 改为 user_id（system_users）
-- ============================================================

ALTER TABLE `edu_activity_owner`
  ADD COLUMN `user_id` bigint NULL COMMENT '负责人用户ID（system_users.id）' AFTER `activity_id`;

UPDATE `edu_activity_owner` o
  INNER JOIN `edu_teacher` t ON o.`teacher_id` = t.`id` AND t.`deleted` = b'0'
SET o.`user_id` = t.`user_id`
WHERE o.`deleted` = b'0' AND t.`user_id` IS NOT NULL;

DELETE FROM `edu_activity_owner` WHERE `user_id` IS NULL;

ALTER TABLE `edu_activity_owner`
  DROP INDEX `idx_teacher_id`,
  DROP COLUMN `teacher_id`,
  MODIFY COLUMN `user_id` bigint NOT NULL COMMENT '负责人用户ID（system_users.id）',
  ADD INDEX `idx_user_id`(`user_id` ASC) USING BTREE;
