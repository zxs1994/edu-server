-- 专项活动 - 「我的活动报名」改名为「我的活动」
UPDATE `system_menu`
SET `name` = '我的活动', `updater` = '1', `update_time` = NOW()
WHERE `id` = 5542 AND `deleted` = b'0';
