-- ============================================================
-- 合同编号序列表（审批通过后自动生成合同编号）
-- 编号规则：yyyyCMPA-T-nnn
-- 流水维度：年度 + 类型（year + type_code）
-- 说明：
-- 1) 每年每类型从 001 开始递增；
-- 2) 作废/终止不回收流水号；
-- 3) 本脚本仅创建序列表，不做历史数据补号。
-- ============================================================

CREATE TABLE IF NOT EXISTS `oa_contract_code_seq` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `biz_year` int NOT NULL COMMENT '业务年度(yyyy)',
  `type_code` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '合同类型码(X/C/F/H/Z/Q)',
  `current_seq` int NOT NULL DEFAULT 0 COMMENT '当前已分配最大流水号',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_year_type` (`biz_year`, `type_code`) USING BTREE,
  CONSTRAINT `chk_current_seq_range` CHECK (`current_seq` >= 0 AND `current_seq` <= 999)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='合同编号序列表(按年度+类型分段)';

-- 已执行标记（执行后取消注释）
-- -- executed: 2026-07-07 合同编号序列表
