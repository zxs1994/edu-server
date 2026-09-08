-- ============================================================
-- 预算配置：年度时段预算 + 付款申请审批通过时间
-- ============================================================

-- 1) 年度预算表
CREATE TABLE IF NOT EXISTS `edu_reward_budget_year` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `budget_year` int NOT NULL COMMENT '预算年度，如 2026',
  `period_mode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '时段模式：QUARTER/MONTH/CUSTOM',
  `total_budget` decimal(14, 2) NOT NULL DEFAULT 0.00 COMMENT '年度预算合计（由时段汇总）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_budget_year`(`tenant_id` ASC, `budget_year` ASC, `deleted` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动年度预算';

-- 2) 时段预算表
CREATE TABLE IF NOT EXISTS `edu_reward_budget_period` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `year_id` bigint NOT NULL COMMENT '年度预算ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '时段名称',
  `period_no` int NOT NULL DEFAULT 0 COMMENT '排序号',
  `start_date` date NOT NULL COMMENT '开始日期（含）',
  `end_date` date NOT NULL COMMENT '结束日期（含）',
  `budget_amount` decimal(14, 2) NOT NULL DEFAULT 0.00 COMMENT '时段预算金额',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_year_id`(`year_id` ASC) USING BTREE,
  INDEX `idx_date_range`(`start_date` ASC, `end_date` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预算配置年度时段预算';

-- 3) 付款申请：审批通过时间
SET @__col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'edu_activity_payment_request'
    AND COLUMN_NAME = 'approve_time'
);
SET @__sql := IF(@__col_exists = 0,
  'ALTER TABLE `edu_activity_payment_request` ADD COLUMN `approve_time` datetime NULL DEFAULT NULL COMMENT ''审批通过时间'' AFTER `process_status`, ADD INDEX `idx_approve_time`(`approve_time` ASC)',
  'SELECT 1');
PREPARE __stmt FROM @__sql;
EXECUTE __stmt;
DEALLOCATE PREPARE __stmt;

-- 历史已通过单据回填（无精确通过时间时用 update_time 兜底）
UPDATE `edu_activity_payment_request`
SET `approve_time` = `update_time`
WHERE `deleted` = b'0'
  AND `process_status` = 2
  AND `approve_time` IS NULL;

-- 4) 字典：时段模式
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2242, '预算配置时段模式', 'edu_reward_budget_period_mode', 0, '专项活动预算配置年度时段切分方式', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'edu_reward_budget_period_mode');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22420, 1, '按季度', 'QUARTER', 'edu_reward_budget_period_mode', 0, 'primary', '', '一年 4 段', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_reward_budget_period_mode' AND `value` = 'QUARTER');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22421, 2, '按月', 'MONTH', 'edu_reward_budget_period_mode', 0, 'success', '', '一年 12 段', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_reward_budget_period_mode' AND `value` = 'MONTH');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22422, 3, '自定义', 'CUSTOM', 'edu_reward_budget_period_mode', 0, 'warning', '', '自定义起止日期多段', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_reward_budget_period_mode' AND `value` = 'CUSTOM');
