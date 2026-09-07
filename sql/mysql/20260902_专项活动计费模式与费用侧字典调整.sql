-- 专项活动计费模式：包干 + 按人头（替换按场次）
-- 费用侧：教培 + 学生（替换校方支出/学员收费）

UPDATE `system_dict_data`
SET `label` = '包干', `value` = 'fixed', `sort` = 1, `remark` = '结项时按固定总额结算'
WHERE `dict_type` = 'edu_fee_mode' AND `value` IN ('FIXED', 'fixed');

UPDATE `system_dict_data`
SET `label` = '按人头', `value` = 'head', `sort` = 2, `color_type` = 'processing', `remark` = '结项时按实例出勤人数折算'
WHERE `dict_type` = 'edu_fee_mode' AND `value` IN ('SESSION', 'session', 'head');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22232, 2, '按人头', 'head', 'edu_fee_mode', 0, 'processing', '', '结项时按实例出勤人数折算', '1', NOW(), '1', NOW(), b'0'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_mode' AND `value` = 'head');

UPDATE `edu_activity_fee_standard` SET `fee_mode` = 'fixed' WHERE `fee_mode` IN ('FIXED', 'fixed');
UPDATE `edu_activity_fee_standard` SET `fee_mode` = 'head' WHERE `fee_mode` IN ('SESSION', 'session');

UPDATE `system_dict_data`
SET `label` = '教培', `value` = 'teacher', `sort` = 1, `remark` = '付给教培，需指定收款人'
WHERE `dict_type` = 'edu_fee_side' AND `value` IN ('SCHOOL', 'teacher');

UPDATE `system_dict_data`
SET `label` = '学生', `value` = 'student', `sort` = 2, `remark` = '系统给出勤学生发积分，无需收款人'
WHERE `dict_type` = 'edu_fee_side' AND `value` IN ('STUDENT', 'student');

UPDATE `edu_activity_fee_standard` SET `fee_side` = 'teacher' WHERE `fee_side` = 'SCHOOL';
UPDATE `edu_activity_fee_standard` SET `fee_side` = 'student' WHERE `fee_side` = 'STUDENT';
