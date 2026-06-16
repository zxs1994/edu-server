-- =====================================================
-- 工作台徽标已读改造（水位线方案）- 数据库迁移脚本
-- 新建 bpm_workbench_read 表，存储用户各 Tab 的最后查看时间
-- =====================================================

CREATE TABLE IF NOT EXISTS `bpm_workbench_read` (
  `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`        bigint       NOT NULL COMMENT '用户编号',
  `tab_key`        varchar(32)  NOT NULL COMMENT 'Tab标识(todo/myBill/done/copy)',
  `last_view_time` datetime     NOT NULL COMMENT '最后查看时间',
  `creator`        varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater`        varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id`      bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tab` (`user_id`, `tab_key`, `deleted`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作台Tab已读水位线';
