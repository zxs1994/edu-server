-- ============================================================
-- 专项活动 - 执行实例表 + 状态字典 + 菜单权限
-- ============================================================

CREATE TABLE IF NOT EXISTS `edu_activity_instance` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `instance_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实例编号（INST-{活动单号}-{期次}）',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `period_no` int NOT NULL DEFAULT 1 COMMENT '期次编号',
  `planned_date` datetime NOT NULL COMMENT '计划执行时间',
  `actual_date` datetime NULL DEFAULT NULL COMMENT '实际执行时间',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态（待反馈/已结项/已取消入库；其余按时间计算）',
  `enroll_count` int NOT NULL DEFAULT 0 COMMENT '报名人数',
  `enroll_limit` int NULL DEFAULT NULL COMMENT '报名上限',
  `attendance_count` int NOT NULL DEFAULT 0 COMMENT '出勤人数',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_instance_code`(`instance_code` ASC, `deleted` ASC, `tenant_id` ASC) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_planned_date`(`planned_date` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动执行实例';

-- 字典：实例状态（id 使用 2227 / 22270+，避免与费用类型 2225/22250 冲突）
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

-- 活动实例列表菜单（parent 动态关联「专项活动」顶级目录）
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

INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, m.id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` m
WHERE m.id IN (5538, 5539)
  AND NOT EXISTS (SELECT 1 FROM `system_role_menu` rm WHERE rm.role_id = 1 AND rm.menu_id = m.id AND rm.deleted = b'0');
