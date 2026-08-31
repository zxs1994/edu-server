-- ============================================================
-- 首页「信息总览」权限菜单（组件由默认首页代码固定插入，不走设计器）
-- ============================================================

-- 权限：信息总览（按钮级，挂在基础信息目录下）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5520, '信息总览', 'edu:dashboard:overview', 3, 0, 5500, '', '#', NULL, NULL,
       0, b'1', b'1', b'1', b'0', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5520);

-- 超级管理员授权
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, 5520, '1', NOW(), '1', NOW(), b'0', 1
FROM DUAL WHERE NOT EXISTS (
  SELECT 1 FROM `system_role_menu` WHERE role_id = 1 AND menu_id = 5520 AND deleted = b'0'
);
