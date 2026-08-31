-- 基础信息菜单图标调整（与首页信息总览一致）
UPDATE `system_menu`
SET `icon` = 'lucide:graduation-cap',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5501;

UPDATE `system_menu`
SET `icon` = 'lucide:presentation',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5510;
