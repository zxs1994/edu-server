-- 报销明细表新增交通工具、单据张数（日常报销/差旅报销共用）
ALTER TABLE `oa_expense_reimburse_detail`
    ADD COLUMN `transport_type` tinyint NULL COMMENT '交通工具（字典 oa_transport_type）' AFTER `destination`,
    ADD COLUMN `receipt_count` int NULL COMMENT '单据张数' AFTER `description`;

-- executed: 2026-07-15
