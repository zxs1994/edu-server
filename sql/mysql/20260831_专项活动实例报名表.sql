-- ============================================================
-- 专项活动 - 执行实例报名表 + 菜单权限
-- ============================================================

CREATE TABLE IF NOT EXISTS `edu_activity_instance_enrollment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `instance_id` bigint NOT NULL COMMENT '执行实例ID',
  `user_id` bigint NOT NULL COMMENT '报名用户ID（system_users.id）',
  `enroll_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_instance_user`(`instance_id` ASC, `user_id` ASC, `deleted` ASC, `tenant_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动执行实例-报名记录';

-- 我的活动报名（仅学生可见）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5542, '我的活动报名', 'edu:activity-instance:enroll', 2, 4, parent.id, 'instance/enroll', 'lucide:user-check',
       'edu/activity-instance/enroll/index', 'EduActivityInstanceEnroll',
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM `system_menu` parent
WHERE parent.`path` = '/edu/activity'
  AND parent.`parent_id` = 0
  AND parent.`deleted` = b'0'
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5542);

-- 报名管理（负责人查看报名列表，复用 query 权限）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5543, '活动实例报名', 'edu:activity-instance:enroll-manage', 3, 3, 5538, '', '#', NULL, NULL,
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5538 AND `deleted` = b'0')
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5543);

-- super_admin：仅分配「活动实例报名」按钮权限，不含学生端「我的活动报名」
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, m.id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` m
WHERE m.id IN (5543)
  AND NOT EXISTS (SELECT 1 FROM `system_role_menu` rm WHERE rm.role_id = 1 AND rm.menu_id = m.id AND rm.deleted = b'0');

-- 若曾给 super_admin 分配过「我的活动报名」，移除
UPDATE `system_role_menu`
SET `deleted` = b'1', `updater` = '1', `update_time` = NOW()
WHERE `role_id` = 1 AND `menu_id` = 5542 AND `deleted` = b'0';

-- 学生角色（214）：需父级「专项活动」目录 + 我的报名
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 214, m.id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` m
WHERE m.id IN (5530, 5542)
  AND EXISTS (SELECT 1 FROM `system_role` WHERE `id` = 214 AND `deleted` = b'0')
  AND NOT EXISTS (SELECT 1 FROM `system_role_menu` rm WHERE rm.role_id = 214 AND rm.menu_id = m.id AND rm.deleted = b'0');

-- 若曾给教培角色分配过「我的活动报名」，移除（教培无需自助报名）
UPDATE `system_role_menu`
SET `deleted` = b'1', `updater` = '1', `update_time` = NOW()
WHERE `role_id` = 213 AND `menu_id` = 5542 AND `deleted` = b'0';
