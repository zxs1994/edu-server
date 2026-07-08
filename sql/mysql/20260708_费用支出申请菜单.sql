-- ============================================================
-- 费用支出申请 - 业务菜单 + 应用中心虚拟入口
-- 业务菜单挂 5321（差旅报销目录）
-- 应用中心虚拟菜单 5414（第 13 个流程入口，全员手动添加）
-- ============================================================

-- 应用中心可选入口（parent_id=0, visible=0, app_visible=1）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5414, '费用支出申请', '', 2, 13, 0,
       '/oa/expense-travel/expense-payment-info?from=startProcess&processDefinitionKey=oa_expense_payment_bill',
       'carbon:currency', NULL, NULL,
       0, b'0', b'1', b'1', b'1', b'0', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5414);

-- 列表页
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5420, '费用支出申请', '', 2, 4, 5321, 'expense-payment-list', 'carbon:currency',
       'oa/expense-payment/list/index', 'OaExpensePaymentBillList',
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5420);

-- 详情页（隐藏菜单）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5421, '费用支出详情', '', 2, 5, 5321, 'expense-payment-info', '',
       'oa/expense-payment/info/index', 'OaExpensePaymentBillInfo',
       0, b'0', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5421);

-- 按钮权限
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5422, '查询', 'oa:expense-payment-bill:query', 3, 1, 5420, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5422);
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5423, '新增/详情', 'oa:expense-payment-bill:create', 3, 2, 5420, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5423);
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5424, '修改', 'oa:expense-payment-bill:update', 3, 3, 5420, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5424);
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5425, '删除', 'oa:expense-payment-bill:delete', 3, 4, 5420, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5425);
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5426, '导出', 'oa:expense-payment-bill:export', 3, 5, 5420, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5426);
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5427, '提交', 'oa:expense-payment-bill:submit', 3, 6, 5420, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5427);

-- 超级管理员角色授权（role_id=1）
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, m.id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` m
WHERE m.id IN (5414, 5420, 5421, 5422, 5423, 5424, 5425, 5426, 5427)
  AND NOT EXISTS (SELECT 1 FROM `system_role_menu` rm WHERE rm.role_id = 1 AND rm.menu_id = m.id AND rm.deleted = b'0');

-- 应用中心最大显示数 12 -> 13
UPDATE `system_home_page_layout`
SET `config` = JSON_SET(COALESCE(NULLIF(`config`, ''), '{}'), '$.maxAppCount', 13)
WHERE `component_code` = 'workbench_app_center' AND `deleted` = b'0';

-- 已执行标记（执行后取消注释）
-- -- executed: 2026-07-08 费用支出申请菜单
