/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

DROP DATABASE IF EXISTS `emall`;
CREATE DATABASE IF NOT EXISTS `emall` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `emall`;

DROP TABLE IF EXISTS `cart`;
CREATE TABLE IF NOT EXISTS `cart` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int NOT NULL COMMENT '用户ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '购买数量',
  `create_time` datetime DEFAULT (now()) COMMENT '创建时间',
  `selected` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否选中',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `fk_cart_member` (`user_id`) USING BTREE,
  KEY `fk_cart_product` (`product_id`) USING BTREE,
  CONSTRAINT `fk_cart_member` FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='购物车项';

DELETE FROM `cart`;
/*!50000 ALTER TABLE `cart` AUTO_INCREMENT = 1 */;
INSERT INTO `cart` (`id`, `user_id`, `product_id`, `quantity`, `create_time`, `selected`) VALUES
	(23, 5, 11, 1, '2026-07-17 20:07:25', 1),
	(24, 5, 12, 2, '2026-07-17 20:07:31', 1),
	(28, 5, 15, 10, '2026-07-22 10:10:26', 1),
	(29, 4, 15, 1, '2026-07-22 10:43:49', 1);

DROP TABLE IF EXISTS `category`;
CREATE TABLE IF NOT EXISTS `category` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `parent_id` int DEFAULT '0' COMMENT '父级分类ID (0表示根节点)',
  `level` tinyint DEFAULT '1' COMMENT '层级深度 (1/2/3)',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';

DELETE FROM `category`;
/*!50000 ALTER TABLE `category` AUTO_INCREMENT = 1 */;
INSERT INTO `category` (`id`, `name`, `parent_id`, `level`, `sort_order`, `create_time`) VALUES
	(1, '电子产品', 0, 1, 1, '2026-07-09 11:19:56'),
	(2, '服装服饰', 0, 1, 2, '2026-07-11 15:03:47'),
	(3, '手机通讯', 1, 2, 1, '2026-07-11 15:15:09'),
	(4, '电脑办公', 1, 2, 2, '2026-07-11 15:20:03'),
	(5, '男装', 2, 2, 1, '2026-07-17 19:55:30');

DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE IF NOT EXISTS `chat_message` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` int NOT NULL COMMENT '会话ID（关联 chat_session.id）',
  `sender_type` tinyint NOT NULL COMMENT '发送者类型：0-用户 1-客服 2-AI机器人',
  `sender_id` int DEFAULT NULL COMMENT '发送者ID（用户ID/客服ID，AI时为NULL）',
  `content` text NOT NULL COMMENT '消息内容',
  `msg_type` tinyint NOT NULL DEFAULT '0' COMMENT '消息类型：0-文本 1-图片 2-商品卡片 3-订单卡片',
  `extra_data` json DEFAULT NULL COMMENT '扩展数据（如商品卡片信息）',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读：0-未读 1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '已读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_sender_type` (`sender_type`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_chatmessage_session` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客服消息表';

DELETE FROM `chat_message`;
/*!50000 ALTER TABLE `chat_message` AUTO_INCREMENT = 1 */;
INSERT INTO `chat_message` (`id`, `session_id`, `sender_type`, `sender_id`, `content`, `msg_type`, `extra_data`, `is_read`, `read_time`, `create_time`) VALUES
	(2004, 1005, 0, 5, '这好用吗', 0, NULL, 0, NULL, '2026-07-15 10:03:06'),
	(2005, 1005, 0, 5, '你好', 0, NULL, 0, NULL, '2026-07-15 10:18:23'),
	(2006, 1005, 0, 5, '你好', 3, '{"a": 123}', 0, NULL, '2026-07-15 10:22:34'),
	(2007, 1005, 0, 5, '你好', 3, '{"a": 123}', 0, NULL, '2026-07-15 10:49:46'),
	(2008, 1005, 0, 5, '你好', 3, '{"a": 123}', 0, NULL, '2026-07-15 11:01:19'),
	(2009, 1006, 0, 5, '好用吗', 0, NULL, 1, NULL, '2026-07-15 14:26:59'),
	(2010, 1006, 1, 2, '包好用的！！！！亲！', 0, NULL, 1, NULL, '2026-07-15 14:32:17'),
	(2011, 1006, 0, 5, '真的假的', 0, NULL, 1, NULL, '2026-07-15 14:35:45'),
	(2012, 1006, 0, 5, '不好用退了', 0, NULL, 1, NULL, '2026-07-15 14:35:58'),
	(2013, 1006, 1, 2, 'QAQ', 0, NULL, 1, NULL, '2026-07-15 14:36:13'),
	(2014, 1007, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:09:05'),
	(2015, 1008, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:09:05'),
	(2016, 1009, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:09:14'),
	(2017, 1010, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:09:24'),
	(2018, 1011, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:09:24'),
	(2019, 1012, 0, 4, '我想咨询这个订单的问题', 0, NULL, 1, NULL, '2026-07-15 15:10:27'),
	(2020, 1014, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:10:37'),
	(2021, 1013, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:10:37'),
	(2022, 1015, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:13:50'),
	(2023, 1016, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:14:00'),
	(2024, 1017, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:14:00'),
	(2025, 1018, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:14:23'),
	(2026, 1019, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:14:23'),
	(2027, 1020, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:14:33'),
	(2028, 1021, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:14:33'),
	(2029, 1022, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:16:57'),
	(2030, 1023, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:16:57'),
	(2031, 1024, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:17:07'),
	(2032, 1025, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:17:07'),
	(2033, 1026, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:17:09'),
	(2034, 1027, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 15:17:09'),
	(2208, 1134, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 16:28:28'),
	(2209, 1135, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 16:28:28'),
	(2210, 1135, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-15 16:28:28'),
	(2211, 1134, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-15 16:28:28'),
	(2212, 1136, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 16:32:07'),
	(2213, 1137, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 16:32:07'),
	(2214, 1136, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-15 16:32:08'),
	(2215, 1137, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-15 16:32:08'),
	(2216, 1138, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-15 16:47:33'),
	(2217, 1138, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-15 16:47:33'),
	(2218, 1140, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-16 09:28:28'),
	(2219, 1139, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-16 09:28:28'),
	(2220, 1140, 0, 4, '我想咨询订单 #ORD5900415455 的问题', 3, '{"items": [{"id": 8, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}, {"id": 9, "quantity": 1, "productId": 2, "totalPrice": 40.11, "productName": "商品2", "productPrice": 40.11}], "status": "待支付", "orderId": 7, "orderNo": "ORD5900415455", "createTime": "2026-07-14T02:44:24.000+00:00", "statusName": "未知状态", "totalAmount": 73.17, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-16 09:28:29'),
	(2221, 1139, 0, 4, '我想咨询订单 #ORD5900415455 的问题', 3, '{"items": [{"id": 8, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}, {"id": 9, "quantity": 1, "productId": 2, "totalPrice": 40.11, "productName": "商品2", "productPrice": 40.11}], "status": "待支付", "orderId": 7, "orderNo": "ORD5900415455", "createTime": "2026-07-14T02:44:24.000+00:00", "statusName": "未知状态", "totalAmount": 73.17, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-16 09:28:29'),
	(2222, 1139, 0, 4, '111', 0, NULL, 0, NULL, '2026-07-16 09:28:36'),
	(2223, 1012, 1, 7, '1', 0, NULL, 1, NULL, '2026-07-16 09:40:40'),
	(2224, 1012, 1, 7, '2', 0, NULL, 1, NULL, '2026-07-16 09:40:44'),
	(2225, 1012, 1, 7, '3', 0, NULL, 1, NULL, '2026-07-16 09:40:50'),
	(2226, 1141, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-16 09:40:56'),
	(2227, 1142, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-16 09:40:56'),
	(2228, 1142, 0, 4, '我想咨询订单 #ORD5900415455 的问题', 3, '{"items": [{"id": 8, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}, {"id": 9, "quantity": 1, "productId": 2, "totalPrice": 40.11, "productName": "商品2", "productPrice": 40.11}], "status": "待支付", "orderId": 7, "orderNo": "ORD5900415455", "createTime": "2026-07-14T02:44:24.000+00:00", "statusName": "未知状态", "totalAmount": 73.17, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-16 09:40:56'),
	(2229, 1141, 0, 4, '我想咨询订单 #ORD5900415455 的问题', 3, '{"items": [{"id": 8, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}, {"id": 9, "quantity": 1, "productId": 2, "totalPrice": 40.11, "productName": "商品2", "productPrice": 40.11}], "status": "待支付", "orderId": 7, "orderNo": "ORD5900415455", "createTime": "2026-07-14T02:44:24.000+00:00", "statusName": "未知状态", "totalAmount": 73.17, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-16 09:40:56'),
	(2230, 1142, 0, 4, '111', 0, NULL, 0, NULL, '2026-07-16 09:40:59'),
	(2231, 1144, 0, 4, '我想咨询这个订单的问题', 0, NULL, 1, NULL, '2026-07-16 09:48:43'),
	(2232, 1143, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-16 09:48:43'),
	(2233, 1143, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-16 09:48:43'),
	(2234, 1144, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 1, NULL, '2026-07-16 09:48:43'),
	(2235, 1144, 0, 4, '123', 0, NULL, 1, NULL, '2026-07-16 09:48:46'),
	(2236, 1144, 1, 7, '456', 0, NULL, 1, NULL, '2026-07-16 09:48:59'),
	(2237, 1145, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-16 09:49:05'),
	(2238, 1146, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-16 09:49:05'),
	(2239, 1145, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-16 09:49:05'),
	(2240, 1146, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-16 09:49:05'),
	(2241, 1144, 1, 7, '222', 0, NULL, 1, NULL, '2026-07-16 09:49:18'),
	(2242, 1148, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-16 09:49:26'),
	(2243, 1147, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-16 09:49:26'),
	(2244, 1148, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-16 09:49:26'),
	(2245, 1147, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 33.06, "productName": "商品1", "productPrice": 33.06}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 33.06, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-16 09:49:26'),
	(2246, 1144, 0, 4, '不自动', 0, NULL, 0, NULL, '2026-07-16 10:05:19'),
	(2247, 1144, 0, 4, '将就吧', 0, NULL, 0, NULL, '2026-07-16 10:05:30'),
	(2248, 1149, 0, 4, '您好', 0, NULL, 0, NULL, '2026-07-16 10:21:47'),
	(2249, 1150, 0, 4, '我想咨询这个订单的问题', 0, NULL, 1, NULL, '2026-07-18 10:36:33'),
	(2250, 1150, 0, 4, '我想咨询订单 #ORD9701458639 的问题', 3, '{"items": [{"id": 10, "quantity": 1, "productId": 1, "totalPrice": 9999, "productName": "iPhone 15 Pro Max", "productPrice": 9999}], "status": "待支付", "orderId": 8, "orderNo": "ORD9701458639", "createTime": "2026-07-14T03:36:13.000+00:00", "statusName": "未知状态", "totalAmount": 9999, "receiverName": "1", "receiverPhone": "13035119760"}', 1, NULL, '2026-07-18 10:36:33'),
	(2251, 1150, 0, 4, '这个手机好用吗', 0, NULL, 1, NULL, '2026-07-18 10:48:25'),
	(2252, 1150, 1, 7, '根据知识库内容无法回答', 0, NULL, 1, NULL, '2026-07-18 10:52:32'),
	(2253, 1150, 0, 4, '手机充电功率是', 0, NULL, 1, NULL, '2026-07-18 10:53:09'),
	(2254, 1150, 1, 7, '充电功率是多少瓦？', 0, NULL, 1, NULL, '2026-07-18 10:54:42'),
	(2255, 1150, 0, 4, '充电功率是多少', 0, NULL, 1, NULL, '2026-07-18 10:58:06'),
	(2256, 1150, 1, 7, '这款手机标配33W快充，充电速度高效，半小时即可充至60%以上，告别长时间等待。', 0, NULL, 1, NULL, '2026-07-18 11:08:46'),
	(2257, 1150, 0, 4, '不知道', 0, NULL, 0, NULL, '2026-07-21 09:49:39'),
	(2258, 1151, 0, 5, '您好', 0, NULL, 0, NULL, '2026-07-22 10:10:46'),
	(2259, 1152, 0, 5, '您好', 0, NULL, 0, NULL, '2026-07-22 10:11:10'),
	(2260, 1153, 0, 4, '我想咨询这个订单的问题', 0, NULL, 0, NULL, '2026-07-22 10:55:02'),
	(2261, 1153, 0, 4, '我想咨询订单 #ORD2026072000003 的问题', 3, '{"items": [{"id": 15, "quantity": 1, "productId": 1, "totalPrice": 9999, "productName": "iPhone 15 Pro Max", "productPrice": 9999}], "status": "已支付", "orderId": 12, "orderNo": "ORD2026072000003", "createTime": "2026-07-20T11:24:24.000+00:00", "statusName": "未知状态", "totalAmount": 9999, "receiverName": "1", "receiverPhone": "13035119760"}', 0, NULL, '2026-07-22 10:55:02'),
	(2262, 1153, 0, 4, '充电功率是多大的', 0, NULL, 0, NULL, '2026-07-22 10:55:23'),
	(2263, 1153, 0, 4, '手机型号是', 0, NULL, 0, NULL, '2026-07-22 10:55:39'),
	(2264, 1156, 0, 7, '测试消息：这是一个测试', 0, NULL, 0, NULL, '2026-07-23 14:52:24'),
	(2265, 1157, 0, 7, '测试消息：这是一个测试', 0, NULL, 1, NULL, '2026-07-23 14:52:42'),
	(2266, 1158, 0, 7, '测试消息：这是一个测试', 0, NULL, 1, NULL, '2026-07-23 14:54:57'),
	(2267, 1158, 0, 7, '测试消息内容', 0, NULL, 1, NULL, '2026-07-23 14:54:57'),
	(2268, 1159, 0, 7, '测试消息：这是一个测试', 0, NULL, 1, NULL, '2026-07-23 15:37:15'),
	(2269, 1159, 0, 7, '测试消息内容', 0, NULL, 1, NULL, '2026-07-23 15:37:15');

DROP TABLE IF EXISTS `chat_session`;
CREATE TABLE IF NOT EXISTS `chat_session` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `session_no` varchar(32) NOT NULL COMMENT '会话编号（唯一，格式：CS + 时间戳 + 随机数）',
  `user_id` int NOT NULL COMMENT '用户ID（关联 member.id）',
  `cs_id` int DEFAULT NULL COMMENT '客服ID（关联 sys_user.id，NULL表示未分配）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '会话状态：0-排队中 1-服务中 2-已结束 3-AI托管',
  `mode` tinyint NOT NULL DEFAULT '0' COMMENT '当前模式：0-真人客服 1-AI自动回复',
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '会话开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '会话结束时间',
  `first_message` varchar(500) DEFAULT NULL COMMENT '用户首条消息内容',
  `source` varchar(30) DEFAULT 'MALL' COMMENT '来源：MALL-商城首页 PRODUCT-商品详情 ORDER-订单售后',
  `source_id` varchar(50) DEFAULT NULL COMMENT '来源ID（如商品ID、订单号）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_no` (`session_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_cs_id` (`cs_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_chatsession_member` FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_chatsession_sysuser` FOREIGN KEY (`cs_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客服会话表';

DELETE FROM `chat_session`;
/*!50000 ALTER TABLE `chat_session` AUTO_INCREMENT = 1 */;
INSERT INTO `chat_session` (`id`, `session_no`, `user_id`, `cs_id`, `status`, `mode`, `start_time`, `end_time`, `first_message`, `source`, `source_id`, `create_time`, `update_time`) VALUES
	(1005, 'CS202607150005', 5, 2, 2, 0, '2026-07-15 10:03:06', '2026-07-15 14:16:10', '这好用吗', '4', '4', '2026-07-15 10:03:06', '2026-07-15 13:53:10'),
	(1006, 'CS202607150006', 5, 2, 2, 0, '2026-07-15 14:26:59', '2026-07-23 18:55:00', '好用吗', 'PRODUCT', '4', '2026-07-15 14:26:59', '2026-07-23 18:55:01'),
	(1007, 'CS202607150007', 4, 7, 2, 0, '2026-07-15 15:09:05', '2026-07-16 09:48:14', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:09:05', '2026-07-15 15:09:05'),
	(1008, 'CS202607150008', 4, 7, 2, 0, '2026-07-15 15:09:05', '2026-07-16 09:48:17', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:09:05', '2026-07-15 15:09:05'),
	(1009, 'CS202607150009', 4, 7, 2, 0, '2026-07-15 15:09:14', '2026-07-16 09:48:19', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:09:14', '2026-07-15 15:09:14'),
	(1010, 'CS202607150011', 4, 7, 2, 0, '2026-07-15 15:09:24', '2026-07-16 09:48:21', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:09:24', '2026-07-15 15:09:24'),
	(1011, 'CS202607150010', 4, 7, 2, 0, '2026-07-15 15:09:24', '2026-07-16 09:48:22', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:09:24', '2026-07-15 15:09:24'),
	(1012, 'CS202607150012', 4, 7, 2, 0, '2026-07-15 15:10:27', '2026-07-16 09:41:04', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:10:27', '2026-07-15 15:10:27'),
	(1013, 'CS202607150014', 4, 7, 2, 0, '2026-07-15 15:10:37', '2026-07-16 09:41:08', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:10:37', '2026-07-15 15:10:37'),
	(1014, 'CS202607150013', 4, 7, 2, 0, '2026-07-15 15:10:37', '2026-07-16 09:41:11', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:10:37', '2026-07-15 15:10:37'),
	(1015, 'CS202607150015', 4, 7, 2, 0, '2026-07-15 15:13:50', '2026-07-16 09:41:15', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:13:50', '2026-07-15 15:13:50'),
	(1016, 'CS202607150016', 4, 7, 2, 0, '2026-07-15 15:14:00', '2026-07-16 09:41:19', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:14:00', '2026-07-15 15:14:00'),
	(1017, 'CS202607150017', 4, 7, 2, 0, '2026-07-15 15:14:00', '2026-07-16 09:41:24', '我想咨询这个订单的问题', 'ORDER', '5', '2026-07-15 15:14:00', '2026-07-15 15:14:00'),
	(1018, 'CS202607150018', 4, 7, 2, 0, '2026-07-15 15:14:23', '2026-07-16 09:41:31', '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-15 15:14:23', '2026-07-15 15:14:23'),
	(1019, 'CS202607150019', 4, 7, 2, 0, '2026-07-15 15:14:23', '2026-07-16 09:41:36', '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-15 15:14:23', '2026-07-15 15:14:23'),
	(1020, 'CS202607150020', 4, 7, 2, 0, '2026-07-15 15:14:33', '2026-07-16 09:41:56', '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-15 15:14:33', '2026-07-15 15:14:33'),
	(1021, 'CS202607150021', 4, 7, 2, 0, '2026-07-15 15:14:33', '2026-07-16 09:45:45', '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-15 15:14:33', '2026-07-15 15:14:33'),
	(1022, 'CS202607150022', 4, 7, 2, 0, '2026-07-15 15:16:57', '2026-07-16 09:45:48', '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-15 15:16:57', '2026-07-15 15:16:57'),
	(1023, 'CS202607150023', 4, 7, 2, 0, '2026-07-15 15:16:57', '2026-07-16 09:45:50', '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-15 15:16:57', '2026-07-15 15:16:57'),
	(1024, 'CS202607150024', 4, 7, 2, 0, '2026-07-15 15:17:07', '2026-07-16 09:45:54', '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-15 15:17:07', '2026-07-15 15:17:07'),
	(1025, 'CS202607150025', 4, NULL, 2, 0, '2026-07-15 15:17:07', NULL, '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-15 15:17:07', '2026-07-16 09:47:24'),
	(1026, 'CS202607150026', 4, NULL, 2, 0, '2026-07-15 15:17:09', NULL, '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-15 15:17:09', '2026-07-16 09:47:24'),
	(1027, 'CS202607150027', 4, NULL, 2, 0, '2026-07-15 15:17:09', NULL, '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-15 15:17:09', '2026-07-16 09:47:24'),
	(1134, 'CS202607150135', 4, NULL, 2, 0, '2026-07-15 16:28:28', NULL, '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-15 16:28:28', '2026-07-16 09:47:24'),
	(1135, 'CS202607150134', 4, NULL, 2, 0, '2026-07-15 16:28:28', NULL, '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-15 16:28:28', '2026-07-16 09:47:24'),
	(1136, 'CS202607150137', 4, NULL, 2, 0, '2026-07-15 16:32:07', NULL, '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-15 16:32:07', '2026-07-16 09:47:24'),
	(1137, 'CS202607150136', 4, NULL, 2, 0, '2026-07-15 16:32:07', NULL, '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-15 16:32:07', '2026-07-16 09:47:24'),
	(1138, 'CS202607150138', 4, NULL, 2, 0, '2026-07-15 16:47:33', NULL, '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-15 16:47:33', '2026-07-16 09:47:24'),
	(1139, 'CS202607160001', 4, NULL, 2, 0, '2026-07-16 09:28:28', NULL, '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-16 09:28:28', '2026-07-16 09:47:24'),
	(1140, 'CS202607160002', 4, NULL, 2, 0, '2026-07-16 09:28:28', NULL, '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-16 09:28:28', '2026-07-16 09:47:24'),
	(1141, 'CS202607160004', 4, NULL, 2, 0, '2026-07-16 09:40:56', NULL, '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-16 09:40:56', '2026-07-16 09:47:24'),
	(1142, 'CS202607160003', 4, NULL, 2, 0, '2026-07-16 09:40:56', NULL, '我想咨询这个订单的问题', 'ORDER', '7', '2026-07-16 09:40:56', '2026-07-16 09:47:24'),
	(1143, 'CS202607160006', 4, 7, 2, 0, '2026-07-16 09:48:43', '2026-07-16 10:07:05', '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-16 09:48:43', '2026-07-16 09:48:43'),
	(1144, 'CS202607160005', 4, 7, 2, 0, '2026-07-16 09:48:43', '2026-07-16 10:06:53', '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-16 09:48:43', '2026-07-16 09:48:43'),
	(1145, 'CS202607160008', 4, 7, 2, 0, '2026-07-16 09:49:05', '2026-07-16 10:07:03', '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-16 09:49:05', '2026-07-16 09:49:05'),
	(1146, 'CS202607160007', 4, 7, 2, 0, '2026-07-16 09:49:05', '2026-07-16 10:07:00', '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-16 09:49:05', '2026-07-16 09:49:05'),
	(1147, 'CS202607160009', 4, 7, 2, 0, '2026-07-16 09:49:26', '2026-07-16 10:06:58', '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-16 09:49:26', '2026-07-16 09:49:26'),
	(1148, 'CS202607160010', 4, 7, 2, 0, '2026-07-16 09:49:26', '2026-07-16 10:06:56', '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-16 09:49:26', '2026-07-16 09:49:26'),
	(1149, 'CS202607160012', 4, 7, 2, 0, '2026-07-16 10:21:47', '2026-07-16 15:40:04', '您好', 'MALL', NULL, '2026-07-16 10:21:47', '2026-07-16 10:21:47'),
	(1150, 'CS202607180001', 4, 7, 2, 0, '2026-07-18 10:36:33', '2026-07-22 02:54:58', '我想咨询这个订单的问题', 'ORDER', '8', '2026-07-18 10:36:33', '2026-07-18 10:36:33'),
	(1151, 'CS202607220001', 5, NULL, 2, 0, '2026-07-22 10:10:46', '2026-07-22 02:10:51', '您好', 'MALL', NULL, '2026-07-22 10:10:46', '2026-07-22 10:10:46'),
	(1152, 'CS202607220002', 5, NULL, 2, 0, '2026-07-22 10:11:10', '2026-07-22 02:11:25', '您好', 'MALL', NULL, '2026-07-22 10:11:10', '2026-07-22 10:11:10'),
	(1153, 'CS202607220003', 4, 7, 1, 0, '2026-07-22 10:55:02', NULL, '我想咨询这个订单的问题', 'ORDER', '12', '2026-07-22 10:55:02', '2026-07-22 10:55:02'),
	(1156, 'CS202607230003', 7, NULL, 0, 0, '2026-07-23 14:52:24', NULL, '测试消息：这是一个测试', 'PRODUCT', '1', '2026-07-23 14:52:24', '2026-07-23 14:52:24'),
	(1157, 'CS202607230004', 7, 2, 0, 0, '2026-07-23 14:52:42', NULL, '测试消息：这是一个测试', 'PRODUCT', '1', '2026-07-23 14:52:42', '2026-07-23 14:52:42'),
	(1158, 'CS202607230005', 7, 2, 2, 0, '2026-07-23 14:54:57', '2026-07-23 14:54:58', '测试消息：这是一个测试', 'PRODUCT', '1', '2026-07-23 14:54:57', '2026-07-23 14:54:57'),
	(1159, 'CS202607230006', 7, 2, 2, 0, '2026-07-23 15:37:15', '2026-07-23 15:37:16', '测试消息：这是一个测试', 'PRODUCT', '1', '2026-07-23 15:37:15', '2026-07-23 15:37:15');

DROP TABLE IF EXISTS `cs_status`;
CREATE TABLE IF NOT EXISTS `cs_status` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cs_id` int NOT NULL COMMENT '客服ID（关联 sys_user.id）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-离线 1-在线 2-忙碌',
  `max_concurrent` int NOT NULL DEFAULT '5' COMMENT '最大并发会话数',
  `current_count` int NOT NULL DEFAULT '0' COMMENT '当前服务会话数',
  `last_active_time` datetime DEFAULT NULL COMMENT '最后活跃时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cs_id` (`cs_id`),
  CONSTRAINT `fk_csstatus_sysuser` FOREIGN KEY (`cs_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客服在线状态表';

DELETE FROM `cs_status`;
/*!50000 ALTER TABLE `cs_status` AUTO_INCREMENT = 1 */;
INSERT INTO `cs_status` (`id`, `cs_id`, `status`, `max_concurrent`, `current_count`, `last_active_time`, `update_time`) VALUES
	(1, 7, 1, 50, 1, NULL, '2026-07-16 09:40:15'),
	(2, 2, 0, 5, 0, NULL, '2026-07-23 18:55:56'),
	(3, 6, 0, 5, 0, NULL, '2026-07-15 11:36:59');

DROP TABLE IF EXISTS `inventory`;
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '库存记录ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `stock` int NOT NULL DEFAULT '0' COMMENT '当前实际库存',
  `alert_threshold` int DEFAULT '10' COMMENT '预警阈值',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后变动时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`),
  CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品库存表';

DELETE FROM `inventory`;
/*!50000 ALTER TABLE `inventory` AUTO_INCREMENT = 1 */;
INSERT INTO `inventory` (`id`, `product_id`, `stock`, `alert_threshold`, `update_time`) VALUES
	(1, 1, 49, 10, '2026-07-22 10:09:41'),
	(2, 2, 30, 10, '2026-07-22 10:09:02'),
	(4, 4, 45, 10, '2026-07-22 10:08:28'),
	(7, 7, 19, 10, '2026-07-22 16:50:41'),
	(8, 8, 35, 10, '2026-07-22 16:50:12'),
	(9, 9, 25, 10, '2026-07-22 16:40:30'),
	(10, 10, 99, 10, '2026-07-22 10:04:22'),
	(11, 11, 80, 10, '2026-07-22 10:05:06'),
	(12, 12, 120, 10, '2026-07-22 10:07:07'),
	(13, 13, 15, 10, '2026-07-22 10:07:36'),
	(15, 15, 20, 10, '2026-07-23 11:35:31');

DROP TABLE IF EXISTS `knowledge_doc`;
CREATE TABLE IF NOT EXISTS `knowledge_doc` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '文档ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文档标题/来源',
  `category` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'manual' COMMENT '分类（如 policy / manual / faq）',
  `product_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '关联商品ID',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '文档url',
  `file_type` varchar(50) NOT NULL COMMENT '文档文件后缀名',
  `status` tinyint DEFAULT '1' COMMENT '状态 (0失效 1生效)',
  `chunk_count` int DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_source` (`category`,`product_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI知识库原始文档表';

DELETE FROM `knowledge_doc`;
/*!50000 ALTER TABLE `knowledge_doc` AUTO_INCREMENT = 1 */;
INSERT INTO `knowledge_doc` (`id`, `title`, `category`, `product_id`, `url`, `file_type`, `status`, `chunk_count`, `create_time`, `update_time`) VALUES
	(32, '电商平台手机配置说明.docx', 'manual', NULL, '/static/doc/a77ab261-41de-4372-8935-91764269e191.docx', 'docx', 1, 8, '2026-07-21 18:44:47', '2026-07-21 18:44:47');

DROP TABLE IF EXISTS `member`;
CREATE TABLE IF NOT EXISTS `member` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '会员ID (主键)',
  `email` varchar(100) NOT NULL COMMENT '登录邮箱（唯一）',
  `password` varchar(255) NOT NULL COMMENT '登录密码 (BCrypt加密)',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户（显示用）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号（非必填）',
  `level` tinyint DEFAULT '1' COMMENT '会员等级 (1普通 2白银 3黄金)',
  `points` int DEFAULT '0' COMMENT '当前积分余额',
  `status` tinyint DEFAULT '1' COMMENT '账户状态 (0禁用 1启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会员信息表（前台普通会员）';

DELETE FROM `member`;
/*!50000 ALTER TABLE `member` AUTO_INCREMENT = 1 */;
INSERT INTO `member` (`id`, `email`, `password`, `username`, `phone`, `level`, `points`, `status`, `create_time`, `update_time`) VALUES
	(4, '3283587409@qq.com', '$2a$10$6OG.hhCdJ4HwbVD5.bXBhuCB8mIGb/9Z3ylRR.oR4/H3XhLscB85W', 'xixi', '11111111111', 1, 0, 1, '2026-07-09 16:16:57', '2026-07-09 16:16:57'),
	(5, '123@qq.com', '$2a$10$3DRSDI478e8FpKcNeQM3RuEf879Vol/CAswhelzQbdZl9pxIrSjwC', '白糖', '123456', 1, 0, 1, '2026-07-09 16:35:46', '2026-07-23 18:56:16'),
	(7, 'test@test.com', '$2a$10$rELa0.WFijxOPoFZHewj5O2Rr7e57lLVsE0krqm7fq3tmDon/ojMe', '测试用户', '13912345678', 1, 0, 1, '2026-07-17 09:29:25', '2026-07-17 09:29:25');

DROP TABLE IF EXISTS `order`;
CREATE TABLE IF NOT EXISTS `order` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号（业务唯一）',
  `user_id` int NOT NULL COMMENT '下单用户ID（关联 member.id）',
  `pay_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实际支付金额',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态 (0待支付 1已支付 2已发货 3已完成 4已取消 5退款中)',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `shipping_time` datetime DEFAULT NULL COMMENT '发货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '收货人手机',
  `receiver_address` varchar(255) DEFAULT NULL COMMENT '收货详细地址',
  `remark` varchar(255) DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_order_member` FOREIGN KEY (`user_id`) REFERENCES `member` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';

DELETE FROM `order`;
/*!50000 ALTER TABLE `order` AUTO_INCREMENT = 1 */;
INSERT INTO `order` (`id`, `order_no`, `user_id`, `pay_amount`, `status`, `pay_time`, `shipping_time`, `finish_time`, `receiver_name`, `receiver_phone`, `receiver_address`, `remark`, `create_time`, `update_time`) VALUES
	(2, 'ORD1969322957', 5, 0.00, 0, NULL, NULL, NULL, '黄kx', '18788888', '武汉理工大学', '和一位', '2026-07-12 22:47:19', '2026-07-14 11:28:02'),
	(3, 'ORD0275651846', 5, 40.10, 1, '2026-07-14 11:28:17', NULL, NULL, '黄kx', '18788888', '武汉理工大学', '和一位', '2026-07-12 22:59:43', '2026-07-14 11:28:02'),
	(4, 'ORD1882926593', 5, 0.00, 4, NULL, NULL, NULL, '黄kx', '18788888', '武汉理工大学', '订单取消测试', '2026-07-13 08:34:04', '2026-07-14 11:28:02'),
	(5, 'ORD8284727639', 4, 0.00, 4, NULL, NULL, NULL, '1', '13035119760', 'zhou-yufei', '1', '2026-07-13 10:32:15', '2026-07-14 11:28:02'),
	(6, 'ORD8555191149', 4, 40.11, 1, '2026-07-14 11:33:40', NULL, NULL, '1', '13035119760', 'zhou-yufei', '1', '2026-07-13 10:42:16', '2026-07-14 11:28:02'),
	(7, 'ORD5900415455', 4, 16998.00, 1, '2026-07-20 09:18:49', NULL, NULL, '1', '13035119760', '111', '1', '2026-07-14 10:44:24', '2026-07-14 11:28:02'),
	(8, 'ORD9701458639', 4, 9999.00, 1, '2026-07-20 11:20:00', NULL, NULL, '1', '13035119760', '1', '1', '2026-07-14 11:36:13', '2026-07-14 11:36:13'),
	(9, 'ORD2026071400001', 5, 947.75, 1, '2026-07-14 16:02:26', NULL, NULL, 'hhhhhkkkxx', '18888888888', 'WWWWsswwhhhhhuuuttt', '', '2026-07-14 16:00:35', '2026-07-14 16:00:35'),
	(10, 'ORD2026072000001', 4, 16999.00, 1, '2026-07-20 11:20:28', NULL, NULL, '1', '13035119760', 'zhou-yufei', '1', '2026-07-20 11:09:34', '2026-07-20 11:09:34'),
	(11, 'ORD2026072000002', 4, 299.00, 1, '2026-07-20 11:22:10', NULL, NULL, '1', '13035119760', 'zhou-yufei', '1', '2026-07-20 11:22:06', '2026-07-20 11:22:06'),
	(12, 'ORD2026072000003', 4, 9999.00, 3, '2026-07-20 11:24:28', '2026-07-23 10:11:14', '2026-07-23 10:11:20', '1', '13035119760', 'zhou-yufei', '1', '2026-07-20 11:24:24', '2026-07-20 11:24:24');

DROP TABLE IF EXISTS `order_item`;
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` int NOT NULL COMMENT '所属订单ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL COMMENT '购买数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_id_product_id` (`order_id`,`product_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_id` (`product_id`),
  CONSTRAINT `fk_orderitem_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_orderitem_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单商品明细表';

DELETE FROM `order_item`;
/*!50000 ALTER TABLE `order_item` AUTO_INCREMENT = 1 */;
INSERT INTO `order_item` (`id`, `order_id`, `product_id`, `quantity`, `create_time`) VALUES
	(4, 4, 1, 2, '2026-07-13 08:34:04'),
	(5, 4, 2, 6, '2026-07-13 08:34:04'),
	(6, 5, 4, 1, '2026-07-13 10:32:15'),
	(7, 6, 2, 1, '2026-07-13 10:42:16'),
	(8, 7, 1, 1, '2026-07-14 10:44:24'),
	(9, 7, 2, 1, '2026-07-14 10:44:24'),
	(10, 8, 1, 1, '2026-07-14 11:36:13'),
	(11, 9, 4, 5, '2026-07-14 16:00:35'),
	(12, 9, 2, 6, '2026-07-14 16:00:35'),
	(13, 10, 7, 1, '2026-07-20 11:09:34'),
	(14, 11, 10, 1, '2026-07-20 11:22:06'),
	(15, 12, 1, 1, '2026-07-20 11:24:24');

DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `sub_title` varchar(200) DEFAULT NULL COMMENT '副标题/卖点',
  `category_id` int NOT NULL COMMENT '所属分类ID',
  `price` decimal(10,2) NOT NULL COMMENT '销售单价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock` int DEFAULT '0' COMMENT '库存',
  `status` tinyint DEFAULT '1' COMMENT '商品状态 (0下架 1上架)',
  `description` text COMMENT '商品详情描述',
  `image_urls` json DEFAULT NULL COMMENT '商品轮播图 (JSON数组)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_price` (`price`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品信息表';

DELETE FROM `product`;
/*!50000 ALTER TABLE `product` AUTO_INCREMENT = 1 */;
INSERT INTO `product` (`id`, `name`, `sub_title`, `category_id`, `price`, `original_price`, `stock`, `status`, `description`, `image_urls`, `create_time`, `update_time`) VALUES
	(1, 'iPhone 15 Pro Max', '旗舰级智能手机，钛金属边框', 3, 9999.00, 10999.00, 49, 1, '6.7英寸超视网膜XDR显示屏，A17 Pro芯片，4800万像素主摄，支持USB-C接口', '["/static/img/9807834d-fefd-4083-aa0d-bc82c217d894.webp"]', '2026-07-09 11:20:54', '2026-07-17 19:57:32'),
	(2, '华为Mate 60 Pro', '卫星通话，超可靠玄武架构', 3, 6999.00, 7999.00, 30, 1, '6.82英寸OLED屏幕，昆仑玻璃，第二代卫星通信，超光变影像系统', '["/static/img/30413898-bdc3-4e73-a72a-474c8bc0f262.webp", "/static/img/59bb0f62-d96e-4261-bb08-48349fea35e8.webp"]', '2026-07-09 14:58:23', '2026-07-17 19:58:47'),
	(4, '小米14 Ultra', '徕卡光学，全焦段四摄', 3, 5999.00, 6499.00, 45, 1, '骁龙8 Gen 3处理器，6.73英寸AMOLED屏，5300mAh电池，徕卡四摄系统', '["/static/img/3a838039-aa83-43d7-837c-45160da3592d.webp", "/static/img/4d2438f6-04fe-418e-88bc-9bb1b9db8c74.webp"]', '2026-07-11 11:37:57', '2026-07-17 19:59:20'),
	(7, 'MacBook Pro 14寸', 'M3 Pro芯片，专业级性能', 4, 16999.00, 18999.00, 19, 1, '14.2英寸Liquid视网膜XDR显示屏，M3 Pro芯片，18GB统一内存，1TB固态硬盘', '["/static/img/d1074c6d-59ba-4d15-8907-4f93ed9cd930.webp"]', '2026-07-17 19:59:45', '2026-07-17 19:59:45'),
	(8, '联想ThinkPad X1 Carbon', '商务旗舰，轻薄耐用', 4, 8999.00, 9999.00, 35, 1, '14英寸2.8K OLED屏，第13代英特尔酷睿i7处理器，16GB内存，512GB SSD，碳纤维材质', '["/static/img/7ce7bc4c-58ba-43e7-9826-727f01b37d7f.webp"]', '2026-07-17 19:59:45', '2026-07-17 19:59:45'),
	(9, '华为MateBook X Pro', '超轻薄笔记本，智慧办公', 4, 10999.00, 11999.00, 25, 0, '13.9英寸3K触控全面屏，第13代英特尔酷睿i7，16GB内存，1TB SSD，超级终端互联', '["/static/img/ff0a80dc-d204-48e5-8a1d-16867a796b9d.webp"]', '2026-07-17 19:59:45', '2026-07-17 19:59:45'),
	(10, '耐克运动T恤', '吸汗透气，夏季新款', 5, 299.00, 399.00, 99, 1, '采用Dri-FIT技术，快速排汗，保持干爽舒适，经典圆领设计，多色可选', '["/static/img/fd8efcf9-0f8b-4aac-a38f-e8b35df0498e.webp"]', '2026-07-17 19:59:45', '2026-07-17 19:59:45'),
	(11, '阿迪达斯三叶草外套', '潮流休闲，春秋款', 5, 599.00, 799.00, 80, 1, '经典三叶草系列，舒适棉质面料，刺绣logo，适合日常休闲穿搭', '["/static/img/8a152fa8-0aa6-4b59-bacb-deb8463b44e9.webp", "/static/img/7c13195f-b397-4e2a-ad87-876afc340ca5.webp"]', '2026-07-17 19:59:45', '2026-07-17 19:59:45'),
	(12, '优衣库摇粒绒外套', '保暖舒适，百搭款', 5, 399.00, 499.00, 120, 1, '轻量摇粒绒材质，温暖柔软，简约设计，适合户外活动和日常穿着', '["/static/img/a2b1e024-fa4c-46ba-a10b-06493cab5516.webp", "/static/img/21c60b28-cd18-4a69-a72c-55e6ab7ae307.webp"]', '2026-07-17 19:59:45', '2026-07-17 19:59:45'),
	(13, '三星S24 Ultra', 'AI手机，超大屏幕', 3, 9699.00, 10699.00, 15, 0, '6.8英寸Dynamic AMOLED 2X屏，骁龙8 Gen 3，12GB内存，256GB存储，S Pen手写笔', '["/static/img/e1a6f3c7-b4e1-4c39-b854-24b99c5b0989.webp", "/static/img/470faed3-ef34-4d5d-b0db-70d1a0869c4e.webp"]', '2026-07-17 19:59:45', '2026-07-17 19:59:45'),
	(15, 'test', NULL, 1, 1.00, 1.00, 20, 0, NULL, '["/static/img/66bcf762-961b-4197-ada9-f64dd9629c7a.jpg"]', '2026-07-21 10:45:29', '2026-07-21 10:45:29');

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `email` varchar(100) NOT NULL COMMENT '登录邮箱（唯一）',
  `password` varchar(255) NOT NULL COMMENT '登录密码 (BCrypt加密)',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户名（显示用）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号（非必填）',
  `role_code` varchar(30) NOT NULL DEFAULT 'CS' COMMENT '角色编码 (ADMIN/CS)',
  `status` tinyint DEFAULT '1' COMMENT '账户状态 (0禁用 1启用)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='后台用户表（管理员/客服）';

DELETE FROM `sys_user`;
/*!50000 ALTER TABLE `sys_user` AUTO_INCREMENT = 1 */;
INSERT INTO `sys_user` (`id`, `email`, `password`, `username`, `phone`, `role_code`, `status`, `create_time`, `update_time`) VALUES
	(1, 'admin@qq.com', '$2a$10$9jdkZiagWyLMbM2odItWHuNDw1a.GB3cQKWCzspxnR8Cb6kttM3Di', 'admin', '13912345678', 'ADMIN', 1, '2026-07-10 09:50:52', '2026-07-10 09:50:52'),
	(2, 'xiaowang@qq.com', '$2a$10$1F6dLub39DtE5qtsaQR8Uu3jbJwA6jD0jglhsGU25u3OlIt/GA6JW', '小王', NULL, 'CS', 1, '2026-07-10 10:22:17', '2026-07-15 12:09:21'),
	(6, 'xiaowang8@qq.com', '$2a$10$yOHdHf14TBNjkUNr65jKAe2NkUGSJ9dlfRbIG1H1iBowJXKr8fhwC', '小王8', '26124124124', 'CS', 1, '2026-07-10 10:52:11', '2026-07-10 10:52:11'),
	(7, 'cs@example.com', '$2a$10$Hv5OUmsQdi4Tm3A07AAPkesVq5qG9jhEJqjChGXgTlByPBKABmIly', 'CS', '', 'CS', 1, '2026-07-11 12:17:22', '2026-07-11 12:17:22'),
	(8, 'admin@example.com', '$2a$10$IZfSnq3Ko7M0WoVm/IDEBOz6q58GvqaGRSKKesBOsjdE6ruZ4jM8u', '管理员', '', 'ADMIN', 1, '2026-07-11 12:18:50', '2026-07-11 12:18:59');

DROP TABLE IF EXISTS `user_recommend`;
CREATE TABLE IF NOT EXISTS `user_recommend` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `score` double NOT NULL COMMENT '推荐分数，范围从0到1，表示该商品被推荐的可能性',
  `reason` text NOT NULL COMMENT '推荐理由',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户推荐表';

DELETE FROM `user_recommend`;
/*!50000 ALTER TABLE `user_recommend` AUTO_INCREMENT = 1 */;
INSERT INTO `user_recommend` (`id`, `user_id`, `product_id`, `score`, `reason`, `create_time`) VALUES
	(1, 5, 10, 0.95, '与购物车中商品同属男装类别，作为外套内搭的互补商品', '2026-07-18 00:00:00'),
	(2, 5, 8, 0.55, '男性用户热门办公设备，与服装类形成跨品类互补推荐', '2026-07-18 00:00:00'),
	(3, 5, 1, 0.5, '热门旗舰手机，男性用户偏好品类', '2026-07-18 00:00:00'),
	(4, 5, 2, 0.45, '高端旗舰手机，支持卫星通信功能', '2026-07-18 00:00:00'),
	(5, 5, 4, 0.4, '徕卡影像旗舰手机，高性价比之选', '2026-07-18 00:00:00'),
	(14, 4, 1, 0.98, '您已多次购买iPhone 15 Pro Max，旗舰级智能手机，复购推荐', '2026-07-23 10:36:10'),
	(15, 4, 2, 0.95, '您已多次购买华为Mate 60 Pro，卫星通话旗舰，复购推荐', '2026-07-23 10:36:10'),
	(16, 4, 7, 0.88, '您曾购买MacBook Pro 14寸，专业级性能与手机搭配使用，互补推荐', '2026-07-23 10:36:10'),
	(17, 4, 4, 0.82, '您曾关注小米14 Ultra，徕卡四摄旗舰手机，与您的购机偏好同类推荐', '2026-07-23 10:36:10'),
	(18, 4, 8, 0.78, '您购买过电脑产品，联想ThinkPad X1 Carbon商务轻薄本，电脑办公同类推荐', '2026-07-23 10:36:10'),
	(19, 4, 10, 0.75, '您曾购买耐克运动T恤，吸汗透气夏季新款，复购推荐', '2026-07-23 10:36:10'),
	(20, 4, 11, 0.72, '您购买过运动T恤，阿迪达斯三叶草外套潮流休闲，男装互补推荐', '2026-07-23 10:36:10'),
	(21, 4, 12, 0.68, '您购买过男装，优衣库摇粒绒外套保暖百搭，男装同类推荐', '2026-07-23 10:36:10');

DROP TRIGGER IF EXISTS `cs_status_before_update`;
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `cs_status_before_update` BEFORE UPDATE ON `cs_status` FOR EACH ROW BEGIN
	IF NEW.status!=0 THEN
		SET NEW.status = 
			CASE
				WHEN NEW.current_count >= NEW.max_concurrent THEN 2
				ELSE 1
			END;
	END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

DROP TRIGGER IF EXISTS `inventory_create`;
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
DELIMITER //
CREATE TRIGGER `inventory_create` AFTER INSERT ON `product` FOR EACH ROW BEGIN
  INSERT INTO inventory(product_id,stock) VALUE(NEW.id, NEW.stock);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

DROP TRIGGER IF EXISTS `inventory_update`;
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
DELIMITER //
CREATE TRIGGER `inventory_update` AFTER UPDATE ON `product` FOR EACH ROW BEGIN
  UPDATE inventory SET stock= NEW.stock,update_time=CURRENT_TIMESTAMP WHERE product_id = NEW.id;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

DROP TRIGGER IF EXISTS `sys_user_after_insert`;
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `sys_user_after_insert` AFTER INSERT ON `sys_user` FOR EACH ROW BEGIN
	INSERT INTO cs_status(cs_id) VALUES(NEW.id);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
