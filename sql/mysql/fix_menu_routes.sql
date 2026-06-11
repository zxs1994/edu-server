-- ============================================================
-- 修复OA模块前端页面404问题
-- 原因：数据库菜单路由路径与Vue代码中硬编码的导航路径不匹配
-- 修复：调整菜单path使其与Vue代码期望的路径一致
-- ============================================================

-- 1. 顶层目录：将 "审批管理" 的路径从 /approval 改为 /oa
UPDATE system_menu SET path = '/oa' WHERE id = 5300;

-- 2. 合同管理 - 列表和详情的path
UPDATE system_menu SET path = 'contract-bill-list' WHERE id = 5302;
UPDATE system_menu SET path = 'contract-bill-info' WHERE id = 5303;

-- 3. 公文发文 - 列表和详情的path
UPDATE system_menu SET path = 'document-dispatch-list' WHERE id = 5312;
UPDATE system_menu SET path = 'document-dispatch-info' WHERE id = 5313;

-- 4. 费用报销 - 列表和详情的path
UPDATE system_menu SET path = 'expense-reimburse-list' WHERE id = 5322;
UPDATE system_menu SET path = 'expense-reimburse-info' WHERE id = 5323;

-- 5. 项目立项 - 列表和详情的path
UPDATE system_menu SET path = 'project-initiation-list' WHERE id = 5332;
UPDATE system_menu SET path = 'project-initiation-info' WHERE id = 5333;

-- 6. 收文办理 - 列表和详情的path
UPDATE system_menu SET path = 'incoming-document-list' WHERE id = 5342;
UPDATE system_menu SET path = 'incoming-document-info' WHERE id = 5343;

-- 7. 差旅申请 - 列表和详情的path
UPDATE system_menu SET path = 'travel-apply-list' WHERE id = 5352;
UPDATE system_menu SET path = 'travel-apply-info' WHERE id = 5353;

-- 8. 设置component_name（用于keep-alive缓存）
UPDATE system_menu SET component_name = 'OaContractBillList' WHERE id = 5302;
UPDATE system_menu SET component_name = 'OaContractBillInfo' WHERE id = 5303;
UPDATE system_menu SET component_name = 'OaDocumentDispatchList' WHERE id = 5312;
UPDATE system_menu SET component_name = 'OaDocumentDispatchInfo' WHERE id = 5313;
UPDATE system_menu SET component_name = 'OaExpenseReimburseList' WHERE id = 5322;
UPDATE system_menu SET component_name = 'OaExpenseReimburseInfo' WHERE id = 5323;
UPDATE system_menu SET component_name = 'OaProjectInitiationList' WHERE id = 5332;
UPDATE system_menu SET component_name = 'OaProjectInitiationInfo' WHERE id = 5333;
UPDATE system_menu SET component_name = 'OaIncomingDocumentList' WHERE id = 5342;
UPDATE system_menu SET component_name = 'OaIncomingDocumentInfo' WHERE id = 5343;
UPDATE system_menu SET component_name = 'OaTravelApplyList' WHERE id = 5352;
UPDATE system_menu SET component_name = 'OaTravelApplyInfo' WHERE id = 5353;
