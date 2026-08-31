-- ============================================================
-- 教培档案：移除用户账号联合唯一索引
-- 唯一性改由应用层校验（TeacherServiceImpl.validateUsernameUnique）
-- ============================================================

ALTER TABLE `edu_teacher` DROP INDEX `uk_username`;

ALTER TABLE `edu_teacher` ADD INDEX `idx_username`(`username` ASC) USING BTREE;
