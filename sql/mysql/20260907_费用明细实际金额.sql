-- 付款申请费用明细：实际金额（合计按此汇总；预算金额仍用 amount）
ALTER TABLE `edu_activity_instance_fee_item`
  ADD COLUMN `actual_amount` decimal(18, 2) NULL DEFAULT NULL COMMENT '实际金额（付款申请填写）' AFTER `amount`;
