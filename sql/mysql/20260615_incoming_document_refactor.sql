-- ============================================================
-- 公文收文管理 - 字段重构
-- 新增: secrecy_level, host_person, leader_instruction,
--       handling_deadline, content_summary
-- 删除: sender, doc_summary, is_important, cause
-- 执行数据库: dh-oa
-- 日期: 2026-06-15
-- ============================================================

-- ----------------------------
-- 1. 新增字段
-- ----------------------------
ALTER TABLE `oa_incoming_document_bill`
    ADD COLUMN `secrecy_level` tinyint DEFAULT 0
        COMMENT '密级（0公开 1内部 2机密 3绝密）'
        AFTER `doc_number`,
    ADD COLUMN `host_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL
        COMMENT '主办人'
        AFTER `handling_dept_name`,
    ADD COLUMN `leader_instruction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
        COMMENT '领导批示'
        AFTER `host_person`,
    ADD COLUMN `handling_deadline` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL
        COMMENT '办理期限'
        AFTER `handling_result`,
    ADD COLUMN `content_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
        COMMENT '内容摘要'
        AFTER `handling_deadline`;

-- ----------------------------
-- 2. 删除废弃字段（先删索引，再删列）
-- ----------------------------
ALTER TABLE `oa_incoming_document_bill`
    DROP INDEX `idx_is_important`,
    DROP COLUMN `sender`,
    DROP COLUMN `doc_summary`,
    DROP COLUMN `is_important`,
    DROP COLUMN `cause`;

-- ----------------------------
-- 3. 新增索引
-- ----------------------------
SET @idx_exists = (
    SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'oa_incoming_document_bill'
      AND INDEX_NAME = 'idx_secrecy_level'
);
SET @sql = IF(@idx_exists = 0,
    'ALTER TABLE `oa_incoming_document_bill` ADD INDEX `idx_secrecy_level`(`secrecy_level` ASC) USING BTREE',
    'SELECT ''索引 idx_secrecy_level 已存在，跳过'' AS info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 已执行
