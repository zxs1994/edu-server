-- 差旅报销单：人数（含本人），补贴领取人数
ALTER TABLE `oa_expense_reimburse_bill`
    ADD COLUMN `traveler_count` int NULL COMMENT '人数（含本人），补贴领取人数' AFTER `total_amount`;

-- executed: 2026-07-16
