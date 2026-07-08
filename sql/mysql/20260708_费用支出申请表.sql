-- ============================================================
-- 费用支出申请 - 主表/明细表 + 字典
-- 流程 Key：oa_expense_payment_bill
-- 单据类型码：114
-- ============================================================

CREATE TABLE IF NOT EXISTS `oa_expense_payment_bill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '流程实例编号',
  `process_status` int DEFAULT NULL COMMENT '单据状态',
  `payment_type` tinyint NOT NULL COMMENT '支出类型（1对公 2对私）',
  `project_category` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '费用归属项目',
  `apply_date` date DEFAULT NULL COMMENT '申请日期',
  `urgency_level` tinyint DEFAULT NULL COMMENT '紧急程度',
  `cause` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请说明',
  `total_amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '合计金额',
  `payment_status` tinyint DEFAULT 0 COMMENT '支付状态（0未支付 1已支付）',
  `payee_company_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收款单位名称（对公）',
  `payee_person_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '报销人姓名（对私）',
  `payee_bank` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收款开户行',
  `payee_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '收款账号',
  `contract_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '合同编号（手填）',
  `payment_method` tinyint DEFAULT NULL COMMENT '付款方式',
  `is_prepay` tinyint DEFAULT NULL COMMENT '是否预付（0否 1是）',
  `is_personal_advance` tinyint DEFAULT NULL COMMENT '是否个人垫付（0否 1是）',
  `official_card_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公务卡号',
  `creator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
  `company_id` bigint DEFAULT NULL COMMENT '公司ID',
  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公司名称',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门名称',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_bill_code` (`bill_code`) USING BTREE,
  KEY `idx_process_instance_id` (`process_instance_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='费用支出申请单';

CREATE TABLE IF NOT EXISTS `oa_expense_payment_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_id` bigint NOT NULL COMMENT '主单ID',
  `expense_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '费用类型',
  `expense_date` date DEFAULT NULL COMMENT '费用发生日期',
  `cause` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '费用事由/用途说明',
  `amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_bill_id` (`bill_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='费用支出申请明细';

-- 字典：支出类型
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2117, '费用支出类型', 'oa_expense_payment_type', 0, '费用支出申请-对公/对私', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'oa_expense_payment_type');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21170, 1, '对公支出', '1', 'oa_expense_payment_type', 0, 'processing', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_type' AND `value` = '1');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21171, 2, '对私报销', '2', 'oa_expense_payment_type', 0, 'success', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_type' AND `value` = '2');

-- 字典：费用归属项目
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2118, '费用归属项目', 'oa_expense_payment_project', 0, '费用支出申请-费用归属项目', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'oa_expense_payment_project');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21180, 1, '日常办公', '1', 'oa_expense_payment_project', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_project' AND `value` = '1');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21181, 2, '会议活动', '2', 'oa_expense_payment_project', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_project' AND `value` = '2');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21182, 3, '培训项目', '3', 'oa_expense_payment_project', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_project' AND `value` = '3');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21183, 4, '专项课题', '4', 'oa_expense_payment_project', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_project' AND `value` = '4');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21184, 5, '差旅费', '5', 'oa_expense_payment_project', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_project' AND `value` = '5');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21185, 6, '其他', '9', 'oa_expense_payment_project', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_project' AND `value` = '9');

-- 字典：费用明细类型（支出申请专用）
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2119, '费用支出明细类型', 'oa_expense_payment_detail_type', 0, '费用支出申请-明细费用类型', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'oa_expense_payment_detail_type');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21190, 1, '办公费', '1', 'oa_expense_payment_detail_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_detail_type' AND `value` = '1');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21191, 2, '差旅费', '2', 'oa_expense_payment_detail_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_detail_type' AND `value` = '2');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21192, 3, '会议费', '3', 'oa_expense_payment_detail_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_detail_type' AND `value` = '3');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21193, 4, '培训费', '4', 'oa_expense_payment_detail_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_detail_type' AND `value` = '4');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21194, 5, '业务招待费', '5', 'oa_expense_payment_detail_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_detail_type' AND `value` = '5');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21195, 6, '采购费', '6', 'oa_expense_payment_detail_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_detail_type' AND `value` = '6');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21196, 7, '其他', '9', 'oa_expense_payment_detail_type', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_expense_payment_detail_type' AND `value` = '9');

-- 已执行标记（执行后取消注释）
-- -- executed: 2026-07-08 费用支出申请表+字典
