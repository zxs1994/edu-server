-- ============================================================
-- 「奖金池」菜单/权限/字典展示名改为「预算配置」
-- 权限标识、路由 path、组件路径保持不变
-- ============================================================

-- 1) 菜单与按钮
UPDATE `system_menu`
SET `name` = '预算配置',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5550 AND `deleted` = b'0';

UPDATE `system_menu`
SET `name` = '预算配置查询',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5551 AND `deleted` = b'0';

UPDATE `system_menu`
SET `name` = '预算配置更新',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 5552 AND `deleted` = b'0';

-- 2) 时段模式字典（若已存在）
UPDATE `system_dict_type`
SET `name` = '预算配置时段模式',
    `remark` = '专项活动预算配置年度时段切分方式',
    `updater` = '1',
    `update_time` = NOW()
WHERE `type` = 'edu_reward_budget_period_mode' AND `deleted` = b'0';
