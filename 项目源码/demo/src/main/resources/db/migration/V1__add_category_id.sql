-- 添加 category_id 字段
ALTER TABLE `transaction` ADD COLUMN `category_id` bigint NOT NULL COMMENT '分类ID' AFTER `type`;

-- 更新现有数据
UPDATE `transaction` t
JOIN `category` c ON t.category = c.name
SET t.category_id = c.id;

-- 添加外键约束
ALTER TABLE `transaction` 
ADD CONSTRAINT `fk_transaction_category` 
FOREIGN KEY (`category_id`) REFERENCES `category` (`id`); 