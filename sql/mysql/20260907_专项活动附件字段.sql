-- 专项活动：附件（JSON，非必填）
-- 格式：[{"fileName":"a.pdf","fileUrl":"https://..."}]
ALTER TABLE `edu_activity`
  ADD COLUMN `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '附件列表JSON' AFTER `remark`;
