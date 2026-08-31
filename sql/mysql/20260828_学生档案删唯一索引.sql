-- ============================================================
-- 学生档案：移除学号/用户账号联合唯一索引
-- 原因：软删除场景下 (student_no, deleted, tenant_id) 导致同一学号无法二次删除
-- 唯一性改由应用层校验（StudentServiceImpl.validateStudentNoUnique / validateUsernameUnique）
-- ============================================================

ALTER TABLE `edu_student` DROP INDEX `uk_student_no`;
ALTER TABLE `edu_student` DROP INDEX `uk_username`;

-- 保留普通索引，便于按学号/账号查询
ALTER TABLE `edu_student` ADD INDEX `idx_student_no`(`student_no` ASC) USING BTREE;
ALTER TABLE `edu_student` ADD INDEX `idx_username`(`username` ASC) USING BTREE;
