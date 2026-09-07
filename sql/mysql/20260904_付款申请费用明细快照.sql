-- ============================================================
-- 付款申请：费用明细 ID 快照
-- 驳回后明细可再挂到新单；历史单靠 fee_item_ids 回显，避免丢明细
-- ============================================================

SET @__col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'edu_activity_payment_request'
    AND COLUMN_NAME = 'fee_item_ids'
);
SET @__sql := IF(@__col_exists = 0,
  'ALTER TABLE `edu_activity_payment_request`
     ADD COLUMN `fee_item_ids` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL
     COMMENT ''费用明细ID快照 JSON，如 [1,2,3]'' AFTER `remark`',
  'SELECT ''fee_item_ids already exists''');
PREPARE stmt FROM @__sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 回填：仍挂在本单上的明细
UPDATE `edu_activity_payment_request` r
INNER JOIN (
  SELECT `payment_request_id` AS rid,
         CONCAT('[', GROUP_CONCAT(`id` ORDER BY `id`), ']') AS ids
  FROM `edu_activity_instance_fee_item`
  WHERE `deleted` = b'0'
    AND `payment_request_id` IS NOT NULL
  GROUP BY `payment_request_id`
) t ON r.`id` = t.rid
SET r.`fee_item_ids` = t.ids
WHERE r.`deleted` = b'0'
  AND (r.`fee_item_ids` IS NULL OR r.`fee_item_ids` = '' OR r.`fee_item_ids` = 'null');
-- 已驳回/已取消单据：解绑明细并回到待申请（不允许勾选 rejected；历史回显靠 fee_item_ids）
UPDATE `edu_activity_instance_fee_item` f
INNER JOIN `edu_activity_payment_request` r
        ON f.`payment_request_id` = r.`id` AND r.`deleted` = b'0'
SET f.`payment_request_id` = NULL,
    f.`status` = 'pending_request'
WHERE f.`deleted` = b'0'
  AND r.`process_status` IN (3, 4)
  AND f.`status` IN ('rejected', 'submitted', 'pending_request');

