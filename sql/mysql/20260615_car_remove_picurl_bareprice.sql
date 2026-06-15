-- 删除车辆信息表中的 pic_url（车辆照片）和 bare_price（裸车价）字段
-- 对应需求：车辆照片、裸车价不再需要，前后端同步清理
ALTER TABLE `oa_car` DROP COLUMN `pic_url`, DROP COLUMN `bare_price`;
