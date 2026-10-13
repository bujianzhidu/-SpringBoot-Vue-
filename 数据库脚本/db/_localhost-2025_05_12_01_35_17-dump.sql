-- MySQL dump 10.13  Distrib 9.3.0, for macos15.2 (arm64)
--
-- Host: 127.0.0.1    Database: finance
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `budget`
--

DROP TABLE IF EXISTS `budget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `budget` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category` varchar(50) NOT NULL COMMENT '预算分类',
  `amount` decimal(10,2) NOT NULL COMMENT '预算金额',
  `period` varchar(20) NOT NULL COMMENT '预算周期：MONTHLY-月度，YEARLY-年度',
  `start_date` datetime NOT NULL COMMENT '开始日期',
  `end_date` datetime NOT NULL COMMENT '结束日期',
  `description` varchar(200) DEFAULT NULL COMMENT '预算描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_period` (`period`),
  KEY `idx_date_range` (`start_date`,`end_date`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预算表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `budget`
--

LOCK TABLES `budget` WRITE;
/*!40000 ALTER TABLE `budget` DISABLE KEYS */;
INSERT INTO `budget` VALUES (1,'餐饮',2000.00,'MONTHLY','2024-03-01 00:00:00','2024-03-31 23:59:59','3月餐饮预算','2025-05-09 16:14:52','2025-05-09 22:45:37',1),(2,'交通',1000.00,'MONTHLY','2024-03-01 00:00:00','2024-03-31 23:59:59','3月交通预算','2025-05-09 16:14:52','2025-05-09 22:45:37',1),(3,'购物',3000.00,'MONTHLY','2024-03-01 00:00:00','2024-03-31 23:59:59','3月购物预算','2025-05-09 16:14:52','2025-05-09 22:45:37',1),(4,'工资',100.00,'MONTHLY','2025-05-09 00:00:00','2025-05-16 23:59:59','1','2025-05-09 21:45:59','2025-05-09 22:45:37',1),(5,'工资',100.00,'MONTHLY','2025-05-24 00:00:00','2025-06-15 23:59:59','','2025-05-09 21:46:12','2025-05-09 22:45:37',1),(6,'嫖娼',300.00,'MONTHLY','2025-05-03 00:00:00','2025-05-10 23:59:59','','2025-05-09 22:45:46','2025-05-09 22:45:46',0),(7,'嫖娼',100.00,'MONTHLY','2025-05-02 00:00:00','2025-05-03 23:59:59','','2025-05-10 00:39:39','2025-05-10 00:39:39',0);
/*!40000 ALTER TABLE `budget` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `type` varchar(20) NOT NULL COMMENT '分类类型：INCOME-收入分类，EXPENSE-支出分类',
  `icon` varchar(100) DEFAULT NULL COMMENT '分类图标',
  `sort` int DEFAULT '0' COMMENT '排序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'工资','INCOME','money',1,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(2,'奖金','INCOME','gift',2,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(3,'投资','INCOME','chart',3,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(4,'其他收入','INCOME','more',4,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(5,'餐饮','EXPENSE','food',1,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(6,'交通','EXPENSE','car',2,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(7,'购物','EXPENSE','shopping',3,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(8,'娱乐','EXPENSE','game',4,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(9,'居住','EXPENSE','home',5,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(10,'医疗','EXPENSE','medicine',6,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(11,'教育','EXPENSE','book',7,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(12,'其他支出','EXPENSE','more',8,'2025-05-09 16:14:52','2025-05-09 21:34:18',1),(13,'嫖娼','EXPENSE',NULL,1,'2025-05-09 17:36:10','2025-05-09 21:34:19',1),(14,'工资','INCOME',NULL,0,'2025-05-09 21:36:10','2025-05-09 21:36:10',0),(15,'嫖娼','INCOME',NULL,0,'2025-05-09 22:01:37','2025-05-09 22:01:37',0);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `financial_goal`
--

DROP TABLE IF EXISTS `financial_goal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `financial_goal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '目标名称',
  `target_amount` decimal(10,2) NOT NULL COMMENT '目标金额',
  `current_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '当前金额',
  `start_date` datetime NOT NULL COMMENT '开始日期',
  `target_date` datetime NOT NULL COMMENT '目标日期',
  `status` varchar(20) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '状态：IN_PROGRESS-进行中，COMPLETED-已完成，FAILED-失败',
  `description` varchar(200) DEFAULT NULL COMMENT '目标描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_date_range` (`start_date`,`target_date`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='财务目标表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financial_goal`
--

LOCK TABLES `financial_goal` WRITE;
/*!40000 ALTER TABLE `financial_goal` DISABLE KEYS */;
INSERT INTO `financial_goal` VALUES (1,'买房首付',300000.00,100000.00,'2024-01-01 00:00:00','2024-12-31 23:59:59','IN_PROGRESS','存够买房首付','2025-05-09 16:14:52','2025-05-09 16:14:52',0),(2,'旅游基金',20000.00,5000.00,'2024-01-01 00:00:00','2024-06-30 23:59:59','IN_PROGRESS','暑假旅游基金','2025-05-09 16:14:52','2025-05-09 16:14:52',0),(3,'应急基金',50000.00,50000.00,'2024-01-01 00:00:00','2024-12-31 23:59:59','COMPLETED','应急备用金','2025-05-09 16:14:52','2025-05-09 16:14:52',0);
/*!40000 ALTER TABLE `financial_goal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reminder`
--

DROP TABLE IF EXISTS `reminder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reminder` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '提醒标题',
  `content` varchar(200) DEFAULT NULL COMMENT '提醒内容',
  `remind_time` datetime NOT NULL COMMENT '提醒时间',
  `type` varchar(20) NOT NULL COMMENT '提醒类型：BUDGET-预算提醒，GOAL-目标提醒，CUSTOM-自定义提醒',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，COMPLETED-已完成，CANCELLED-已取消',
  `repeat_type` int DEFAULT '0' COMMENT '重复类型：0-不重复，1-每天，2-每周，3-每月',
  `next_remind_time` datetime DEFAULT NULL COMMENT '下次提醒时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_remind_time` (`remind_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提醒表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reminder`
--

LOCK TABLES `reminder` WRITE;
/*!40000 ALTER TABLE `reminder` DISABLE KEYS */;
/*!40000 ALTER TABLE `reminder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type` varchar(20) NOT NULL COMMENT '交易类型：INCOME-收入，EXPENSE-支出',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `category` varchar(50) NOT NULL COMMENT '交易分类',
  `amount` decimal(10,2) NOT NULL COMMENT '交易金额',
  `description` varchar(200) DEFAULT NULL COMMENT '交易描述',
  `transaction_date` datetime NOT NULL COMMENT '交易日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_category` (`category`),
  KEY `idx_transaction_date` (`transaction_date`),
  KEY `fk_transaction_category` (`category_id`),
  CONSTRAINT `fk_transaction_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (1,'INCOME',1,'工资',10000.00,'3月工资','2024-03-01 10:00:00','2025-05-09 16:14:52','2025-05-09 21:34:12',1),(2,'INCOME',2,'奖金',5000.00,'季度奖金','2024-03-15 14:30:00','2025-05-09 16:14:52','2025-05-09 21:34:12',1),(3,'EXPENSE',5,'餐饮',50.00,'午餐','2024-03-20 12:00:00','2025-05-09 16:14:52','2025-05-09 21:34:12',1),(4,'EXPENSE',6,'交通',100.00,'打车','2024-03-20 18:00:00','2025-05-09 16:14:52','2025-05-09 21:34:12',1),(5,'EXPENSE',7,'购物',200.00,'日用品','2024-03-21 15:00:00','2025-05-09 16:14:52','2025-05-09 21:34:12',1),(6,'INCOME',1,'工资',4000.00,'1','2025-05-09 21:33:51','2025-05-09 21:33:51','2025-05-09 21:34:12',1),(7,'EXPENSE',14,'工资',100.00,'','2025-05-09 21:48:33','2025-05-09 21:48:33','2025-05-09 21:48:33',0),(8,'EXPENSE',14,'工资',100.00,'','2025-05-09 21:50:13','2025-05-09 21:50:13','2025-05-09 21:50:13',0),(9,'INCOME',14,'工资',200.00,'呵呵好','2025-05-09 00:00:00','2025-05-09 21:53:01','2025-05-09 21:54:06',0),(10,'INCOME',14,'工资',400.00,'','2025-05-08 00:00:00','2025-05-09 21:57:04','2025-05-09 21:57:04',0),(11,'EXPENSE',15,'嫖娼',200.00,'','2025-05-07 00:00:00','2025-05-09 22:01:49','2025-05-09 22:01:49',0),(12,'INCOME',14,'工资',10000.00,'中彩票了','2025-05-09 00:00:00','2025-05-09 22:37:44','2025-05-09 22:37:44',0),(13,'INCOME',14,'工资',100.00,'','2025-05-02 00:00:00','2025-05-10 00:46:06','2025-05-10 00:46:06',0),(14,'EXPENSE',14,'工资',200.00,'','2025-05-08 00:00:00','2025-05-10 00:46:50','2025-05-10 00:46:50',0),(15,'EXPENSE',14,'工资',300.00,'','2025-05-01 00:00:00','2025-05-10 00:47:58','2025-05-10 00:47:58',0),(16,'INCOME',14,'工资',200.00,'','2025-05-02 00:00:00','2025-05-10 00:57:37','2025-05-10 00:57:37',0);
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'zijun','$2a$10$Im/FI1jJ7AYIlugtBdnKO.GiP5T0vFRQFxJGKQR/7U6hvKVYXEe4S','1129491453@qq.com',NULL,NULL,1,'2025-05-09 23:28:36','2025-05-09 23:28:36',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-12  1:35:18
