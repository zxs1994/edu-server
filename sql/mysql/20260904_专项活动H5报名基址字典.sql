-- 专项活动 H5 报名：字典配置
-- 字典类型 edu_activity_enroll_config
-- h5_base_url → 标签填 H5 前端基址（如 https://edu.example.com），不要末尾斜杠
-- 短码不过期；修改后约 1 分钟内生效（字典缓存）

INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2260, '专项活动报名配置', 'edu_activity_enroll_config', 0,
       'H5 报名：h5_base_url=前端基址（短码不过期）', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'edu_activity_enroll_config');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 22600, 1, 'http://localhost:5666', 'h5_base_url', 'edu_activity_enroll_config', 0, 'default', '',
       '请改为生产/测试环境真实 H5 前端域名，不含末尾 /', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'edu_activity_enroll_config' AND `value` = 'h5_base_url');
