-- ============================================================
-- 中国引航协会OA系统 - 组织架构数据
-- 数据库: ruoyi-office
-- 日期: 2026-06-09
-- ============================================================

-- ============ 1. 部门 ============
UPDATE system_dept SET deleted = 1 WHERE deleted = 0 AND id NOT IN (100);

INSERT INTO system_dept (id, name, parent_id, sort, leader_user_id, phone, email, status, creator, create_time, updater, update_time, deleted, tenant_id, org_type)
SELECT 200, '中国引航协会', 0, 0, NULL, NULL, NULL, 0, '1', NOW(), '1', NOW(), 0, 1, '0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dept WHERE id = 200);

INSERT INTO system_dept (id, name, parent_id, sort, leader_user_id, phone, email, status, creator, create_time, updater, update_time, deleted, tenant_id, org_type)
SELECT 201, '秘书处', 200, 1, NULL, NULL, NULL, 0, '1', NOW(), '1', NOW(), 0, 1, '0'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_dept WHERE id = 201);

UPDATE system_users SET dept_id = 201, nickname = '系统管理员' WHERE id = 1 AND deleted = 0;

-- ============ 2. 角色 ============
UPDATE system_role SET deleted = 1 WHERE deleted = 0 AND code != 'super_admin';

INSERT INTO system_role (id, name, code, sort, data_scope, data_scope_dept_ids, status, type, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 201, '会长', 'cpha_chairman', 1, 1, '', 0, 2, '会长：全流程查阅权+事后纠错撤销权', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role WHERE id = 201);

INSERT INTO system_role (id, name, code, sort, data_scope, data_scope_dept_ids, status, type, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 202, '秘书长', 'cpha_secretary_general', 2, 1, '', 0, 2, '秘书长：终审权限', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role WHERE id = 202);

INSERT INTO system_role (id, name, code, sort, data_scope, data_scope_dept_ids, status, type, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 203, '常务副秘书长', 'cpha_deputy_sg_executive', 3, 1, '', 0, 2, '常务副秘书长：复核权限', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role WHERE id = 203);

INSERT INTO system_role (id, name, code, sort, data_scope, data_scope_dept_ids, status, type, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 204, '副秘书长', 'cpha_deputy_sg', 4, 2, '', 0, 2, '副秘书长：普通经办人/部门负责人', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role WHERE id = 204);

INSERT INTO system_role (id, name, code, sort, data_scope, data_scope_dept_ids, status, type, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 205, '办公室主任', 'cpha_office_director', 5, 2, '', 0, 2, '办公室主任：部门初审权限', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role WHERE id = 205);

INSERT INTO system_role (id, name, code, sort, data_scope, data_scope_dept_ids, status, type, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 206, '财务', 'cpha_finance', 6, 2, '', 0, 2, '财务：财务审核权限', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role WHERE id = 206);

INSERT INTO system_role (id, name, code, sort, data_scope, data_scope_dept_ids, status, type, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 207, '秘书', 'cpha_secretary', 7, 2, '', 0, 2, '秘书：经办人权限', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_role WHERE id = 207);

-- ============ 3. 岗位 ============
UPDATE system_post SET deleted = 1 WHERE deleted = 0 AND id NOT IN (7, 8, 9);

INSERT INTO system_post (id, code, name, sort, status, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 201, 'CPHA_CHAIRMAN', '会长', 1, 0, '会长岗位', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_post WHERE id = 201);

INSERT INTO system_post (id, code, name, sort, status, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 202, 'CPHA_SECRETARY_GENERAL', '秘书长', 2, 0, '秘书长岗位', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_post WHERE id = 202);

INSERT INTO system_post (id, code, name, sort, status, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 203, 'CPHA_DEPUTY_SG_EXEC', '常务副秘书长', 3, 0, '常务副秘书长岗位', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_post WHERE id = 203);

INSERT INTO system_post (id, code, name, sort, status, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 204, 'CPHA_DEPUTY_SG', '副秘书长', 4, 0, '副秘书长岗位', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_post WHERE id = 204);

INSERT INTO system_post (id, code, name, sort, status, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 205, 'CPHA_OFFICE_DIRECTOR', '办公室主任', 5, 0, '办公室主任岗位', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_post WHERE id = 205);

INSERT INTO system_post (id, code, name, sort, status, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 206, 'CPHA_FINANCE', '财务', 6, 0, '财务岗位', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_post WHERE id = 206);

INSERT INTO system_post (id, code, name, sort, status, remark, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 207, 'CPHA_SECRETARY', '秘书', 7, 0, '秘书岗位', '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_post WHERE id = 207);

-- ============ 4. 用户 ============
-- 密码统一为 admin123（bcrypt hash from admin user）
UPDATE system_users SET deleted = 1 WHERE deleted = 0 AND id != 1;

INSERT INTO system_users (id, username, password, nickname, remark, dept_id, post_ids, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 201, 'LGP2008', '$2a$04$KljJDa/LK7QfDm0lF5OhuePhlPfjRH3tB2Wu351Uidz.oQGJXevPi', '李国平', '会长', 200, '[201]', 0, '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE id = 201);

INSERT INTO system_users (id, username, password, nickname, remark, dept_id, post_ids, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 202, 'SJH2008', '$2a$04$KljJDa/LK7QfDm0lF5OhuePhlPfjRH3tB2Wu351Uidz.oQGJXevPi', '沈建华', '常务副会长兼秘书长', 201, '[202]', 0, '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE id = 202);

INSERT INTO system_users (id, username, password, nickname, remark, dept_id, post_ids, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 203, 'HJG2008', '$2a$04$KljJDa/LK7QfDm0lF5OhuePhlPfjRH3tB2Wu351Uidz.oQGJXevPi', '胡建国', '常务副秘书长', 201, '[203]', 0, '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE id = 203);

INSERT INTO system_users (id, username, password, nickname, remark, dept_id, post_ids, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 204, 'WQ2008', '$2a$04$KljJDa/LK7QfDm0lF5OhuePhlPfjRH3tB2Wu351Uidz.oQGJXevPi', '王强', '副秘书长', 201, '[204]', 0, '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE id = 204);

INSERT INTO system_users (id, username, password, nickname, remark, dept_id, post_ids, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 205, 'LJQ2008', '$2a$04$KljJDa/LK7QfDm0lF5OhuePhlPfjRH3tB2Wu351Uidz.oQGJXevPi', '李继泉', '副秘书长', 201, '[204]', 0, '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE id = 205);

INSERT INTO system_users (id, username, password, nickname, remark, dept_id, post_ids, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 206, 'MCJ2008', '$2a$04$KljJDa/LK7QfDm0lF5OhuePhlPfjRH3tB2Wu351Uidz.oQGJXevPi', '梅春艳', '副秘书长', 201, '[204]', 0, '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE id = 206);

INSERT INTO system_users (id, username, password, nickname, remark, dept_id, post_ids, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 207, 'FXG2008', '$2a$04$KljJDa/LK7QfDm0lF5OhuePhlPfjRH3tB2Wu351Uidz.oQGJXevPi', '冯晓光', '办公室主任', 201, '[205]', 0, '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE id = 207);

INSERT INTO system_users (id, username, password, nickname, remark, dept_id, post_ids, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 208, 'YXF2008', '$2a$04$KljJDa/LK7QfDm0lF5OhuePhlPfjRH3tB2Wu351Uidz.oQGJXevPi', '于赞飞', '财务', 201, '[206]', 0, '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE id = 208);

INSERT INTO system_users (id, username, password, nickname, remark, dept_id, post_ids, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 209, 'MS2008', '$2a$04$KljJDa/LK7QfDm0lF5OhuePhlPfjRH3tB2Wu351Uidz.oQGJXevPi', '工作人员', '秘书', 201, '[207]', 0, '1', NOW(), '1', NOW(), 0, 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM system_users WHERE id = 209);

-- ============ 5. 用户-角色关联 ============
DELETE FROM system_user_role WHERE user_id IN (201,202,203,204,205,206,207,208,209);

INSERT INTO system_user_role (user_id, role_id) VALUES (201, 201);
INSERT INTO system_user_role (user_id, role_id) VALUES (202, 202);
INSERT INTO system_user_role (user_id, role_id) VALUES (203, 203);
INSERT INTO system_user_role (user_id, role_id) VALUES (204, 204);
INSERT INTO system_user_role (user_id, role_id) VALUES (205, 204);
INSERT INTO system_user_role (user_id, role_id) VALUES (206, 204);
INSERT INTO system_user_role (user_id, role_id) VALUES (207, 205);
INSERT INTO system_user_role (user_id, role_id) VALUES (208, 206);
INSERT INTO system_user_role (user_id, role_id) VALUES (209, 207);
INSERT INTO system_user_role (user_id, role_id) VALUES (1, 201);

-- ============ 6. 用户-岗位关联 ============
DELETE FROM system_user_post WHERE user_id IN (201,202,203,204,205,206,207,208,209);

INSERT INTO system_user_post (user_id, post_id) VALUES (201, 201);
INSERT INTO system_user_post (user_id, post_id) VALUES (202, 202);
INSERT INTO system_user_post (user_id, post_id) VALUES (203, 203);
INSERT INTO system_user_post (user_id, post_id) VALUES (204, 204);
INSERT INTO system_user_post (user_id, post_id) VALUES (205, 204);
INSERT INTO system_user_post (user_id, post_id) VALUES (206, 204);
INSERT INTO system_user_post (user_id, post_id) VALUES (207, 205);
INSERT INTO system_user_post (user_id, post_id) VALUES (208, 206);
INSERT INTO system_user_post (user_id, post_id) VALUES (209, 207);

-- ============ 7. 更新部门负责人 ============
UPDATE system_dept SET leader_user_id = 202 WHERE id = 200;
UPDATE system_dept SET leader_user_id = 207 WHERE id = 201;
