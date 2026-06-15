-- ============================================================
-- 公文发文单：删除已废弃的冗余字段
-- 删除列：doc_type、cause、recipients、cc_list、is_important
-- 删除索引：idx_doc_type、idx_is_important（引用了即将删除的列）
-- 执行顺序：在 update_document_dispatch_add_template.sql
--           和 update_document_dispatch_add_secrecy_level.sql 之后
-- 日期: 2026-06-15
-- ============================================================

ALTER TABLE `oa_document_dispatch_bill`
    DROP INDEX `idx_doc_type`,
    DROP INDEX `idx_is_important`,
    DROP COLUMN `doc_type`,
    DROP COLUMN `recipients`,
    DROP COLUMN `cc_list`,
    DROP COLUMN `is_important`,
    DROP COLUMN `cause`;
