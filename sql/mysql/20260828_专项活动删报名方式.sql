-- ============================================================
-- 专项活动：移除报名方式字段及字典
-- ============================================================

ALTER TABLE `edu_activity`
  DROP COLUMN `enroll_mode`;

DELETE FROM `system_dict_data` WHERE `dict_type` = 'edu_activity_enroll_mode';
DELETE FROM `system_dict_type` WHERE `type` = 'edu_activity_enroll_mode';
