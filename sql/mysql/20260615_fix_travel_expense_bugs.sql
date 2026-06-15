-- =====================================================
-- 差旅模块 增量迁移脚本
-- 日期: 2026-06-15
-- 数据库: dh-oa (localhost:3306)
-- 说明: 本次变更对应前端4个Bug修复的数据库调整
--   1. oa_travel_apply_bill.travel_days 从 int 改为 decimal(5,1)，支持1位小数
--   2. oa_expense_reimburse_bill 删除冗余的差旅信息字段（改为从关联单据查询）
-- =====================================================

-- 1. 差旅申请单: travel_days 支持1位小数
ALTER TABLE `oa_travel_apply_bill`
    MODIFY COLUMN `travel_days` decimal(5,1) DEFAULT NULL COMMENT '出差天数（支持1位小数）';

-- 2. 费用报销单: 删除冗余字段（出差事由/日期/天数改为从关联差旅申请单查询展示）
--    保留 travel_bill_code 用于存储关联的出差申请单号（支持多个逗号分隔）
ALTER TABLE `oa_expense_reimburse_bill`
    DROP COLUMN `travel_cause`,
    DROP COLUMN `start_date`,
    DROP COLUMN `end_date`,
    DROP COLUMN `travel_days`;

-- 3. 清理因 Jackson 反序列化 bug 导致的 epoch 脏数据（1970-01-01 08:00:00 = timestamp 0 in GMT+8）
UPDATE `oa_travel_apply_bill`
    SET `travel_start_date` = NULL,
        `travel_end_date` = NULL,
        `travel_days` = NULL
    WHERE `travel_start_date` = '1970-01-01 08:00:00';

-- 4. 新增报销支付状态字典（前端列表使用 CellDict 渲染）
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES ('OA 报销支付状态', 'oa_expense_payment_status', 0, '费用报销单支付状态', '1', NOW(), '1', NOW(), 0);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(1, '未支付', '0', 'oa_expense_payment_status', 0, 'default', '1', NOW(), '1', NOW(), 0),
(2, '已支付', '1', 'oa_expense_payment_status', 0, 'success', '1', NOW(), '1', NOW(), 0);
