/*
Navicat MySQL Data Transfer

Source SshServerInfo         : 192.168.4.192(江苏稳定版)
Source SshServerInfo Version : 50726
Source Host           : 192.168.4.192:3306
Source Database       : xagent

Target SshServerInfo Type    : MYSQL
Target SshServerInfo Version : 50726
File Encoding         : 65001

Date: 2019-06-23 16:50:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for url_limit
-- ----------------------------
DROP TABLE IF EXISTS `url_limit`;
CREATE TABLE `url_limit` (
  `req_url` varchar(125) NOT NULL COMMENT '接口请求地址',
  `url_key` varchar(500) NOT NULL COMMENT 'req_url摘要加密串',
  `url_limit` int(11) DEFAULT '0' COMMENT '每秒钟允许的请求数，默认 0 ，表示不限制',
  `url_desc` varchar(500) DEFAULT NULL COMMENT '接口功能描述',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`url_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='接口限流';
