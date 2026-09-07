-- 活动实例：保存记录后直接已结项，不再使用待反馈状态
-- 可选：将历史「待反馈」且已有活动记录的实例升级为已结项（打开详情时也会自动升级）

UPDATE `edu_activity_instance` ai
INNER JOIN `edu_activity_instance_record` r ON r.instance_id = ai.id AND r.deleted = b'0'
SET ai.status = 'COMPLETED', ai.update_time = NOW()
WHERE ai.deleted = b'0'
  AND ai.status = 'PENDING_FEEDBACK';

-- 字典「待反馈」可保留用于历史展示，也可手动停用：
-- UPDATE system_dict_data SET status = 1 WHERE dict_type = 'edu_activity_instance_status' AND value = 'PENDING_FEEDBACK';
