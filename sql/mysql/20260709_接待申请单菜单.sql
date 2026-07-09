-- ============================================================
-- 接待申请单 - 业务菜单 + 应用中心虚拟入口
-- 业务菜单挂 5430（接待管理目录）
-- 应用中心虚拟菜单 5415（第 14 个流程入口）
-- ============================================================

-- 接待管理目录
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5430, '接待管理', '', 1, 6, 5300, 'reception', 'ant-design:coffee-outlined', NULL, NULL,
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5430);

-- 应用中心可选入口（parent_id=0, visible=0, app_visible=1）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5415, '接待申请单', '', 2, 14, 0,
       '/oa/reception/reception-apply-info?from=startProcess&processDefinitionKey=oa_reception_apply_bill',
       'ant-design:coffee-outlined', NULL, NULL,
       0, b'0', b'1', b'1', b'1', b'0', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5415);

-- 列表页
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5431, '接待申请单', '', 2, 1, 5430, 'reception-apply-list', 'ant-design:coffee-outlined',
       'oa/reception/list/index', 'OaReceptionApplyBillList',
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5431);

-- 详情页（隐藏菜单）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5432, '接待申请详情', '', 2, 2, 5430, 'reception-apply-info', '',
       'oa/reception/info/index', 'OaReceptionApplyBillInfo',
       0, b'0', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5432);

-- 按钮权限
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5433, '查询', 'oa:reception-apply-bill:query', 3, 1, 5431, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5433);
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5434, '新增/详情', 'oa:reception-apply-bill:create', 3, 2, 5431, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5434);
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5435, '修改', 'oa:reception-apply-bill:update', 3, 3, 5431, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5435);
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5436, '删除', 'oa:reception-apply-bill:delete', 3, 4, 5431, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5436);
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5437, '导出', 'oa:reception-apply-bill:export', 3, 5, 5431, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5437);
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5438, '提交', 'oa:reception-apply-bill:submit', 3, 6, 5431, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5438);

-- 超级管理员角色授权（role_id=1）
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, m.id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` m
WHERE m.id IN (5415, 5430, 5431, 5432, 5433, 5434, 5435, 5436, 5437, 5438)
  AND NOT EXISTS (SELECT 1 FROM `system_role_menu` rm WHERE rm.role_id = 1 AND rm.menu_id = m.id AND rm.deleted = b'0');

-- 同步授权给费用支出申请已授权的全部角色（否则应用中心虚拟入口可点但路由 404）
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT DISTINCT ep.role_id, rm.menu_id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_role_menu` ep
CROSS JOIN (
    SELECT 5415 AS menu_id UNION SELECT 5430 UNION SELECT 5431 UNION SELECT 5432
    UNION SELECT 5433 UNION SELECT 5434 UNION SELECT 5435 UNION SELECT 5436 UNION SELECT 5437 UNION SELECT 5438
) rm
WHERE ep.menu_id IN (5414, 5420, 5421, 5422, 5423, 5424, 5425, 5426, 5427)
  AND ep.deleted = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `system_role_menu` x
    WHERE x.role_id = ep.role_id AND x.menu_id = rm.menu_id AND x.deleted = b'0'
  );

-- 应用中心最大显示数 13 -> 14
UPDATE `system_home_page_layout`
SET `config` = JSON_SET(COALESCE(NULLIF(`config`, ''), '{}'), '$.maxAppCount', 14)
WHERE `component_code` = 'workbench_app_center' AND `deleted` = b'0';

-- 已执行标记（执行后取消注释）
-- -- executed: 2026-07-09 接待申请单菜单
