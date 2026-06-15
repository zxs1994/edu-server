-- =====================================================
-- 车辆管理和用印管理: 移除公司相关字段
-- 日期: 2026-06-15
-- 说明:
--   车辆管理和用印管理模块不再需要公司维度的数据隔离，
--   移除所有表中的 company_id 和 company_name 字段及相关索引。
-- =====================================================

-- ==================== 车辆管理 ====================

-- 1. oa_car（车辆信息表）: 删除 company_id / company_name 列
ALTER TABLE `oa_car`
    DROP COLUMN `company_id`,
    DROP COLUMN `company_name`;

-- 2. oa_car_apply_bill（用车申请单）: 删除相关索引和列
ALTER TABLE `oa_car_apply_bill`
    DROP INDEX `idx_oa_car_apply_bill_company_returned`,
    DROP COLUMN `company_id`,
    DROP COLUMN `company_name`;

-- 3. oa_car_return_bill（还车申请单）: 删除 company_id / company_name 列
ALTER TABLE `oa_car_return_bill`
    DROP COLUMN `company_id`,
    DROP COLUMN `company_name`;

-- ==================== 用印管理 ====================

-- 4. oa_seal（印章信息表）: 删除相关索引和列
ALTER TABLE `oa_seal`
    DROP INDEX `idx_company_id`,
    DROP COLUMN `company_id`,
    DROP COLUMN `company_name`;

-- 5. oa_seal_apply_bill（用印申请单）: 删除相关索引和列
ALTER TABLE `oa_seal_apply_bill`
    DROP INDEX `idx_company_id`,
    DROP COLUMN `company_id`,
    DROP COLUMN `company_name`;
