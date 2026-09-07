-- ============================================================
-- 专项活动 - 活动实例菜单补丁（可单独执行）
-- 原因：20260831 原脚本字典 id 2225/22250 与费用类型冲突，导致后续菜单未插入
-- ============================================================

-- 活动实例列表菜单
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5538, '活动实例', '', 2, 3, parent.id, 'instance/list', 'lucide:calendar-check',
       'edu/activity-instance/list/index', 'EduActivityInstanceList',
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM `system_menu` parent
WHERE parent.`path` = '/edu/activity'
  AND parent.`parent_id` = 0
  AND parent.`deleted` = b'0'
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5538);

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5539, '活动实例查询', 'edu:activity-instance:query', 3, 1, 5538, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5538 AND `deleted` = b'0')
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5539);

-- 超级管理员角色授权（role_id=1）
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, m.id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` m
WHERE m.id IN (5538, 5539)
  AND NOT EXISTS (SELECT 1 FROM `system_role_menu` rm WHERE rm.role_id = 1 AND rm.menu_id = m.id AND rm.deleted = b'0');

-- 实例状态字典补丁（若主脚本字典段未成功执行）
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2227, '专项活动实例状态', 'edu_activity_instance_status', 0, '专项活动-活动实例状态', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'edu_activity_instance_status');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22270, 1, '待报名', 'PENDING_ENROLL', 'edu_activity_instance_status', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_activity_instance_status' AND `value` = 'PENDING_ENROLL');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22271, 2, '报名中', 'ENROLLING', 'edu_activity_instance_status', 0, 'processing', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_activity_instance_status' AND `value` = 'ENROLLING');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22272, 3, '待执行', 'PENDING_EXEC', 'edu_activity_instance_status', 0, 'warning', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_activity_instance_status' AND `value` = 'PENDING_EXEC');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22273, 4, '执行中', 'EXECUTING', 'edu_activity_instance_status', 0, 'primary', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_activity_instance_status' AND `value` = 'EXECUTING');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22274, 5, '待反馈', 'PENDING_FEEDBACK', 'edu_activity_instance_status', 0, 'cyan', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_activity_instance_status' AND `value` = 'PENDING_FEEDBACK');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22275, 6, '已结项', 'COMPLETED', 'edu_activity_instance_status', 0, 'success', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_activity_instance_status' AND `value` = 'COMPLETED');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22276, 7, '已取消', 'CANCELLED', 'edu_activity_instance_status', 0, 'error', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_activity_instance_status' AND `value` = 'CANCELLED');
