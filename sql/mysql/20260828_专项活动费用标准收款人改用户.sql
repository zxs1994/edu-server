-- 费用标准收款人：由教培档案改为 system_users
ALTER TABLE `edu_activity_fee_standard`
  CHANGE COLUMN `payee_teacher_id` `payee_user_id` bigint NULL DEFAULT NULL COMMENT '收款人用户ID';

-- 历史数据：教培档案 ID 转为对应用户 ID
UPDATE `edu_activity_fee_standard` f
INNER JOIN `edu_teacher` t ON f.`payee_user_id` = t.`id`
SET f.`payee_user_id` = t.`user_id`
WHERE f.`payee_user_id` IS NOT NULL AND t.`user_id` IS NOT NULL;
