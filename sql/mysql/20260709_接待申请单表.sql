-- ============================================================
-- 接待申请单 - 主表 + 字典
-- 流程 Key：oa_reception_apply_bill
-- 单据类型码：115
-- ============================================================

CREATE TABLE IF NOT EXISTS `oa_reception_apply_bill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `bill_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单据编号',
  `process_instance_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '流程实例编号',
  `process_status` int DEFAULT NULL COMMENT '单据状态',
  `cause` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请事由',
  `dining_time` datetime DEFAULT NULL COMMENT '就餐时间',
  `dining_standard` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '就餐标准',
  `guest_count` int DEFAULT NULL COMMENT '来宾人数',
  `accompany_count` int DEFAULT NULL COMMENT '陪同人数',
  `estimated_cost` decimal(18,2) DEFAULT NULL COMMENT '预估费用',
  `creator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '申请人姓名',
  `company_id` bigint DEFAULT NULL COMMENT '公司ID',
  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公司名称',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门名称',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_bill_code` (`bill_code`) USING BTREE,
  KEY `idx_process_instance_id` (`process_instance_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='接待申请单';

-- 字典：就餐标准
INSERT INTO `system_dict_type` (`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT 2120, '接待就餐标准', 'oa_reception_dining_standard', 0, '接待申请单-就餐标准', '1', NOW(), '1', NOW(), b'0', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_type` WHERE `type` = 'oa_reception_dining_standard');

INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21200, 1, 'A类（≤300元/人）', 'a', 'oa_reception_dining_standard', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_reception_dining_standard' AND `value` = 'a');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21201, 2, 'B类（≤200元/人）', 'b', 'oa_reception_dining_standard', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_reception_dining_standard' AND `value` = 'b');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21202, 3, 'C类（≤100元/人）', 'c', 'oa_reception_dining_standard', 0, 'default', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_reception_dining_standard' AND `value` = 'c');
INSERT INTO `system_dict_data` (`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 21203, 4, '特殊审批', 'special', 'oa_reception_dining_standard', 0, 'warning', '', '', '1', NOW(), '1', NOW(), b'0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'oa_reception_dining_standard' AND `value` = 'special');

-- 已执行标记（执行后取消注释）
-- -- executed: 2026-07-09 接待申请单表+字典
