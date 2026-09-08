-- ============================================================
-- 专项活动菜单分组：活动管理 / 费用管理
-- 叶子菜单改为绝对 path，配合前端「绝对路径不拼接父级」，保持原路由与 BPM 不变
-- ============================================================

-- 1) 一级目录：活动管理
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5562, '活动管理', '', 1, 1, 5530, 'activity-manage', 'lucide:calendar-range', NULL, NULL,
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5530 AND `deleted` = b'0')
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5562);

-- 2) 一级目录：费用管理
INSERT INTO `system_menu` (`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `app_visible`, `managed`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 5563, '费用管理', '', 1, 2, 5530, 'fee-manage', 'lucide:wallet-cards', NULL, NULL,
       0, b'1', b'1', b'1', b'1', b'1', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5530 AND `deleted` = b'0')
  AND NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 5563);

-- 3) 活动管理下：活动计划 / 详情 / 活动实例 / 详情（绝对路径，路由不变）
UPDATE `system_menu`
SET `parent_id` = 5562,
    `path` = '/edu/activity/list',
    `sort` = 1,
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5531 AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = 5562,
    `path` = '/edu/activity/info',
    `sort` = 2,
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5532 AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = 5562,
    `path` = '/edu/activity/instance/list',
    `sort` = 3,
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5538 AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = 5562,
    `path` = '/edu/activity/instance/info',
    `sort` = 4,
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5540 AND `deleted` = b'0';

-- 4) 费用管理下：费用明细 / 付款申请 / 详情 / 预算配置
UPDATE `system_menu`
SET `parent_id` = 5563,
    `path` = '/edu/activity/fee-item/list',
    `sort` = 1,
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5560 AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = 5563,
    `path` = '/edu/activity/payment/list',
    `sort` = 2,
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5553 AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = 5563,
    `path` = '/edu/activity/payment/info',
    `sort` = 3,
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5554 AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = 5563,
    `path` = '/edu/activity/reward-pool',
    `sort` = 4,
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5550 AND `deleted` = b'0';

-- 5) 「我的活动」仍挂在专项活动下，排在两个目录之后
UPDATE `system_menu`
SET `parent_id` = 5530,
    `sort` = 3,
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5542 AND `deleted` = b'0';

-- 6) 超级管理员授权新目录
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, m.id, '1', NOW(), '1', NOW(), b'0', 1
FROM `system_menu` m
WHERE m.id IN (5562, 5563)
  AND NOT EXISTS (
    SELECT 1 FROM `system_role_menu` rm
    WHERE rm.role_id = 1 AND rm.menu_id = m.id AND rm.deleted = b'0'
  );

-- 7) 已有对应子菜单权限的角色，同步授予父目录（避免仅「我的活动」的学生角色出现空目录）
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT DISTINCT rm.role_id, 5562, '1', NOW(), '1', NOW(), b'0', rm.tenant_id
FROM `system_role_menu` rm
WHERE rm.menu_id IN (5531, 5532, 5538, 5540)
  AND rm.deleted = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `system_role_menu` x
    WHERE x.role_id = rm.role_id AND x.menu_id = 5562 AND x.deleted = b'0' AND x.tenant_id = rm.tenant_id
  );

INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT DISTINCT rm.role_id, 5563, '1', NOW(), '1', NOW(), b'0', rm.tenant_id
FROM `system_role_menu` rm
WHERE rm.menu_id IN (5550, 5553, 5554, 5560)
  AND rm.deleted = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `system_role_menu` x
    WHERE x.role_id = rm.role_id AND x.menu_id = 5563 AND x.deleted = b'0' AND x.tenant_id = rm.tenant_id
  );
