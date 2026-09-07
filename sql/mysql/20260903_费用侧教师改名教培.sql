-- ============================================================
-- 字典文案：费用侧「教师」→「教培」
-- ============================================================

UPDATE `system_dict_data`
SET `label` = '教培',
    `remark` = '付给教培，需指定收款人',
    `updater` = '1',
    `update_time` = NOW()
WHERE `dict_type` = 'edu_fee_side'
  AND `value` = 'teacher'
  AND `deleted` = b'0';

UPDATE `system_dict_data`
SET `remark` = REPLACE(`remark`, '教师侧', '教培侧'),
    `updater` = '1',
    `update_time` = NOW()
WHERE `dict_type` = 'edu_fee_item_status'
  AND `remark` LIKE '%教师侧%'
  AND `deleted` = b'0';
