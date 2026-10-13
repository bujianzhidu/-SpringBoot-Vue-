-- 创建数据库
CREATE DATABASE IF NOT EXISTS finance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE finance;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `email` varchar(100) NOT NULL COMMENT '邮箱',
    `avatar` varchar(200) DEFAULT NULL COMMENT '头像URL',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 交易记录表
CREATE TABLE IF NOT EXISTS `transaction` (
                                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                             `type` varchar(20) NOT NULL COMMENT '交易类型：INCOME-收入，EXPENSE-支出',
    `category_id` bigint NOT NULL COMMENT '分类ID',
    `category` varchar(50) NOT NULL COMMENT '交易分类',
    `amount` decimal(10,2) NOT NULL COMMENT '交易金额',
    `description` varchar(200) DEFAULT NULL COMMENT '交易描述',
    `transaction_date` datetime NOT NULL COMMENT '交易日期',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_category` (`category`),
    KEY `idx_transaction_date` (`transaction_date`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- 预算表
CREATE TABLE IF NOT EXISTS `budget` (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `category` varchar(50) NOT NULL COMMENT '预算分类',
    `amount` decimal(10,2) NOT NULL COMMENT '预算金额',
    `period` varchar(20) NOT NULL COMMENT '预算周期：MONTHLY-月度，YEARLY-年度',
    `start_date` datetime NOT NULL COMMENT '开始日期',
    `end_date` datetime NOT NULL COMMENT '结束日期',
    `description` varchar(200) DEFAULT NULL COMMENT '预算描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_period` (`period`),
    KEY `idx_date_range` (`start_date`, `end_date`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预算表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `name` varchar(50) NOT NULL COMMENT '分类名称',
    `type` varchar(20) NOT NULL COMMENT '分类类型：INCOME-收入分类，EXPENSE-支出分类',
    `icon` varchar(100) DEFAULT NULL COMMENT '分类图标',
    `sort` int DEFAULT 0 COMMENT '排序号',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_sort` (`sort`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易分类表';

-- 提醒表
CREATE TABLE IF NOT EXISTS `reminder` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `title` varchar(100) NOT NULL COMMENT '提醒标题',
    `content` varchar(200) DEFAULT NULL COMMENT '提醒内容',
    `remind_time` datetime NOT NULL COMMENT '提醒时间',
    `type` varchar(20) NOT NULL COMMENT '提醒类型：BUDGET-预算提醒，GOAL-目标提醒，CUSTOM-自定义提醒',
    `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，COMPLETED-已完成，CANCELLED-已取消',
    `repeat_type` int DEFAULT 0 COMMENT '重复类型：0-不重复，1-每天，2-每周，3-每月',
    `next_remind_time` datetime DEFAULT NULL COMMENT '下次提醒时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_remind_time` (`remind_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提醒表';

-- 财务目标表
CREATE TABLE IF NOT EXISTS `financial_goal` (
                                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                `name` varchar(100) NOT NULL COMMENT '目标名称',
    `target_amount` decimal(10,2) NOT NULL COMMENT '目标金额',
    `current_amount` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '当前金额',
    `start_date` datetime NOT NULL COMMENT '开始日期',
    `target_date` datetime NOT NULL COMMENT '目标日期',
    `status` varchar(20) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '状态：IN_PROGRESS-进行中，COMPLETED-已完成，FAILED-失败',
    `description` varchar(200) DEFAULT NULL COMMENT '目标描述',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_date_range` (`start_date`, `target_date`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务目标表';

-- 插入默认分类数据
INSERT INTO `category` (`name`, `type`, `icon`, `sort`) VALUES
-- 收入分类
('工资', 'INCOME', 'money', 1),
('奖金', 'INCOME', 'gift', 2),
('投资', 'INCOME', 'chart', 3),
('其他收入', 'INCOME', 'more', 4),

-- 支出分类
('餐饮', 'EXPENSE', 'food', 1),
('交通', 'EXPENSE', 'car', 2),
('购物', 'EXPENSE', 'shopping', 3),
('娱乐', 'EXPENSE', 'game', 4),
('居住', 'EXPENSE', 'home', 5),
('医疗', 'EXPENSE', 'medicine', 6),
('教育', 'EXPENSE', 'book', 7),
('其他支出', 'EXPENSE', 'more', 8);

-- 插入测试交易数据
INSERT INTO `transaction` (`type`, `category`, `amount`, `description`, `transaction_date`) VALUES
                                                                                                ('INCOME', '工资', 10000.00, '3月工资', '2024-03-01 10:00:00'),
                                                                                                ('INCOME', '奖金', 5000.00, '季度奖金', '2024-03-15 14:30:00'),
                                                                                                ('EXPENSE', '餐饮', 50.00, '午餐', '2024-03-20 12:00:00'),
                                                                                                ('EXPENSE', '交通', 100.00, '打车', '2024-03-20 18:00:00'),
                                                                                                ('EXPENSE', '购物', 200.00, '日用品', '2024-03-21 15:00:00');

-- 插入测试预算数据
INSERT INTO `budget` (`category`, `amount`, `period`, `start_date`, `end_date`, `description`) VALUES
                                                                                                   ('餐饮', 2000.00, 'MONTHLY', '2024-03-01 00:00:00', '2024-03-31 23:59:59', '3月餐饮预算'),
                                                                                                   ('交通', 1000.00, 'MONTHLY', '2024-03-01 00:00:00', '2024-03-31 23:59:59', '3月交通预算'),
                                                                                                   ('购物', 3000.00, 'MONTHLY', '2024-03-01 00:00:00', '2024-03-31 23:59:59', '3月购物预算');

-- 插入测试财务目标数据
INSERT INTO `financial_goal` (`name`, `target_amount`, `current_amount`, `start_date`, `target_date`, `status`, `description`) VALUES
                                                                                                                                   ('买房首付', 300000.00, 100000.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'IN_PROGRESS', '存够买房首付'),
                                                                                                                                   ('旅游基金', 20000.00, 5000.00, '2024-01-01 00:00:00', '2024-06-30 23:59:59', 'IN_PROGRESS', '暑假旅游基金'),
                                                                                                                                   ('应急基金', 50000.00, 50000.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59', 'COMPLETED', '应急备用金');