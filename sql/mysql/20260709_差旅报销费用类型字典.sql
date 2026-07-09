-- 差旅报销：费用类型改为字典 oa_travel_expense_type
-- 字典值：1交通费 2住宿费 3餐饮费 4通讯费 5其他

-- 1. 新增字典类型
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2121, 'OA 差旅费用类型', 'oa_travel_expense_type', 0, '差旅报销-明细费用类型', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'oa_travel_expense_type');

-- 2. 新增字典数据
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21210, 1, '交通费', '1', 'oa_travel_expense_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_travel_expense_type' AND `value` = '1');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21211, 2, '住宿费', '2', 'oa_travel_expense_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_travel_expense_type' AND `value` = '2');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21212, 3, '餐饮费', '3', 'oa_travel_expense_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_travel_expense_type' AND `value` = '3');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21213, 4, '通讯费', '4', 'oa_travel_expense_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_travel_expense_type' AND `value` = '4');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21214, 5, '其他', '5', 'oa_travel_expense_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_travel_expense_type' AND `value` = '5');

-- 3.（可选）历史差旅报销单：若明细里存的是中文标签，映射为字典 value
-- 执行前请先备份；仅处理 bill_type=2 的差旅报销单
-- UPDATE `oa_expense_reimburse_detail` d
-- INNER JOIN `oa_expense_reimburse_bill` b ON b.id = d.bill_id AND b.bill_type = 2 AND b.deleted = 0
-- SET d.expense_type = CASE d.expense_type
--   WHEN '交通费' THEN '1'
--   WHEN '住宿费' THEN '2'
--   WHEN '餐饮费' THEN '3'
--   WHEN '通讯费' THEN '4'
--   WHEN '其他' THEN '5'
--   ELSE d.expense_type
-- END
-- WHERE d.deleted = 0;

-- 已执行标记（执行后取消注释）
-- -- executed: 2026-07-09 差旅报销费用类型字典
