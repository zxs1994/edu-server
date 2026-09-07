-- ============================================================
-- 专项活动 - 执行实例反馈表（学生 / 教师 / 班务评价）
-- 唯一性：应用层校验（未删除记录 instance+user / instance 不重复），不用联合唯一索引
-- 原因：软删除场景下 (xxx, deleted, tenant_id) 会导致同一键无法二次删除
-- ============================================================

CREATE TABLE IF NOT EXISTS `edu_activity_instance_student_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `instance_id` bigint NOT NULL COMMENT '执行实例ID',
  `user_id` bigint NOT NULL COMMENT '学生用户ID（system_users.id）',
  `satisfaction` tinyint NOT NULL COMMENT '满意度（1-5）',
  `harvest` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收获',
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文字评价',
  `submit_time` datetime NOT NULL COMMENT '提交时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_instance_user`(`instance_id` ASC, `user_id` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动执行实例-学生反馈';

CREATE TABLE IF NOT EXISTS `edu_activity_instance_teacher_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `instance_id` bigint NOT NULL COMMENT '执行实例ID',
  `user_id` bigint NOT NULL COMMENT '教培用户ID（system_users.id）',
  `summary` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行总结',
  `problem` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '问题',
  `suggestion` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '建议',
  `submit_time` datetime NOT NULL COMMENT '提交时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_instance_user`(`instance_id` ASC, `user_id` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动执行实例-教师反馈';

CREATE TABLE IF NOT EXISTS `edu_activity_instance_admin_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `instance_id` bigint NOT NULL COMMENT '执行实例ID',
  `submitter_user_id` bigint NOT NULL COMMENT '提交人用户ID',
  `quality_score` tinyint NOT NULL COMMENT '质量评分（1-5）',
  `closing_opinion` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '结项意见',
  `submit_time` datetime NOT NULL COMMENT '提交时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_instance_id`(`instance_id` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动执行实例-班务评价';
