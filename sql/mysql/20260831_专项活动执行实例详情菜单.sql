-- ============================================================
-- 专项活动 - 活动实例详情（隐藏菜单）+ 更新权限
-- ============================================================

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5540, '活动实例详情', '', 2, 4, parent.id, 'instance/info', '',
       'edu/activity-instance/info/index', 'EduActivityInstanceInfo',
       0, b'0', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM `system_menu` parent
WHERE parent.`path` = '/edu/activity'
  AND parent.`parent_id` = 0
  AND parent.`deleted` = b'0'
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5540);

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5541, '活动实例更新', 'edu:activity-instance:update', 3, 2, 5538, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5538 AND `deleted` = b'0')
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5541);

INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, m.id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` m
WHERE m.id IN (5540, 5541)
  AND NOT EXISTS (SELECT 1 FROM `system_role_menu` rm WHERE rm.role_id = 1 AND rm.menu_id = m.id AND rm.deleted = b'0');
