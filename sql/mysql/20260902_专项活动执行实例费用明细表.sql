-- ============================================================
-- 专项活动 - 执行实例费用明细（结项后自动生成）
-- ============================================================

CREATE TABLE IF NOT EXISTS `edu_activity_instance_fee_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `fee_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '明细编号 FEE-{YYYYMMDD}-{序号}',
  `instance_id` bigint NOT NULL COMMENT '执行实例ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `fee_standard_id` bigint NOT NULL COMMENT '费用标准ID',
  `fee_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用类型（字典 edu_fee_type）',
  `fee_mode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计费模式（字典 edu_fee_mode）',
  `currency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '币种（字典 edu_fee_currency）',
  `unit_price` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `quantity` int NOT NULL DEFAULT 0 COMMENT '数量',
  `amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '预算金额（单价×数量）',
  `actual_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '实际金额（付款申请填写）',
  `fee_side` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用侧（字典 edu_fee_side）',
  `payee_user_id` bigint NULL DEFAULT NULL COMMENT '收款人用户ID',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态（字典 edu_fee_item_status）',
  `payment_request_id` bigint NULL DEFAULT NULL COMMENT '关联付款申请ID',
  `generate_time` datetime NOT NULL COMMENT '生成时间',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '付款时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '费用项说明',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_instance_id`(`instance_id` ASC) USING BTREE,
  INDEX `idx_fee_code`(`fee_code` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动执行实例-费用明细';

-- 费用明细状态字典（type=2228；勿用 2226/22260-22263，已被 edu_fee_currency 占用）
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2228, '专项活动费用明细状态', 'edu_fee_item_status', 0, '专项活动-实例费用明细状态', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'edu_fee_item_status');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22280, 1, '待付款申请', 'pending_request', 'edu_fee_item_status', 0, 'warning', '', '教师侧费用，待批量付款申请', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'pending_request');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22281, 2, '已提交付款申请', 'submitted', 'edu_fee_item_status', 0, 'processing', '', '已创建付款申请', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'submitted');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22282, 3, '已入账（积分）', 'credited', 'edu_fee_item_status', 0, 'cyan', '', '学生侧结项后待积分入账', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'credited');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22283, 4, '已付款', 'paid', 'edu_fee_item_status', 0, 'success', '', '教师侧付款完成或学生积分已确认', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'paid');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22284, 5, '已驳回', 'rejected', 'edu_fee_item_status', 0, 'error', '', '付款申请被驳回', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'rejected');
