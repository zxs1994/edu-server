-- ============================================================
-- 删除已废弃的奖金池壳表，年度预算不再依赖 pool_id
-- 依赖：已执行 20260907_奖金池年度时段预算.sql
-- ============================================================

-- 1) 年度预算去掉 pool_id
SET @__idx_exists := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'edu_reward_budget_year'
    AND INDEX_NAME = 'uk_pool_year'
);
SET @__sql := IF(@__idx_exists > 0,
  'ALTER TABLE `edu_reward_budget_year` DROP INDEX `uk_pool_year`',
  'SELECT 1');
PREPARE __stmt FROM @__sql;
EXECUTE __stmt;
DEALLOCATE PREPARE __stmt;

SET @__idx_exists := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'edu_reward_budget_year'
    AND INDEX_NAME = 'idx_pool_id'
);
SET @__sql := IF(@__idx_exists > 0,
  'ALTER TABLE `edu_reward_budget_year` DROP INDEX `idx_pool_id`',
  'SELECT 1');
PREPARE __stmt FROM @__sql;
EXECUTE __stmt;
DEALLOCATE PREPARE __stmt;

SET @__col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'edu_reward_budget_year'
    AND COLUMN_NAME = 'pool_id'
);
SET @__sql := IF(@__col_exists > 0,
  'ALTER TABLE `edu_reward_budget_year` DROP COLUMN `pool_id`',
  'SELECT 1');
PREPARE __stmt FROM @__sql;
EXECUTE __stmt;
DEALLOCATE PREPARE __stmt;

SET @__idx_exists := (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'edu_reward_budget_year'
    AND INDEX_NAME = 'uk_budget_year'
);
SET @__sql := IF(@__idx_exists = 0,
  'ALTER TABLE `edu_reward_budget_year` ADD UNIQUE INDEX `uk_budget_year`(`tenant_id` ASC, `budget_year` ASC, `deleted` ASC)',
  'SELECT 1');
PREPARE __stmt FROM @__sql;
EXECUTE __stmt;
DEALLOCATE PREPARE __stmt;

-- 2) 删除奖金池壳表与流水表
DROP TABLE IF EXISTS `edu_reward_pool_txn`;
DROP TABLE IF EXISTS `edu_reward_pool`;

-- 3) 清理已无用的池状态/流水字典（可选，幂等）
UPDATE `system_dict_data` SET `deleted` = b'1', `update_time` = NOW()
WHERE `dict_type` IN ('edu_reward_pool_status', 'edu_reward_pool_txn_type') AND `deleted` = b'0';

UPDATE `system_dict_type` SET `deleted` = b'1', `update_time` = NOW(), `deleted_time` = NOW()
WHERE `type` IN ('edu_reward_pool_status', 'edu_reward_pool_txn_type') AND `deleted` = b'0';
