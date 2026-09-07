-- ============================================================
-- 专项活动付款申请表 + 菜单
-- BPM Create 路径：/edu/activity/payment/info
-- BPM View 组件：edu/activity-payment/info/index
-- 流程定义 Key：edu_activity_payment_request（必须以此 Key 部署，否则监听器无法路由）
--
-- 【流程部署步骤】
-- 1. 执行本 SQL（建表 + 菜单）
-- 2. 管理后台 → 流程管理 → 流程模型：新建模型
--    - 标识/Key = edu_activity_payment_request
--    - 名称 = 专项活动付款申请
--    - 表单类型 = 业务表单
--    - 提交路由 = /edu/activity/payment/info
--    - 查看组件 = edu/activity-payment/info/index
-- 3. 设计简单审批流（发起人 → 审批人 → 结束），发布并部署
-- 4. 重新登录刷新菜单权限后联调：草稿保存 / 提交 / 通过 / 驳回
-- ============================================================

CREATE TABLE IF NOT EXISTS `edu_activity_payment_request` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `bill_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单据编号 PAY-{YYYY}-{流水}',
  `activity_id` bigint NOT NULL COMMENT '专项活动ID',
  `instance_id` bigint NOT NULL COMMENT '活动实例ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '申请标题/事由',
  `total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '合计金额',
  `currency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '币种（字典 edu_fee_currency）',
  `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '流程实例编号',
  `process_status` tinyint NOT NULL DEFAULT -1 COMMENT '流程状态（-1未提交 1审批中 2通过 3拒绝 4取消）',
  `applicant_user_id` bigint NULL DEFAULT NULL COMMENT '申请人用户ID',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_bill_code`(`bill_code` ASC, `deleted` ASC, `tenant_id` ASC) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE,
  INDEX `idx_instance_id`(`instance_id` ASC) USING BTREE,
  INDEX `idx_process_status`(`process_status` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动付款申请';

-- 已执行过旧版建表（无 instance_id）时自动补列
SET @__col_exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'edu_activity_payment_request'
    AND COLUMN_NAME = 'instance_id'
);
SET @__sql := IF(@__col_exists = 0,
  'ALTER TABLE `edu_activity_payment_request` ADD COLUMN `instance_id` bigint NULL COMMENT ''活动实例ID'' AFTER `activity_id`, ADD INDEX `idx_instance_id`(`instance_id` ASC)',
  'SELECT 1');
PREPARE __stmt FROM @__sql;
EXECUTE __stmt;
DEALLOCATE PREPARE __stmt;

-- 学生侧费用不再走积分：历史「已入账（积分）」改为「待付款申请」（未挂付款单的）
UPDATE `edu_activity_instance_fee_item`
SET `status` = 'pending_request', `update_time` = NOW()
WHERE `deleted` = b'0'
  AND `fee_side` = 'student'
  AND `status` = 'credited'
  AND (`payment_request_id` IS NULL OR `payment_request_id` = 0);

UPDATE `system_dict_data`
SET `remark` = '教培/学生侧费用，待批量付款申请',
    `update_time` = NOW()
WHERE `dict_type` = 'edu_fee_item_status'
  AND `value` = 'pending_request'
  AND `deleted` = b'0';

UPDATE `system_dict_data`
SET `remark` = '历史状态：学生侧曾用积分入账，现与教培侧统一走付款申请',
    `update_time` = NOW()
WHERE `dict_type` = 'edu_fee_item_status'
  AND `value` = 'credited'
  AND `deleted` = b'0';

UPDATE `system_dict_data`
SET `remark` = '付款申请审批通过后（教培/学生侧）',
    `update_time` = NOW()
WHERE `dict_type` = 'edu_fee_item_status'
  AND `value` = 'paid'
  AND `deleted` = b'0';

-- 付款申请列表
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5553, '付款申请', '', 2, 6, 5530, 'payment/list', 'lucide:wallet',
       'edu/activity-payment/list/index', 'EduActivityPaymentList',
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5530 AND `deleted` = b'0')
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5553);

-- 付款申请详情（隐藏；BPM 自定义表单）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5554, '付款申请详情', '', 2, 7, 5530, 'payment/info', '',
       'edu/activity-payment/info/index', 'EduActivityPaymentInfo',
       0, b'0', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5530 AND `deleted` = b'0')
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5554);

-- 按钮权限
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5555, '付款申请查询', 'edu:activity-payment:query', 3, 1, 5553, '', '#', NULL, NULL,
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5555);

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5556, '付款申请创建', 'edu:activity-payment:create', 3, 2, 5553, '', '#', NULL, NULL,
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5556);

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5557, '付款申请更新', 'edu:activity-payment:update', 3, 3, 5553, '', '#', NULL, NULL,
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5557);

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5558, '付款申请删除', 'edu:activity-payment:delete', 3, 4, 5553, '', '#', NULL, NULL,
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5558);

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5559, '付款申请提交', 'edu:activity-payment:submit', 3, 5, 5553, '', '#', NULL, NULL,
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5559);

-- 超级管理员授权
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, m.id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` m
WHERE m.id IN (5553, 5554, 5555, 5556, 5557, 5558, 5559)
  AND NOT EXISTS (SELECT 1 FROM `system_role_menu` rm WHERE rm.role_id = 1 AND rm.menu_id = m.id AND rm.deleted = b'0');
