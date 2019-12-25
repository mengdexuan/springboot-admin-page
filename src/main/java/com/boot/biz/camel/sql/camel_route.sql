/*
Navicat MySQL Data Transfer

Source SshServerInfo         : 192.168.4.192(江苏稳定版)
Source SshServerInfo Version : 50726
Source Host           : 192.168.4.192:3306
Source Database       : xagent

Target SshServerInfo Type    : MYSQL
Target SshServerInfo Version : 50726
File Encoding         : 65001

Date: 2019-06-27 18:25:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for camel_route
-- ----------------------------
DROP TABLE IF EXISTS `camel_route`;
CREATE TABLE `camel_route` (
  `route_id` varchar(255) NOT NULL COMMENT '路由id',
  `status` varchar(15) DEFAULT NULL COMMENT '路由状态\r\nStarting：正在启动\r\nStarted：已启动\r\nStopping：正在停止\r\nStopped：已停止\r\nSuspending：正在暂停\r\nSuspended：已暂停',
  `description` varchar(255) DEFAULT NULL COMMENT '路由描述',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='camel路由';
