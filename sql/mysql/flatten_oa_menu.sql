-- 将OA模块从3层嵌套扁平化为2层嵌套
-- 与已有的 seal/car/correction 模块保持一致的结构
-- 
-- 修改前 (3层): /oa -> contract -> contract-bill-list / contract-bill-info
-- 修改后 (2层): /oa -> contract-bill-list / contract-bill-info

-- Step 1: 将所有页面菜单(type=2)的parent_id改为5300(/oa)
-- Contract pages
UPDATE system_menu SET parent_id = 5300 WHERE id IN (5302, 5303);
-- Document pages
UPDATE system_menu SET parent_id = 5300 WHERE id IN (5312, 5313);
-- Expense pages
UPDATE system_menu SET parent_id = 5300 WHERE id IN (5322, 5323);
-- Project pages
UPDATE system_menu SET parent_id = 5300 WHERE id IN (5332, 5333);
-- Incoming pages
UPDATE system_menu SET parent_id = 5300 WHERE id IN (5342, 5343);
-- Travel pages
UPDATE system_menu SET parent_id = 5300 WHERE id IN (5352, 5353);

-- Step 2: 隐藏中间目录(type=1) - 不删除,只隐藏
UPDATE system_menu SET visible = 0 WHERE id IN (5301, 5311, 5321, 5331, 5341, 5351);

-- Step 3: 将按钮权限(type=3)也移到对应的页面菜单下
-- Contract buttons (5304-5310) -> under contract-bill-list (5302)
UPDATE system_menu SET parent_id = 5302 WHERE id IN (5304, 5305, 5306, 5307, 5308, 5309, 5310);
-- Document buttons (5314-5320) -> under document-dispatch-list (5312)
UPDATE system_menu SET parent_id = 5312 WHERE id IN (5314, 5315, 5316, 5317, 5318, 5319, 5320);
-- Expense buttons (5324-5330) -> under expense-reimburse-list (5322)
UPDATE system_menu SET parent_id = 5322 WHERE id IN (5324, 5325, 5326, 5327, 5328, 5329, 5330);
-- Project buttons (5334-5340) -> under project-initiation-list (5332)
UPDATE system_menu SET parent_id = 5332 WHERE id IN (5334, 5335, 5336, 5337, 5338, 5339, 5340);
-- Incoming buttons (5344-5350) -> under incoming-document-list (5342)
UPDATE system_menu SET parent_id = 5342 WHERE id IN (5344, 5345, 5346, 5347, 5348, 5349, 5350);
-- Travel buttons (5354-5360) -> under travel-apply-list (5352)
UPDATE system_menu SET parent_id = 5352 WHERE id IN (5354, 5355, 5356, 5357, 5358, 5359, 5360);

-- Step 4: 同步更新 system_role_menu 中对应的角色权限分配
-- 不需要修改，因为role_menu是按menu_id关联的，parent_id变化不影响权限

-- 验证结果
SELECT id, parent_id, type, visible, path, component, component_name
FROM system_menu 
WHERE deleted = 0 AND (parent_id = 5300 OR id = 5300)
ORDER BY type, sort, id;
