-- 站内信：恢复菜单入口 + 活动实例创建通知模板 + 修复收不到问题
-- 执行后需重新登录（或刷新权限缓存）才能看到「消息中心」菜单

-- 1) 恢复消息中心 / 站内信管理菜单（软删还原）
UPDATE `system_menu`
SET `deleted` = b'0', `update_time` = NOW()
WHERE `id` IN (2739, 2144, 2145, 2146, 2147, 2148, 2149, 2150, 2151, 2152)
  AND `deleted` = b'1';

-- 2) 活动实例生成站内信模板（仅发给学生参与人，含报名链接）
INSERT INTO `system_notify_template`
(`name`, `code`, `nickname`, `content`, `type`, `params`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT
  '活动实例已生成',
  'EDU_ACTIVITY_INSTANCE_CREATED',
  '教务系统',
  '专项活动「{activityName}」第{periodNo}期已生成实例（{instanceCode}），计划执行时间：{plannedDate}。请点击「去报名」完成报名。',
  2,
  '["activityName","periodNo","instanceCode","plannedDate"]',
  0,
  '活动审批通过生成实例后，通知学生参与人前往报名',
  '1',
  NOW(),
  '1',
  NOW(),
  b'0'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `system_notify_template` WHERE `code` = 'EDU_ACTIVITY_INSTANCE_CREATED' AND `deleted` = b'0'
);

-- 2.1) 已存在模板时同步内容与参数（学生报名；前端用参数渲染「去报名」按钮）
UPDATE `system_notify_template`
SET `content` = '专项活动「{activityName}」第{periodNo}期已生成实例（{instanceCode}），计划执行时间：{plannedDate}。请点击「去报名」完成报名。',
    `params` = '["activityName","periodNo","instanceCode","plannedDate"]',
    `remark` = '活动审批通过生成实例后，通知学生参与人前往报名；前端按模板编码渲染「去报名」按钮，不依赖外链域名',
    `updater` = '1',
    `update_time` = NOW()
WHERE `code` = 'EDU_ACTIVITY_INSTANCE_CREATED'
  AND `deleted` = b'0';

-- 3) 修复历史站内信 tenant_id=0（流程回调 Ignore 租户导致接收人按租户查不到）
UPDATE `system_notify_message` m
INNER JOIN `system_users` u ON u.id = m.user_id AND u.deleted = b'0'
SET m.tenant_id = u.tenant_id, m.update_time = NOW()
WHERE m.deleted = b'0'
  AND (m.tenant_id IS NULL OR m.tenant_id = 0)
  AND u.tenant_id IS NOT NULL
  AND u.tenant_id > 0;
