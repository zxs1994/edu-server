-- =====================================================
-- 差旅管理模块 数据库迁移脚本
-- 生成时间: 2026-06-15
-- 数据库: dh-oa (localhost:3306)
-- 说明: 重构差旅申请单表结构, 新增行程明细子表, 清理费用报销表无用字段
-- =====================================================

-- =====================================================
-- 第一部分: 差旅申请单 (oa_travel_apply_bill) 重构
-- =====================================================

-- 1.1 删除不再使用的字段
ALTER TABLE `oa_travel_apply_bill`
    DROP COLUMN `destination`,
    DROP COLUMN `transport_type`,
    DROP COLUMN `accommodation_type`,
    DROP COLUMN `budget_detail`,
    DROP COLUMN `travel_members`,
    DROP COLUMN `is_overseas`,
    DROP COLUMN `overseas_remark`;

-- 1.2 重命名: budget_amount -> estimated_cost (预计费用)
ALTER TABLE `oa_travel_apply_bill`
    CHANGE COLUMN `budget_amount` `estimated_cost` decimal(15,2) DEFAULT '0.00' COMMENT '预计费用';

-- 1.3 修改 travel_days 字段类型：INT -> DECIMAL(5,1)，支持1位小数
ALTER TABLE `oa_travel_apply_bill`
    MODIFY COLUMN `travel_days` decimal(5,1) DEFAULT NULL COMMENT '出差天数（支持1位小数）';

-- 1.4 新增字段: companion (同行人), reimbursement_status (报销状态)
ALTER TABLE `oa_travel_apply_bill`
    ADD COLUMN `companion` varchar(500) DEFAULT NULL COMMENT '同行人' AFTER `travel_days`,
    ADD COLUMN `reimbursement_status` tinyint DEFAULT '0' COMMENT '报销状态（0未报销 1已报销）' AFTER `estimated_cost`;

-- 1.5 删除 is_overseas 索引 (字段删除时已自动删除), 新增 reimbursement_status 索引
ALTER TABLE `oa_travel_apply_bill`
    ADD INDEX `idx_reimbursement_status` (`reimbursement_status`);


-- =====================================================
-- 第二部分: 新建行程明细子表 (oa_travel_itinerary)
-- =====================================================

CREATE TABLE IF NOT EXISTS `oa_travel_itinerary` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `bill_id` bigint NOT NULL COMMENT '差旅申请单ID',
    `departure_city` varchar(100) DEFAULT NULL COMMENT '出发城市',
    `destination_city` varchar(100) DEFAULT NULL COMMENT '到达城市',
    `start_date` datetime DEFAULT NULL COMMENT '开始日期',
    `end_date` datetime DEFAULT NULL COMMENT '结束日期',
    `transport_type` tinyint DEFAULT NULL COMMENT '交通方式（1火车 2飞机 3自驾 4公务用车 5其他）',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `sort_order` int DEFAULT '0' COMMENT '排序',
    `creator` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_bill_id` (`bill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='差旅行程明细';


-- =====================================================
-- 第三部分: 费用报销单 (oa_expense_reimburse_bill) 清理无用字段
-- =====================================================

-- 注意：保留 travel_bill_code 字段（用于存储关联的出差申请单号，支持多个逗号分隔）
-- 删除不再需要的冗余字段（出差事由、日期、天数现在从关联的差旅申请单中查询）
ALTER TABLE `oa_expense_reimburse_bill`
    DROP COLUMN `travel_cause`,
    DROP COLUMN `start_date`,
    DROP COLUMN `end_date`,
    DROP COLUMN `travel_days`;


-- =====================================================
-- 迁移完成
-- =====================================================
