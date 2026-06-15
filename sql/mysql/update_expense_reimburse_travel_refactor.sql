-- ================================================================
-- 差旅报销单 数据库迁移脚本
-- 适用于正式环境部署
-- 日期: 2026-06-15
-- 说明: 将通用费用报销单重构为差旅报销单，新增费用明细子表字段
-- ================================================================

-- ================================================================
-- 第一部分: oa_expense_reimburse_bill (费用报销单主表) 结构变更
-- ================================================================

-- 1.1 新增差旅相关字段
ALTER TABLE `oa_expense_reimburse_bill`
ADD COLUMN `travel_bill_code` varchar(50) DEFAULT NULL COMMENT '关联出差申请单号' AFTER `process_status`,
ADD COLUMN `travel_cause` varchar(500) DEFAULT NULL COMMENT '出差事由' AFTER `travel_bill_code`,
ADD COLUMN `start_date` date DEFAULT NULL COMMENT '开始日期' AFTER `travel_cause`,
ADD COLUMN `end_date` date DEFAULT NULL COMMENT '结束日期' AFTER `start_date`,
ADD COLUMN `travel_days` decimal(5,1) DEFAULT NULL COMMENT '出差天数' AFTER `end_date`,
ADD COLUMN `payment_status` tinyint DEFAULT 0 COMMENT '支付状态（0未支付 1已支付）' AFTER `total_amount`;

-- 1.2 删除不再使用的通用费用字段
ALTER TABLE `oa_expense_reimburse_bill`
DROP COLUMN `expense_type`,
DROP COLUMN `expense_date`,
DROP COLUMN `expense_description`,
DROP COLUMN `payment_method`,
DROP COLUMN `bank_account`,
DROP COLUMN `bank_name`,
DROP COLUMN `is_large_amount`,
DROP COLUMN `large_amount_remark`,
DROP COLUMN `cause`;

-- ================================================================
-- 第二部分: oa_expense_reimburse_detail (报销费用明细表) 结构变更
-- ================================================================

-- 2.1 重命名字段以匹配新的差旅费用明细结构
ALTER TABLE `oa_expense_reimburse_detail`
CHANGE COLUMN `expense_item` `expense_type` varchar(200) NOT NULL COMMENT '费用类型（交通费/住宿费等）',
CHANGE COLUMN `invoice_no` `departure` varchar(200) DEFAULT NULL COMMENT '出发地',
CHANGE COLUMN `invoice_date` `destination` varchar(200) DEFAULT NULL COMMENT '到达地';

-- 2.2 新增排序字段
ALTER TABLE `oa_expense_reimburse_detail`
ADD COLUMN `sort_order` int DEFAULT 0 COMMENT '排序' AFTER `description`;

-- ================================================================
-- 验证: 执行以下语句确认变更结果
-- ================================================================

-- DESCRIBE `oa_expense_reimburse_bill`;
-- DESCRIBE `oa_expense_reimburse_detail`;
