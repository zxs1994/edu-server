-- 日常费用报销：费用类型改为字典 oa_expense_type
-- 字典值：1办公用品 2劳保用品 3电脑耗材 4设计制作 5快递物流

-- 1. 确保字典类型存在
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2103, 'OA 日常费用类型', 'oa_expense_type', 0, '日常费用报销-明细费用类型', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'oa_expense_type');

UPDATE `system_dict_type`
SET `name` = 'OA 日常费用类型',
    `remark` = '日常费用报销-明细费用类型',
    `updater` = '1',
    `update_time` = NOW()
WHERE `type` = 'oa_expense_type';

-- 2. 停用旧字典项（会议/招待/其他等）
UPDATE `system_dict_data`
SET `deleted` = b'1',
    `updater` = '1',
    `update_time` = NOW()
WHERE `dict_type` = 'oa_expense_type'
  AND `value` IN ('6', '7', '8');

-- 3. 更新/新增日常费用类型字典项
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 3431, 1, '办公用品', '1', 'oa_expense_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_type' AND `value` = '1');
UPDATE `system_dict_data`
SET `sort` = 1, `label` = '办公用品', `status` = 0, `deleted` = b'0', `updater` = '1', `update_time` = NOW()
WHERE `dict_type` = 'oa_expense_type' AND `value` = '1';

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 3432, 2, '劳保用品', '2', 'oa_expense_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_type' AND `value` = '2');
UPDATE `system_dict_data`
SET `sort` = 2, `label` = '劳保用品', `status` = 0, `deleted` = b'0', `updater` = '1', `update_time` = NOW()
WHERE `dict_type` = 'oa_expense_type' AND `value` = '2';

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 3433, 3, '电脑耗材', '3', 'oa_expense_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_type' AND `value` = '3');
UPDATE `system_dict_data`
SET `sort` = 3, `label` = '电脑耗材', `status` = 0, `deleted` = b'0', `updater` = '1', `update_time` = NOW()
WHERE `dict_type` = 'oa_expense_type' AND `value` = '3';

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 3434, 4, '设计制作', '4', 'oa_expense_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_type' AND `value` = '4');
UPDATE `system_dict_data`
SET `sort` = 4, `label` = '设计制作', `status` = 0, `deleted` = b'0', `updater` = '1', `update_time` = NOW()
WHERE `dict_type` = 'oa_expense_type' AND `value` = '4';

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 3435, 5, '快递物流', '5', 'oa_expense_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_type' AND `value` = '5');
UPDATE `system_dict_data`
SET `sort` = 5, `label` = '快递物流', `status` = 0, `deleted` = b'0', `updater` = '1', `update_time` = NOW()
WHERE `dict_type` = 'oa_expense_type' AND `value` = '5';

-- 4.（可选）历史日常报销单：若明细里存的是中文标签，映射为字典 value
-- 执行前请先备份；仅处理 bill_type=1 的日常报销单
-- UPDATE `oa_expense_reimburse_detail` d
-- INNER JOIN `oa_expense_reimburse_bill` b ON b.id = d.bill_id AND b.bill_type = 1 AND b.deleted = 0
-- SET d.expense_type = CASE d.expense_type
--   WHEN '办公用品' THEN '1'
--   WHEN '劳保用品' THEN '2'
--   WHEN '电脑耗材' THEN '3'
--   WHEN '设计制作' THEN '4'
--   WHEN '快递物流' THEN '5'
--   ELSE d.expense_type
-- END
-- WHERE d.deleted = 0;

-- 已执行标记（执行后取消注释）
-- -- executed: 2026-07-09 日常费用报销费用类型字典
