-- 专项活动 H5 报名短信模板（阿里云）
-- 模板编码必须与后端常量一致：edu_activity_instance_enroll
-- 变量建议：activityName、periodNo、plannedDate、enrollUrl
--
-- H5 基址在字典配置：edu_activity_enroll_config / h5_base_url（见 20260904_专项活动H5报名基址字典.sql）
--
-- 使用前请在【系统管理 → 短信管理 → 短信模板】录入，并绑定已审核通过的阿里云模板。
-- 渠道需已配置阿里云短信。
--
-- 示例内容（仅供参考，以阿里云审核文案为准）：
-- 【签名】同学你好，活动${activityName}第${periodNo}期计划于${plannedDate}开展，请点击报名：${enrollUrl}

-- 本文件不自动插入，避免覆盖现有模板配置；由运维在后台手工创建。
SELECT 1;
