-- ============================================================
-- 中国引航协会OA系统 - 菜单权限和字典数据
-- 数据库: ruoyi-office
-- 日期: 2026-06-09
-- ============================================================

-- ============ 菜单数据 ============
-- type: 1=目录, 2=菜单, 3=按钮
-- visible: 1=可见, 0=隐藏
-- keep_alive: 1=缓存, 0=不缓存

-- 审批管理（目录）
INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5300, '审批管理', 1, 5, 5013, 'approval', 'ant-design:audit-outlined', NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5300);

-- === 合同管理 ===
-- 中间目录（隐藏，保持路由兼容性）
INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5301, '合同管理', 1, 1, 5300, 'contract', 'ant-design:file-text-outlined', NULL, NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5301);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5302, '合同审批列表', 2, 1, 5300, 'contract-bill-list', NULL, 'oa/contract/list/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5302);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5303, '合同审批详情', 2, 2, 5300, 'contract-bill-info', NULL, 'oa/contract/info/index', NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5303);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, creator, create_time, updater, update_time, deleted) VALUES
(5304, '合同查询', 'oa:contract-bill:query', 3, 1, 5302, '1', NOW(), '1', NOW(), 0),
(5305, '合同新增', 'oa:contract-bill:create', 3, 2, 5302, '1', NOW(), '1', NOW(), 0),
(5306, '合同修改', 'oa:contract-bill:update', 3, 3, 5302, '1', NOW(), '1', NOW(), 0),
(5307, '合同删除', 'oa:contract-bill:delete', 3, 4, 5302, '1', NOW(), '1', NOW(), 0),
(5308, '合同导出', 'oa:contract-bill:export', 3, 5, 5302, '1', NOW(), '1', NOW(), 0),
(5309, '合同提交', 'oa:contract-bill:submit', 3, 6, 5302, '1', NOW(), '1', NOW(), 0),
(5310, '合同撤回', 'oa:contract-bill:withdraw', 3, 7, 5302, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE name=VALUES(name), permission=VALUES(permission);

-- === 公文发文 ===
-- 中间目录（隐藏，保持路由兼容性）
INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5311, '公文发文', 1, 2, 5300, 'document', 'ant-design:file-word-outlined', NULL, NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5311);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5312, '公文发文列表', 2, 1, 5300, 'document-dispatch-list', NULL, 'oa/document/list/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5312);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5313, '公文发文详情', 2, 2, 5300, 'document-dispatch-info', NULL, 'oa/document/info/index', NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5313);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, creator, create_time, updater, update_time, deleted) VALUES
(5314, '公文查询', 'oa:document-dispatch-bill:query', 3, 1, 5312, '1', NOW(), '1', NOW(), 0),
(5315, '公文新增', 'oa:document-dispatch-bill:create', 3, 2, 5312, '1', NOW(), '1', NOW(), 0),
(5316, '公文修改', 'oa:document-dispatch-bill:update', 3, 3, 5312, '1', NOW(), '1', NOW(), 0),
(5317, '公文删除', 'oa:document-dispatch-bill:delete', 3, 4, 5312, '1', NOW(), '1', NOW(), 0),
(5318, '公文导出', 'oa:document-dispatch-bill:export', 3, 5, 5312, '1', NOW(), '1', NOW(), 0),
(5319, '公文提交', 'oa:document-dispatch-bill:submit', 3, 6, 5312, '1', NOW(), '1', NOW(), 0),
(5320, '公文撤回', 'oa:document-dispatch-bill:withdraw', 3, 7, 5312, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE name=VALUES(name), permission=VALUES(permission);

-- === 费用报销 ===
-- 中间目录（隐藏，保持路由兼容性）
INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5321, '费用报销', 1, 3, 5300, 'expense', 'ant-design:money-collect-outlined', NULL, NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5321);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5322, '费用报销列表', 2, 1, 5300, 'expense-reimburse-list', NULL, 'oa/expense/list/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5322);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5323, '费用报销详情', 2, 2, 5300, 'expense-reimburse-info', NULL, 'oa/expense/info/index', NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5323);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, creator, create_time, updater, update_time, deleted) VALUES
(5324, '报销查询', 'oa:expense-reimburse-bill:query', 3, 1, 5322, '1', NOW(), '1', NOW(), 0),
(5325, '报销新增', 'oa:expense-reimburse-bill:create', 3, 2, 5322, '1', NOW(), '1', NOW(), 0),
(5326, '报销修改', 'oa:expense-reimburse-bill:update', 3, 3, 5322, '1', NOW(), '1', NOW(), 0),
(5327, '报销删除', 'oa:expense-reimburse-bill:delete', 3, 4, 5322, '1', NOW(), '1', NOW(), 0),
(5328, '报销导出', 'oa:expense-reimburse-bill:export', 3, 5, 5322, '1', NOW(), '1', NOW(), 0),
(5329, '报销提交', 'oa:expense-reimburse-bill:submit', 3, 6, 5322, '1', NOW(), '1', NOW(), 0),
(5330, '报销撤回', 'oa:expense-reimburse-bill:withdraw', 3, 7, 5322, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE name=VALUES(name), permission=VALUES(permission);

-- === 立项管理 ===
-- 中间目录（隐藏，保持路由兼容性）
INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5331, '立项管理', 1, 4, 5300, 'project', 'ant-design:project-outlined', NULL, NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5331);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5332, '立项列表', 2, 1, 5300, 'project-initiation-list', NULL, 'oa/project/list/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5332);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5333, '立项详情', 2, 2, 5300, 'project-initiation-info', NULL, 'oa/project/info/index', NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5333);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, creator, create_time, updater, update_time, deleted) VALUES
(5334, '立项查询', 'oa:project-initiation-bill:query', 3, 1, 5332, '1', NOW(), '1', NOW(), 0),
(5335, '立项新增', 'oa:project-initiation-bill:create', 3, 2, 5332, '1', NOW(), '1', NOW(), 0),
(5336, '立项修改', 'oa:project-initiation-bill:update', 3, 3, 5332, '1', NOW(), '1', NOW(), 0),
(5337, '立项删除', 'oa:project-initiation-bill:delete', 3, 4, 5332, '1', NOW(), '1', NOW(), 0),
(5338, '立项导出', 'oa:project-initiation-bill:export', 3, 5, 5332, '1', NOW(), '1', NOW(), 0),
(5339, '立项提交', 'oa:project-initiation-bill:submit', 3, 6, 5332, '1', NOW(), '1', NOW(), 0),
(5340, '立项撤回', 'oa:project-initiation-bill:withdraw', 3, 7, 5332, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE name=VALUES(name), permission=VALUES(permission);

-- === 收文办理 ===
-- 中间目录（隐藏，保持路由兼容性）
INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5341, '收文办理', 1, 5, 5300, 'incoming', 'ant-design:inbox-outlined', NULL, NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5341);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5342, '收文办理列表', 2, 1, 5300, 'incoming-document-list', NULL, 'oa/incoming/list/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5342);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5343, '收文办理详情', 2, 2, 5300, 'incoming-document-info', NULL, 'oa/incoming/info/index', NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5343);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, creator, create_time, updater, update_time, deleted) VALUES
(5344, '收文查询', 'oa:incoming-document-bill:query', 3, 1, 5342, '1', NOW(), '1', NOW(), 0),
(5345, '收文新增', 'oa:incoming-document-bill:create', 3, 2, 5342, '1', NOW(), '1', NOW(), 0),
(5346, '收文修改', 'oa:incoming-document-bill:update', 3, 3, 5342, '1', NOW(), '1', NOW(), 0),
(5347, '收文删除', 'oa:incoming-document-bill:delete', 3, 4, 5342, '1', NOW(), '1', NOW(), 0),
(5348, '收文导出', 'oa:incoming-document-bill:export', 3, 5, 5342, '1', NOW(), '1', NOW(), 0),
(5349, '收文提交', 'oa:incoming-document-bill:submit', 3, 6, 5342, '1', NOW(), '1', NOW(), 0),
(5350, '收文撤回', 'oa:incoming-document-bill:withdraw', 3, 7, 5342, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE name=VALUES(name), permission=VALUES(permission);

-- === 差旅申请 ===
-- 中间目录（隐藏，保持路由兼容性）
INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5351, '差旅申请', 1, 6, 5300, 'travel', 'ant-design:plane-outlined', NULL, NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5351);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5352, '差旅申请列表', 2, 1, 5300, 'travel-apply-list', NULL, 'oa/travel/list/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5352);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5353, '差旅申请详情', 2, 2, 5300, 'travel-apply-info', NULL, 'oa/travel/info/index', NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5353);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, creator, create_time, updater, update_time, deleted) VALUES
(5354, '差旅查询', 'oa:travel-apply-bill:query', 3, 1, 5352, '1', NOW(), '1', NOW(), 0),
(5355, '差旅新增', 'oa:travel-apply-bill:create', 3, 2, 5352, '1', NOW(), '1', NOW(), 0),
(5356, '差旅修改', 'oa:travel-apply-bill:update', 3, 3, 5352, '1', NOW(), '1', NOW(), 0),
(5357, '差旅删除', 'oa:travel-apply-bill:delete', 3, 4, 5352, '1', NOW(), '1', NOW(), 0),
(5358, '差旅导出', 'oa:travel-apply-bill:export', 3, 5, 5352, '1', NOW(), '1', NOW(), 0),
(5359, '差旅提交', 'oa:travel-apply-bill:submit', 3, 6, 5352, '1', NOW(), '1', NOW(), 0),
(5360, '差旅撤回', 'oa:travel-apply-bill:withdraw', 3, 7, 5352, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE name=VALUES(name), permission=VALUES(permission);

-- === 纠错管理 ===
INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5370, '纠错管理', 1, 99, 5013, 'correction', 'ant-design:warning-outlined', NULL, NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5370);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5371, '纠错列表', 2, 1, 5370, 'correction-list', NULL, 'oa/correction/list/index', NULL, 0, 1, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5371);

INSERT INTO system_menu (id, name, type, sort, parent_id, path, icon, component, component_name, status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
SELECT 5372, '纠错详情', 2, 2, 5370, 'correction-info', NULL, 'oa/correction/info/index', NULL, 0, 0, 1, 1, '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_menu WHERE id = 5372);

INSERT INTO system_menu (id, name, permission, type, sort, parent_id, creator, create_time, updater, update_time, deleted) VALUES
(5373, '纠错查询', 'oa:correction-bill:query', 3, 1, 5370, '1', NOW(), '1', NOW(), 0),
(5374, '纠错新增', 'oa:correction-bill:create', 3, 2, 5370, '1', NOW(), '1', NOW(), 0),
(5375, '纠错修改', 'oa:correction-bill:update', 3, 3, 5370, '1', NOW(), '1', NOW(), 0),
(5376, '纠错删除', 'oa:correction-bill:delete', 3, 4, 5370, '1', NOW(), '1', NOW(), 0),
(5377, '纠错提交', 'oa:correction-bill:submit', 3, 5, 5370, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE name=VALUES(name), permission=VALUES(permission);

-- === 角色-菜单权限（super_admin 拥有所有新菜单） ===
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 1, id, '1', NOW(), '1', NOW(), 0, 1 FROM system_menu WHERE id BETWEEN 5300 AND 5400 AND deleted = 0;

-- === 会长角色拥有所有审批模块菜单 ===
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 201, id, '1', NOW(), '1', NOW(), 0, 1 FROM system_menu WHERE id BETWEEN 5300 AND 5400 AND deleted = 0;

-- === 秘书长角色拥有审批管理菜单 ===
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 202, id, '1', NOW(), '1', NOW(), 0, 1 FROM system_menu WHERE id BETWEEN 5300 AND 5400 AND deleted = 0;

-- === 常务副秘书长角色 ===
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 203, id, '1', NOW(), '1', NOW(), 0, 1 FROM system_menu WHERE id BETWEEN 5300 AND 5400 AND deleted = 0;

-- === 副秘书长角色 ===
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 204, id, '1', NOW(), '1', NOW(), 0, 1 FROM system_menu WHERE id BETWEEN 5300 AND 5400 AND deleted = 0;

-- === 办公室主任角色 ===
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 205, id, '1', NOW(), '1', NOW(), 0, 1 FROM system_menu WHERE id BETWEEN 5300 AND 5400 AND deleted = 0;

-- === 财务角色 ===
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 206, id, '1', NOW(), '1', NOW(), 0, 1 FROM system_menu WHERE id BETWEEN 5300 AND 5400 AND deleted = 0;

-- === 秘书角色 ===
INSERT IGNORE INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 207, id, '1', NOW(), '1', NOW(), 0, 1 FROM system_menu WHERE id BETWEEN 5300 AND 5400 AND deleted = 0;


-- ============ 字典数据 ============

-- 合同类型
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2100, 'OA 合同类型', 'oa_contract_type', 0, '合同审批-合同类型', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2100);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3401, 1, '采购合同', '1', 'oa_contract_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3402, 2, '销售合同', '2', 'oa_contract_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3403, 3, '服务合同', '3', 'oa_contract_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3404, 4, '合作协议', '4', 'oa_contract_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3405, 5, '其他',     '5', 'oa_contract_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 公文类型
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2101, 'OA 公文类型', 'oa_doc_type', 0, '公文发文-公文类型', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2101);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3411, 1, '通知', '1', 'oa_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3412, 2, '公告', '2', 'oa_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3413, 3, '报告', '3', 'oa_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3414, 4, '请示', '4', 'oa_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3415, 5, '批复', '5', 'oa_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3416, 6, '函',   '6', 'oa_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3417, 7, '纪要', '7', 'oa_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3418, 8, '其他', '8', 'oa_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 紧急程度
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2102, 'OA 紧急程度', 'oa_urgency_level', 0, '紧急程度', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2102);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3421, 0, '普通', '0', 'oa_urgency_level', 0, 'success', '', NULL, '1', NOW(), '1', NOW(), 0),
(3422, 1, '紧急', '1', 'oa_urgency_level', 0, 'warning', '', NULL, '1', NOW(), '1', NOW(), 0),
(3423, 2, '特急', '2', 'oa_urgency_level', 0, 'danger',  '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 费用类型
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2103, 'OA 费用类型', 'oa_expense_type', 0, '费用报销-费用类型', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2103);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3431, 1, '办公用品', '1', 'oa_expense_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3432, 2, '交通',     '2', 'oa_expense_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3433, 3, '餐饮',     '3', 'oa_expense_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3434, 4, '通讯',     '4', 'oa_expense_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3435, 5, '差旅',     '5', 'oa_expense_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3436, 6, '会议',     '6', 'oa_expense_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3437, 7, '招待',     '7', 'oa_expense_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3438, 8, '其他',     '8', 'oa_expense_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 付款方式
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2104, 'OA 付款方式', 'oa_payment_method', 0, '费用报销-付款方式', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2104);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3441, 1, '银行转账', '1', 'oa_payment_method', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3442, 2, '现金',     '2', 'oa_payment_method', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3443, 3, '支票',     '3', 'oa_payment_method', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 项目类型
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2105, 'OA 项目类型', 'oa_project_type', 0, '立项管理-项目类型', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2105);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3451, 1, '活动', '1', 'oa_project_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3452, 2, '项目', '2', 'oa_project_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3453, 3, '课题', '3', 'oa_project_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3454, 4, '其他', '4', 'oa_project_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 来文类型
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2106, 'OA 来文类型', 'oa_incoming_doc_type', 0, '收文办理-来文类型', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2106);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3461, 1, '上级文件', '1', 'oa_incoming_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3462, 2, '平级文件', '2', 'oa_incoming_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3463, 3, '下级文件', '3', 'oa_incoming_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3464, 4, '群众来信', '4', 'oa_incoming_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3465, 5, '其他',     '5', 'oa_incoming_doc_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 办理状态
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2107, 'OA 办理状态', 'oa_handling_status', 0, '收文办理-办理状态', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2107);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3471, 0, '待办理', '0', 'oa_handling_status', 0, 'info',    '', NULL, '1', NOW(), '1', NOW(), 0),
(3472, 1, '办理中', '1', 'oa_handling_status', 0, 'warning', '', NULL, '1', NOW(), '1', NOW(), 0),
(3473, 2, '已办结', '2', 'oa_handling_status', 0, 'success', '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 交通方式
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2108, 'OA 交通方式', 'oa_transport_type', 0, '差旅申请-交通方式', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2108);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3481, 1, '火车',     '1', 'oa_transport_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3482, 2, '飞机',     '2', 'oa_transport_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3483, 3, '自驾',     '3', 'oa_transport_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3484, 4, '公务用车', '4', 'oa_transport_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3485, 5, '其他',     '5', 'oa_transport_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 住宿方式
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2109, 'OA 住宿方式', 'oa_accommodation_type', 0, '差旅申请-住宿方式', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2109);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3491, 1, '酒店',   '1', 'oa_accommodation_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3492, 2, '招待所', '2', 'oa_accommodation_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3493, 3, '其他',   '3', 'oa_accommodation_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 纠错-原单据类型
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2110, 'OA 纠错单据类型', 'oa_correction_bill_type', 0, '纠错管理-原单据类型', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2110);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3501, 1, '合同审批',   'oa_contract_bill',            'oa_correction_bill_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3502, 2, '公文发文',   'oa_document_dispatch_bill',   'oa_correction_bill_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3503, 3, '费用报销',   'oa_expense_reimburse_bill',   'oa_correction_bill_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3504, 4, '项目立项',   'oa_project_initiation_bill',  'oa_correction_bill_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3505, 5, '用印申请',   'oa_seal_apply_bill',          'oa_correction_bill_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3506, 6, '收文办理',   'oa_incoming_document_bill',   'oa_correction_bill_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0),
(3507, 7, '差旅申请',   'oa_travel_apply_bill',        'oa_correction_bill_type', 0, '', '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 纠错-冻结状态
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2111, 'OA 冻结状态', 'oa_freeze_status', 0, '纠错管理-冻结状态', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2111);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3511, 0, '未冻结', '0', 'oa_freeze_status', 0, 'success', '', NULL, '1', NOW(), '1', NOW(), 0),
(3512, 1, '已冻结', '1', 'oa_freeze_status', 0, 'danger',  '', NULL, '1', NOW(), '1', NOW(), 0),
(3513, 2, '已解冻', '2', 'oa_freeze_status', 0, 'info',    '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);

-- 纠错-纠错状态
INSERT INTO system_dict_type (id, name, type, status, remark, creator, create_time, updater, update_time, deleted)
SELECT 2112, 'OA 纠错状态', 'oa_correction_status', 0, '纠错管理-纠错状态', '1', NOW(), '1', NOW(), 0
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dict_type WHERE id = 2112);

INSERT INTO system_dict_data (id, sort, label, value, dict_type, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(3521, 0, '待处理', '0', 'oa_correction_status', 0, 'info',    '', NULL, '1', NOW(), '1', NOW(), 0),
(3522, 1, '重审中', '1', 'oa_correction_status', 0, 'warning', '', NULL, '1', NOW(), '1', NOW(), 0),
(3523, 2, '已完成', '2', 'oa_correction_status', 0, 'success', '', NULL, '1', NOW(), '1', NOW(), 0),
(3524, 3, '已撤销', '3', 'oa_correction_status', 0, 'danger',  '', NULL, '1', NOW(), '1', NOW(), 0)
ON DUPLICATE KEY UPDATE label=VALUES(label);
