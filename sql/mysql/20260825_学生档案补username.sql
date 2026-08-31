-- ============================================================
-- 学生档案补充用户账号字段 username
-- 已有数据：默认用学号回填
-- ============================================================

ALTER TABLE `edu_student`
  ADD COLUMN `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户账号' AFTER `student_no`;

UPDATE `edu_student`
SET `username` = `student_no`
WHERE `username` IS NULL OR `username` = '';

ALTER TABLE `edu_student`
  MODIFY COLUMN `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号';
