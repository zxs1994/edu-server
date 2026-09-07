-- 通知公告类型：新增「专项活动」（system_notice_type value=5）
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22277, 5, '专项活动', '5', 'system_notice_type', 0, 'processing', '', '专项活动实例公告', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'system_notice_type' AND `value` = '5');
