-- 专项活动 H5 报名短链映射（6 位码，不依赖 Redis）
CREATE TABLE IF NOT EXISTS `edu_activity_enroll_link` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `code` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短码',
  `user_id` bigint NOT NULL COMMENT '学生用户ID',
  `instance_id` bigint NOT NULL COMMENT '活动实例ID',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间（已废弃，短码不过期）',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC, `deleted` ASC) USING BTREE,
  INDEX `idx_user_instance`(`user_id` ASC, `instance_id` ASC, `tenant_id` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专项活动H5报名短链';
