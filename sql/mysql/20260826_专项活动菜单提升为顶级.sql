-- ============================================================
-- 将已存在的「专项活动」从基础信息下提升为顶级菜单
-- 路由保持 /edu/activity/list、/edu/activity/info（BPM 不变）
-- ============================================================

UPDATE `system_menu`
SET `parent_id` = 0,
    `path` = '/edu/activity',
    `sort` = 25,
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5530
  AND `deleted` = b'0';
