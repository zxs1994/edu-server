-- 专项活动 H5 报名短链：取消过期时间
-- 1) expire_time 改为可空（新建短码不再写入）
-- 2) 停用字典 token_expire_days

ALTER TABLE `edu_activity_enroll_link`
  MODIFY COLUMN `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间（已废弃，短码不过期）';

UPDATE `system_dict_type`
SET `remark` = 'H5 报名：h5_base_url=前端基址（短码不过期）',
    `updater` = '1',
    `update_time` = NOW()
WHERE `type` = 'edu_activity_enroll_config';

UPDATE `system_dict_data`
SET `deleted` = b'1',
    `updater` = '1',
    `update_time` = NOW()
WHERE `dict_type` = 'edu_activity_enroll_config'
  AND `value` = 'token_expire_days'
  AND `deleted` = b'0';
