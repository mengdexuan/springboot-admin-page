/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : springboot-admin-page

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2019-12-23 13:50:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `sys_dictionary`;
CREATE TABLE `sys_dictionary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dict_name` varchar(255) DEFAULT NULL COMMENT '字典名称',
  `dict_key` varchar(255) NOT NULL COMMENT '字典key',
  `dict_value` varchar(2000) NOT NULL COMMENT '字典值',
  `dict_note` varchar(500) DEFAULT NULL COMMENT '备注',
  `enable` smallint(1) DEFAULT NULL COMMENT '是否可用：0：不可用  1：可用',
  `order_no` int(11) DEFAULT NULL COMMENT '排序字段',
  `pid` bigint(20) DEFAULT NULL COMMENT '父级ID',
  `create_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `m_key_unique` (`dict_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='数据字典';

-- ----------------------------
-- Records of sys_dictionary
-- ----------------------------
INSERT INTO `sys_dictionary` VALUES ('1', '未登录或登录超时', '10000', '未登录或登录超时', '未登录或登录超时', '1', '1', null, '2019-10-24 18:07:52');
INSERT INTO `sys_dictionary` VALUES ('2', '登录超时时间，默认30分钟', 'login_time_out', '30', '登录超时时间，默认30分钟', '1', '2', null, '2019-10-24 18:18:24');
INSERT INTO `sys_dictionary` VALUES ('3', '登录名或密码错误', '10001', '登录名或密码错误', '登录名或密码错误', '1', '3', null, '2019-10-24 18:59:21');
INSERT INTO `sys_dictionary` VALUES ('4', '登录拦截', 'login_interceptor', 'false', '登录拦截', '1', '4', null, '2019-12-23 13:10:28');
