-- 差旅申请单：出行人数（含本人），出境必填
ALTER TABLE `oa_travel_apply_bill`
    ADD COLUMN `traveler_count` int NULL COMMENT '出行人数（含本人）' AFTER `companion`;

-- executed: 2026-07-15
