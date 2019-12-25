/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : springboot-admin-page

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2019-12-17 17:00:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for key_val
-- ----------------------------
DROP TABLE IF EXISTS `key_val`;
CREATE TABLE `key_val` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_id` varchar(64) DEFAULT NULL COMMENT '数据id',
  `data_key` varchar(64) DEFAULT NULL COMMENT '数据key',
  `data_val` text COMMENT '数据值',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='键值记录表';
