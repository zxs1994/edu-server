-- ============================================================
-- 付款申请明细规则：
-- 1. 审批拒绝 → 明细 status=rejected，保留 payment_request_id
-- 2. 已驳回明细仅原付款单可选，别的单不可选
-- 若曾执行过「全部 rejected→pending」的旧脚本，用本脚本尽量挂回
-- ============================================================

-- 仍挂在驳回/取消单上的明细：确保为 rejected
UPDATE `edu_activity_instance_fee_item` f
INNER JOIN `edu_activity_payment_request` r
        ON f.`payment_request_id` = r.`id` AND r.`deleted` = b'0'
SET f.`status` = 'rejected',
    f.`update_time` = NOW()
WHERE f.`deleted` = b'0'
  AND r.`process_status` IN (3, 4)
  AND f.`status` <> 'paid';

-- 已解绑但仍在驳回单 fee_item_ids 快照中的：挂回并标 rejected
UPDATE `edu_activity_instance_fee_item` f
INNER JOIN `edu_activity_payment_request` r
        ON r.`deleted` = b'0'
       AND r.`process_status` IN (3, 4)
       AND r.`fee_item_ids` IS NOT NULL
       AND r.`fee_item_ids` <> ''
       AND (
            r.`fee_item_ids` = CONCAT('[', f.`id`, ']')
            OR r.`fee_item_ids` LIKE CONCAT('[', f.`id`, ',%')
            OR r.`fee_item_ids` LIKE CONCAT('%,', f.`id`, ',%')
            OR r.`fee_item_ids` LIKE CONCAT('%,', f.`id`, ']')
       )
SET f.`status` = 'rejected',
    f.`payment_request_id` = r.`id`,
    f.`update_time` = NOW()
WHERE f.`deleted` = b'0'
  AND f.`payment_request_id` IS NULL
  AND f.`status` IN ('pending_request', 'rejected');
