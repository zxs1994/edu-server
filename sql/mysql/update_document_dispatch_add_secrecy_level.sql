-- ============================================================
-- 公文发文管理 - 新增密级字段 & 字典调整
-- 执行数据库: dh-oa
-- 日期: 2026-06-12
-- ============================================================

-- ----------------------------
-- 1. oa_document_dispatch_bill 表新增 secrecy_level（密级）字段
--    值域: 0=公开 1=内部 2=机密 3=绝密
-- ----------------------------
ALTER TABLE `oa_document_dispatch_bill`
ADD COLUMN `secrecy_level` tinyint DEFAULT 0
  COMMENT '密级（0公开 1内部 2机密 3绝密）'
  AFTER `urgency_level`;

-- 添加索引（幂等处理：先判断是否已存在）
SET @idx_exists = (
  SELECT COUNT(*) FROM information_schema.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'oa_document_dispatch_bill'
    AND INDEX_NAME = 'idx_secrecy_level'
);
SET @sql = IF(@idx_exists = 0,
  'ALTER TABLE `oa_document_dispatch_bill` ADD INDEX `idx_secrecy_level`(`secrecy_level` ASC) USING BTREE',
  'SELECT ''索引 idx_secrecy_level 已存在，跳过'' AS info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ----------------------------
-- 2. 新建字典类型 oa_secrecy_level（密级）
-- ----------------------------
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES ('OA 密级', 'oa_secrecy_level', 0, '公文发文-密级', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `name` = 'OA 密级', `remark` = '公文发文-密级';

-- ----------------------------
-- 3. 插入密级字典数据（先清除旧数据，再写入）
-- ----------------------------
DELETE FROM `system_dict_data` WHERE `dict_type` = 'oa_secrecy_level';

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
  (0, '公开', '0', 'oa_secrecy_level', 0, 'default', '', '1', NOW(), '1', NOW(), b'0'),
  (1, '内部', '1', 'oa_secrecy_level', 0, 'primary', '', '1', NOW(), '1', NOW(), b'0'),
  (2, '机密', '2', 'oa_secrecy_level', 0, 'warning', '', '1', NOW(), '1', NOW(), b'0'),
  (3, '绝密', '3', 'oa_secrecy_level', 0, 'danger',  '', '1', NOW(), '1', NOW(), b'0');

-- ----------------------------
-- 4. 更新字典 oa_urgency_level：将"紧急"改为"急件"
-- ----------------------------
UPDATE `system_dict_data`
SET `label` = '急件', `updater` = '1', `update_time` = NOW()
WHERE `dict_type` = 'oa_urgency_level' AND `value` = '1';
