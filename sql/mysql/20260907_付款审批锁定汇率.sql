-- ============================================================
-- 付款申请：审批通过时锁定汇率与折合人民币金额
-- ============================================================

-- 1) exchange_rate
SET @__col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'edu_activity_payment_request'
    AND COLUMN_NAME = 'exchange_rate'
);
SET @__sql := IF(@__col_exists = 0,
  'ALTER TABLE `edu_activity_payment_request` ADD COLUMN `exchange_rate` decimal(18, 8) NULL DEFAULT NULL COMMENT ''审批通过时锁定汇率（1 外币 = X 人民币）'' AFTER `approve_time`',
  'SELECT 1');
PREPARE __stmt FROM @__sql;
EXECUTE __stmt;
DEALLOCATE PREPARE __stmt;

-- 2) amount_cny
SET @__col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'edu_activity_payment_request'
    AND COLUMN_NAME = 'amount_cny'
);
SET @__sql := IF(@__col_exists = 0,
  'ALTER TABLE `edu_activity_payment_request` ADD COLUMN `amount_cny` decimal(18, 2) NULL DEFAULT NULL COMMENT ''审批通过时锁定折合人民币金额'' AFTER `exchange_rate`',
  'SELECT 1');
PREPARE __stmt FROM @__sql;
EXECUTE __stmt;
DEALLOCATE PREPARE __stmt;

-- 3) 历史已通过 + 人民币：直接锁定（外币由应用首次汇总时按当时汇率补锁）
UPDATE `edu_activity_payment_request`
SET `exchange_rate` = 1.00000000,
    `amount_cny` = `total_amount`
WHERE `deleted` = b'0'
  AND `process_status` = 2
  AND `amount_cny` IS NULL
  AND (`currency` = 'CNY' OR `currency` IS NULL OR `currency` = '');
