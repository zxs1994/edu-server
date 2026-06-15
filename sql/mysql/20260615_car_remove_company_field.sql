-- =====================================================
-- 车辆信息模块: 移除前端公司选择，后端自动设置顶级组织
-- 日期: 2026-06-15
-- 数据库: dh-oa (localhost:3306)
-- 说明:
--   1. oa_car 表的 company_id / company_name 改为可空（由后端自动填充顶级组织）
--   2. 将"中国引航协会"(id=200)的 org_type 从 '0'(部门) 更新为 '1'(公司)，
--      使其能被识别为顶级组织
-- =====================================================

-- 1. oa_car: company_id 和 company_name 允许为空（后端自动设置）
ALTER TABLE `oa_car`
    MODIFY COLUMN `company_id` bigint DEFAULT NULL COMMENT '公司ID',
    MODIFY COLUMN `company_name` varchar(64) DEFAULT NULL COMMENT '公司名称';

-- 2. 将"中国引航协会"设置为顶级组织（公司类型）
UPDATE `system_dept`
SET `org_type` = '1'
WHERE `id` = 200 AND `name` = '中国引航协会' AND `parent_id` = 0;
