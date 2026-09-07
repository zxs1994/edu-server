-- ============================================================
-- 专项活动 - 费用明细状态字典补丁
-- 原因：原脚本误用 2226/22260-22263（与 edu_fee_currency 冲突）
-- 现改用：dict_type=2228，dict_data=22280-22283
-- ============================================================

INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2228, '专项活动费用明细状态', 'edu_fee_item_status', 0, '专项活动-实例费用明细状态', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'edu_fee_item_status');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22280, 1, '待付款申请', 'pending_request', 'edu_fee_item_status', 0, 'warning', '', '教培侧费用，待批量付款申请', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'pending_request');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22281, 2, '已提交付款申请', 'submitted', 'edu_fee_item_status', 0, 'processing', '', '已创建付款申请', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'submitted');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22282, 3, '已入账（积分）', 'credited', 'edu_fee_item_status', 0, 'cyan', '', '学生侧结项后待积分入账', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'credited');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22283, 4, '已付款', 'paid', 'edu_fee_item_status', 0, 'success', '', '教培侧付款完成或学生积分已确认', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'paid');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22284, 5, '已驳回', 'rejected', 'edu_fee_item_status', 0, 'error', '', '付款申请被驳回', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_item_status' AND `value` = 'rejected');
