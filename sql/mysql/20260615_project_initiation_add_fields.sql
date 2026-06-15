-- ============================================================
-- 项目立项管理表结构升级：新增字段、移除旧字段
-- 日期：2026-06-15
-- 说明：根据新的立项管理设计，调整表结构以支持优先级、项目分类、
--       项目经理、关联合同、对方单位等新字段
-- ============================================================

USE `dh-oa`;

-- ========== 1. 删除旧字段 ==========
ALTER TABLE oa_project_initiation_bill
    DROP COLUMN expected_outcome,
    DROP COLUMN is_major,
    DROP COLUMN major_remark,
    DROP COLUMN cause;

-- ========== 2. 新增业务字段 ==========
ALTER TABLE oa_project_initiation_bill
    ADD COLUMN priority            TINYINT       DEFAULT NULL  COMMENT '优先级：1高 2中 3低' AFTER project_type,
    ADD COLUMN project_category    TINYINT       DEFAULT NULL  COMMENT '项目分类：1研发项目 2交付项目 3运维项目' AFTER priority,
    ADD COLUMN project_set_id      BIGINT        DEFAULT NULL  COMMENT '所属项目集ID' AFTER project_category,
    ADD COLUMN project_set_name    VARCHAR(200)  DEFAULT NULL  COMMENT '所属项目集名称' AFTER project_set_id,
    ADD COLUMN related_contract_id BIGINT        DEFAULT NULL  COMMENT '关联合同ID' AFTER end_date,
    ADD COLUMN contract_code       VARCHAR(100)  DEFAULT NULL  COMMENT '合同编号' AFTER related_contract_id,
    ADD COLUMN contract_name       VARCHAR(200)  DEFAULT NULL  COMMENT '合同名称' AFTER contract_code,
    ADD COLUMN project_manager_id  BIGINT        DEFAULT NULL  COMMENT '项目经理ID' AFTER budget_amount,
    ADD COLUMN project_manager_name VARCHAR(100) DEFAULT NULL  COMMENT '项目经理名称' AFTER project_manager_id,
    ADD COLUMN counterparty_type   TINYINT       DEFAULT NULL  COMMENT '对方类型：1CRM客户 2ERP供应商' AFTER project_manager_name,
    ADD COLUMN counterparty_id     BIGINT        DEFAULT NULL  COMMENT '对方单位ID' AFTER counterparty_type,
    ADD COLUMN counterparty_name   VARCHAR(200)  DEFAULT NULL  COMMENT '对方单位名称' AFTER counterparty_id,
    ADD COLUMN counterparty_contact VARCHAR(100) DEFAULT NULL  COMMENT '对方联系人' AFTER counterparty_name,
    ADD COLUMN counterparty_phone  VARCHAR(50)   DEFAULT NULL  COMMENT '对方联系电话' AFTER counterparty_contact;

-- ========== 3. 新增索引 ==========
ALTER TABLE oa_project_initiation_bill
    ADD INDEX idx_oa_pib_priority (priority),
    ADD INDEX idx_oa_pib_project_category (project_category),
    ADD INDEX idx_oa_pib_project_manager (project_manager_id),
    ADD INDEX idx_oa_pib_counterparty_type (counterparty_type);

-- ========== 4. 更新字典类型 ==========
-- 新增 oa_priority 字典类型
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT 1, '高', '1', 'oa_priority', 0, 'danger', '', NULL, '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'oa_priority' AND value = '1' AND deleted = b'0');

INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT 2, '中', '2', 'oa_priority', 0, 'processing', '', NULL, '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'oa_priority' AND value = '2' AND deleted = b'0');

INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT 3, '低', '3', 'oa_priority', 0, 'default', '', NULL, '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'oa_priority' AND value = '3' AND deleted = b'0');

-- 新增 oa_project_category 字典类型
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT 1, '研发项目', '1', 'oa_project_category', 0, '', '', NULL, '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'oa_project_category' AND value = '1' AND deleted = b'0');

INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT 2, '交付项目', '2', 'oa_project_category', 0, '', '', NULL, '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'oa_project_category' AND value = '2' AND deleted = b'0');

INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT 3, '运维项目', '3', 'oa_project_category', 0, '', '', NULL, '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'oa_project_category' AND value = '3' AND deleted = b'0');

-- 新增 oa_counterparty_type 字典类型
INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT 1, 'CRM客户', '1', 'oa_counterparty_type', 0, 'processing', '', NULL, '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'oa_counterparty_type' AND value = '1' AND deleted = b'0');

INSERT INTO system_dict_data (sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted)
SELECT 2, 'ERP供应商', '2', 'oa_counterparty_type', 0, 'success', '', NULL, '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_data WHERE dict_type = 'oa_counterparty_type' AND value = '2' AND deleted = b'0');

-- ========== 5. 更新项目类型字典数据 ==========
-- 更新现有 oa_project_type 字典数据：活动→研发型, 项目→交付实施型, 课题→工程建造型, 删除其他
UPDATE system_dict_data SET label = '研发型', updater = '1', update_time = NOW() WHERE dict_type = 'oa_project_type' AND value = '1' AND deleted = b'0';
UPDATE system_dict_data SET label = '交付实施型', updater = '1', update_time = NOW() WHERE dict_type = 'oa_project_type' AND value = '2' AND deleted = b'0';
UPDATE system_dict_data SET label = '工程建造型', updater = '1', update_time = NOW() WHERE dict_type = 'oa_project_type' AND value = '3' AND deleted = b'0';
UPDATE system_dict_data SET deleted = b'1', updater = '1', update_time = NOW() WHERE dict_type = 'oa_project_type' AND value = '4' AND deleted = b'0';

-- ========== 已执行 ==========
