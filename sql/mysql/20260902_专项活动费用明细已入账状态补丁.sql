-- ============================================================
-- 专项活动 - 费用明细状态「已入账（积分）」补丁
-- 1. 新增字典 credited
-- 2. 调整 paid/rejected 排序
-- 3. 历史学生侧误标 paid 的数据回退为 credited
-- ============================================================

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22285, 3, '已入账（积分）', 'credited', 'edu_fee_item_status', 0, 'cyan', '', '学生侧结项后待积分入账', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'credited');

UPDATE `system_dict_data`
SET `sort` = 4, `remark` = '教师侧付款完成或学生积分已确认', `updater` = '1', `update_time` = NOW()
WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'paid';

UPDATE `system_dict_data`
SET `sort` = 5, `updater` = '1', `update_time` = NOW()
WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'rejected';

-- 历史数据：学生侧不应直接为 paid（积分模块未接入前）
UPDATE `edu_activity_instance_fee_item`
SET `status` = 'credited', `pay_time` = NULL, `updater` = '1', `update_time` = NOW()
WHERE `fee_side` = 'student' AND `status` = 'paid' AND `deleted` = b'0';
