-- ============================================================
-- 公文发文 - 新增套红模板模块 & 扩展发文单字段
-- 执行数据库: dh-oa
-- 日期: 2026-06-12
-- ============================================================

-- ============================
-- 一、套红模板表 oa_red_template
-- ============================
CREATE TABLE IF NOT EXISTS `oa_red_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `org_name` varchar(200) DEFAULT NULL COMMENT '机关名称（红头大字）',
  `doc_type_label` varchar(50) DEFAULT '文 件' COMMENT '文件类型标签（如：文 件、通 知）',
  `header_color` varchar(20) DEFAULT '#FF0000' COMMENT '红头颜色',
  `template_content` text COMMENT '模板HTML内容',
  `preview_image` varchar(500) DEFAULT NULL COMMENT '预览图片URL',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套红模板';

-- ============================
-- 二、oa_document_dispatch_bill 新增字段
-- ============================

-- 套红模板关联
ALTER TABLE `oa_document_dispatch_bill`
  ADD COLUMN `template_id` bigint DEFAULT NULL COMMENT '套红模板ID' AFTER `secrecy_level`;

-- 发文字号拆分字段
ALTER TABLE `oa_document_dispatch_bill`
  ADD COLUMN `doc_number_prefix` varchar(50) DEFAULT NULL COMMENT '发文字号前缀（如：无办发）' AFTER `doc_number`,
  ADD COLUMN `doc_number_year` int DEFAULT NULL COMMENT '发文字号年份（如：2026）' AFTER `doc_number_prefix`,
  ADD COLUMN `doc_number_serial` int DEFAULT NULL COMMENT '发文字号序号' AFTER `doc_number_year`;

-- 公开类别
ALTER TABLE `oa_document_dispatch_bill`
  ADD COLUMN `disclosure_category` tinyint DEFAULT 0 COMMENT '公开类别（0主动公开 1依申请公开 2不公开）' AFTER `urgency_level`;

-- 发文日期
ALTER TABLE `oa_document_dispatch_bill`
  ADD COLUMN `issue_date` date DEFAULT NULL COMMENT '发文日期' AFTER `disclosure_category`;

-- 主送部门（逗号分隔，多选）
ALTER TABLE `oa_document_dispatch_bill`
  ADD COLUMN `main_recipients` varchar(1000) DEFAULT NULL COMMENT '主送部门（逗号分隔）' AFTER `issue_date`;

-- 抄送部门（逗号分隔，多选）
ALTER TABLE `oa_document_dispatch_bill`
  ADD COLUMN `cc_departments` varchar(1000) DEFAULT NULL COMMENT '抄送部门（逗号分隔）' AFTER `main_recipients`;

-- 签发人
ALTER TABLE `oa_document_dispatch_bill`
  ADD COLUMN `signer` varchar(100) DEFAULT NULL COMMENT '签发人' AFTER `cc_departments`;

-- ============================
-- 三、新增字典：公开类别 oa_disclosure_category
-- ============================
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES ('OA 公开类别', 'oa_disclosure_category', 0, '公文发文-公开类别', '1', NOW(), '1', NOW(), b'0')
ON DUPLICATE KEY UPDATE `name` = 'OA 公开类别';

DELETE FROM `system_dict_data` WHERE `dict_type` = 'oa_disclosure_category';
INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
  (0, '主动公开', '0', 'oa_disclosure_category', 0, 'success', '', '1', NOW(), '1', NOW(), b'0'),
  (1, '依申请公开', '1', 'oa_disclosure_category', 0, 'warning', '', '1', NOW(), '1', NOW(), b'0'),
  (2, '不公开',     '2', 'oa_disclosure_category', 0, 'danger',  '', '1', NOW(), '1', NOW(), b'0');
