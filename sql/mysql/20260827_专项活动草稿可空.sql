-- ============================================================
-- 专项活动：草稿可空字段（保存草稿时允许未填完）
-- 提交时仍由后端 validateForSubmit 严格校验
-- ============================================================

ALTER TABLE `edu_activity`
  MODIFY COLUMN `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动名称',
  MODIFY COLUMN `activity_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动类型（字典 edu_activity_type）',
  MODIFY COLUMN `start_date` date NULL DEFAULT NULL COMMENT '开始日期';

ALTER TABLE `edu_activity_fee_standard`
  MODIFY COLUMN `fee_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '费用项名称',
  MODIFY COLUMN `fee_mode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '计费模式（字典 edu_fee_mode）',
  MODIFY COLUMN `fee_side` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '费用侧（字典 edu_fee_side）';
