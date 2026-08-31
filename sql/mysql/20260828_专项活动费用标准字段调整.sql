-- 专项活动费用标准：费用类型、币种、金额；移除费用项名称/单价/数量
ALTER TABLE `edu_activity_fee_standard`
  ADD COLUMN `fee_type` varchar(50) NULL DEFAULT NULL COMMENT '费用类型（字典 edu_fee_type）' AFTER `activity_id`,
  ADD COLUMN `currency` varchar(50) NULL DEFAULT NULL COMMENT '币种（字典 edu_fee_currency）' AFTER `fee_mode`,
  ADD COLUMN `amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '金额' AFTER `currency`;

-- 历史数据：金额 = 单价 * 数量
UPDATE `edu_activity_fee_standard`
SET `amount` = IFNULL(`unit_price`, 0) * IFNULL(`quantity`, 1)
WHERE `amount` IS NULL;

ALTER TABLE `edu_activity_fee_standard`
  DROP COLUMN `fee_name`,
  DROP COLUMN `unit_price`,
  DROP COLUMN `quantity`;

ALTER TABLE `edu_activity_fee_standard`
  MODIFY COLUMN `remark` varchar(500) NULL DEFAULT NULL COMMENT '费用项说明';

-- 字典：费用类型
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2225, '专项活动费用类型', 'edu_fee_type', 0, '专项活动-费用标准费用类型', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'edu_fee_type');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22250, 1, '培训费', 'TRAINING', 'edu_fee_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_type' AND `value` = 'TRAINING');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22251, 2, '材料费', 'MATERIAL', 'edu_fee_type', 0, 'processing', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_type' AND `value` = 'MATERIAL');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22252, 3, '交通费', 'TRAFFIC', 'edu_fee_type', 0, 'warning', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_type' AND `value` = 'TRAFFIC');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22253, 4, '其他', 'OTHER', 'edu_fee_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_type' AND `value` = 'OTHER');

-- 字典：币种
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2226, '专项活动费用币种', 'edu_fee_currency', 0, '专项活动-费用标准币种', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'edu_fee_currency');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22260, 1, '人民币', 'CNY', 'edu_fee_currency', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_currency' AND `value` = 'CNY');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22261, 2, '美元', 'USD', 'edu_fee_currency', 0, 'processing', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_currency' AND `value` = 'USD');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22262, 3, '欧元', 'EUR', 'edu_fee_currency', 0, 'success', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_currency' AND `value` = 'EUR');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22263, 4, '港币', 'HKD', 'edu_fee_currency', 0, 'warning', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_fee_currency' AND `value` = 'HKD');
