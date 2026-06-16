-- =====================================================
-- 工作台徽标已读改造 - 数据库迁移脚本
-- 为 bpm_process_instance_copy 表新增 read_status 字段
-- =====================================================

-- 新增 read_status 字段（已读状态：0未读 1已读）
ALTER TABLE `bpm_process_instance_copy`
    ADD COLUMN `read_status` tinyint NOT NULL DEFAULT 0 COMMENT '已读状态：0未读 1已读' AFTER `reason`;

-- 将已有数据全部标记为已读（存量数据视为已读）
UPDATE `bpm_process_instance_copy` SET `read_status` = 1 WHERE `deleted` = b'0';
