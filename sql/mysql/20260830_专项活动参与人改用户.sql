-- ============================================================
-- 专项活动参与人：档案 ID 改为 system_users.id
-- （若已按旧版 20260829 建表且含 participant_type/participant_id）
-- ============================================================

ALTER TABLE `edu_activity_participant`
  ADD COLUMN `user_id` bigint NULL COMMENT '参与人用户ID（system_users.id）' AFTER `activity_id`;

UPDATE `edu_activity_participant` p
  INNER JOIN `edu_student` s ON p.`participant_type` = 'STUDENT' AND p.`participant_id` = s.`id` AND s.`deleted` = b'0'
SET p.`user_id` = s.`user_id`
WHERE p.`deleted` = b'0' AND s.`user_id` IS NOT NULL;

UPDATE `edu_activity_participant` p
  INNER JOIN `edu_teacher` t ON p.`participant_type` = 'TEACHER' AND p.`participant_id` = t.`id` AND t.`deleted` = b'0'
SET p.`user_id` = t.`user_id`
WHERE p.`deleted` = b'0' AND t.`user_id` IS NOT NULL AND p.`user_id` IS NULL;

DELETE FROM `edu_activity_participant` WHERE `user_id` IS NULL;

ALTER TABLE `edu_activity_participant`
  DROP INDEX `idx_participant`,
  DROP COLUMN `participant_type`,
  DROP COLUMN `participant_id`,
  MODIFY COLUMN `user_id` bigint NOT NULL COMMENT '参与人用户ID（system_users.id）',
  ADD INDEX `idx_user_id`(`user_id` ASC) USING BTREE;
