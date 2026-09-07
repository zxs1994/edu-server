-- ============================================================
-- 菜单改名：执行实例 → 活动实例
-- ============================================================

UPDATE `system_menu`
SET `name` = '活动实例',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5538
  AND `name` = '执行实例'
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `name` = '活动实例查询',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5539
  AND `name` = '执行实例查询'
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `name` = '活动实例详情',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5540
  AND `name` = '执行实例详情'
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `name` = '活动实例更新',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5541
  AND `name` = '执行实例更新'
  AND `deleted` = b'0';
