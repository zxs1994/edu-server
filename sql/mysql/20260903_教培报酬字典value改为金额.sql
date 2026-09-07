-- ============================================================
-- 教培报酬/奖励标准：保持字典，value 改为可转数字的金额字符串
-- 选收款人时可 Number(value) 带出费用金额
-- ============================================================

-- 若曾改为 decimal，恢复为 varchar（已是 varchar 时保持兼容）
ALTER TABLE `edu_teacher`
  MODIFY COLUMN `reward_standard` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '报酬/奖励标准（字典 edu_teacher_reward，value 为金额）';

-- 恢复字典类型（若被软删）
UPDATE `system_dict_type`
SET `deleted` = b'0',
    `deleted_time` = NULL,
    `remark` = '教培档案-报酬/奖励标准（字典 value 为金额，可转数字）',
    `updater` = '1',
    `update_time` = NOW()
WHERE `type` = 'edu_teacher_reward';

INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2211, '教培报酬奖励标准', 'edu_teacher_reward', 0, '教培档案-报酬/奖励标准（字典 value 为金额，可转数字）', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'edu_teacher_reward');

-- 按 id 更新：label 展示，value 为金额数字字符串
UPDATE `system_dict_data`
SET `deleted` = b'0',
    `label` = '300元',
    `value` = '300',
    `sort` = 1,
    `color_type` = 'default',
    `remark` = '报酬标准 300 元',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 22110;

UPDATE `system_dict_data`
SET `deleted` = b'0',
    `label` = '500元',
    `value` = '500',
    `sort` = 2,
    `color_type` = 'processing',
    `remark` = '报酬标准 500 元',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 22111;

UPDATE `system_dict_data`
SET `deleted` = b'0',
    `label` = '800元',
    `value` = '800',
    `sort` = 3,
    `color_type` = 'success',
    `remark` = '报酬标准 800 元',
    `updater` = '1',
    `update_time` = NOW()
WHERE `id` = 22112;

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22110, 1, '300元', '300', 'edu_teacher_reward', 0, 'default', '', '报酬标准 300 元', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `id` = 22110);

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22111, 2, '500元', '500', 'edu_teacher_reward', 0, 'processing', '', '报酬标准 500 元', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `id` = 22111);

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22112, 3, '800元', '800', 'edu_teacher_reward', 0, 'success', '', '报酬标准 800 元', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `id` = 22112);

-- 历史档案：旧字典值 A/B/C 映射到金额
UPDATE `edu_teacher` SET `reward_standard` = '300' WHERE `reward_standard` = 'A';
UPDATE `edu_teacher` SET `reward_standard` = '500' WHERE `reward_standard` = 'B';
UPDATE `edu_teacher` SET `reward_standard` = '800' WHERE `reward_standard` = 'C';
