-- ============================================================
-- 专项活动 - 奖金池（系统单池）表 + 字典 + 菜单权限
-- ============================================================

CREATE TABLE IF NOT EXISTS `edu_reward_pool` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '专项活动奖金池' COMMENT '池名称',
  `total_budget` decimal(14, 2) NOT NULL DEFAULT 0.00 COMMENT '总预算',
  `frozen_amount` decimal(14, 2) NOT NULL DEFAULT 0.00 COMMENT '已冻结',
  `paid_amount` decimal(14, 2) NOT NULL DEFAULT 0.00 COMMENT '已实发',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0启用 1停用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动奖金池';

CREATE TABLE IF NOT EXISTS `edu_reward_pool_txn` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `pool_id` bigint NOT NULL COMMENT '奖金池ID',
  `txn_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流水类型',
  `amount` decimal(14, 2) NOT NULL COMMENT '变动金额（绝对值）',
  `total_after` decimal(14, 2) NOT NULL COMMENT '变动后总预算',
  `frozen_after` decimal(14, 2) NOT NULL COMMENT '变动后已冻结',
  `paid_after` decimal(14, 2) NOT NULL COMMENT '变动后已实发',
  `biz_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
  `biz_id` bigint NULL DEFAULT NULL COMMENT '业务ID',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '说明',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pool_id`(`pool_id` ASC) USING BTREE,
  INDEX `idx_txn_type`(`txn_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动奖金池流水';

-- 默认单池（幂等）
INSERT INTO `edu_reward_pool` (`name`, `total_budget`, `frozen_amount`, `paid_amount`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT '专项活动奖金池', 0.00, 0.00, 0.00, 0, '系统默认奖金池', '1', NOW(), '1', NOW(), b'0', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `edu_reward_pool` WHERE `deleted` = b'0');

-- 字典：奖金池状态
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2240, '奖金池状态', 'edu_reward_pool_status', 0, '专项活动-奖金池启停状态', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'edu_reward_pool_status');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22400, 1, '启用', '0', 'edu_reward_pool_status', 0, 'success', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_reward_pool_status' AND `value` = '0');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22401, 2, '停用', '1', 'edu_reward_pool_status', 0, 'danger', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_reward_pool_status' AND `value` = '1');

-- 字典：奖金池流水类型（含预留）
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2241, '奖金池流水类型', 'edu_reward_pool_txn_type', 0, '专项活动-奖金池流水类型', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'edu_reward_pool_txn_type');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22410, 1, '充值', 'RECHARGE', 'edu_reward_pool_txn_type', 0, 'success', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_reward_pool_txn_type' AND `value` = 'RECHARGE');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22411, 2, '调减', 'ADJUST_DOWN', 'edu_reward_pool_txn_type', 0, 'warning', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_reward_pool_txn_type' AND `value` = 'ADJUST_DOWN');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22412, 3, '冻结', 'FREEZE', 'edu_reward_pool_txn_type', 0, 'primary', '', '预留：活动审批通过冻结预算', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_reward_pool_txn_type' AND `value` = 'FREEZE');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22413, 4, '解冻', 'UNFREEZE', 'edu_reward_pool_txn_type', 0, 'default', '', '预留：活动取消/驳回解冻', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_reward_pool_txn_type' AND `value` = 'UNFREEZE');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22414, 5, '实发', 'PAY', 'edu_reward_pool_txn_type', 0, 'danger', '', '预留：付款完成扣减冻结并计入实发', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_reward_pool_txn_type' AND `value` = 'PAY');

-- 菜单：奖金池（挂在专项活动目录下）
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5550, '奖金池', '', 2, 5, parent.id, 'reward-pool', 'lucide:wallet',
       'edu/reward-pool/index', 'EduRewardPool',
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM `system_menu` parent
WHERE parent.`path` = '/edu/activity'
  AND parent.`parent_id` = 0
  AND parent.`deleted` = b'0'
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5550);

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5551, '奖金池查询', 'edu:reward-pool:query', 3, 1, 5550, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5550 AND `deleted` = b'0')
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5551);

INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5552, '奖金池更新', 'edu:reward-pool:update', 3, 2, 5550, '', '#', NULL, NULL, 0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5550 AND `deleted` = b'0')
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5552);

-- 超级管理员角色授权（role_id=1）
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, m.id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` m
WHERE m.id IN (5550, 5551, 5552)
  AND NOT EXISTS (SELECT 1 FROM `system_role_menu` rm WHERE rm.role_id = 1 AND rm.menu_id = m.id AND rm.deleted = b'0');
