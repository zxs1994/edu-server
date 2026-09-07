-- ============================================================
-- 专项活动 - 学生侧按人头费用明细拆分说明
-- 旧逻辑：按人头学生侧生成 1 条汇总（quantity=出勤人数，payee 为空）
-- 新逻辑：按人头学生侧一人一条（quantity=1，payee=该学生）
--
-- 已结项实例：打开「费用明细」Tab 时后端会自动修复（删除无收款人的汇总行并拆分）
-- 本 SQL 仅作人工兜底，一般无需执行。
-- ============================================================

-- 查看待修复的汇总行（学生 + 按人头 + 无收款人）
-- SELECT id, fee_code, instance_id, quantity, amount, status
-- FROM edu_activity_instance_fee_item
-- WHERE fee_side = 'student' AND fee_mode = 'head' AND payee_user_id IS NULL AND deleted = b'0';

-- 不建议直接手工 DELETE；请重启后端后打开对应实例「费用明细」Tab 触发自动拆分。
