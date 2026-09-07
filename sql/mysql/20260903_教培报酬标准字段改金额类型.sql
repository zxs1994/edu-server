-- ============================================================
-- 教培报酬/奖励标准：库字段改为金额类型（下拉仍用字典 value）
-- ============================================================

-- 先清空无法转数字的历史值
UPDATE `edu_teacher`
SET `reward_standard` = NULL
WHERE `reward_standard` IS NOT NULL
  AND CAST(`reward_standard` AS CHAR) NOT REGEXP '^[0-9]+(\\.[0-9]+)?$';

ALTER TABLE `edu_teacher`
  MODIFY COLUMN `reward_standard` decimal(12, 2) NULL DEFAULT NULL COMMENT '报酬/奖励标准（金额；选项来自字典 edu_teacher_reward）';
