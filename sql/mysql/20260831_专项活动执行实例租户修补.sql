-- 修补已生成但 tenant_id=0 的活动执行实例（从关联活动复制租户）
UPDATE `edu_activity_instance` i
INNER JOIN `edu_activity` a ON i.`activity_id` = a.`id` AND a.`deleted` = b'0'
SET i.`tenant_id` = a.`tenant_id`
WHERE i.`deleted` = b'0'
  AND i.`tenant_id` = 0
  AND a.`tenant_id` > 0;

-- 修补专项活动实例公告（按标题前缀匹配，从同租户活动实例推断租户）
UPDATE `system_notice` n
INNER JOIN (
    SELECT a.`tenant_id`, i.`period_no`, a.`name`
    FROM `edu_activity_instance` i
    INNER JOIN `edu_activity` a ON i.`activity_id` = a.`id` AND a.`deleted` = b'0'
    WHERE i.`deleted` = b'0' AND a.`tenant_id` > 0
) act ON n.`title` LIKE CONCAT(act.`name`, ' 第', act.`period_no`, '期')
SET n.`tenant_id` = act.`tenant_id`
WHERE n.`deleted` = b'0'
  AND n.`tenant_id` = 0;
