/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : springboot-admin-page

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2019-12-09 15:02:58
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for auth_group
-- ----------------------------
DROP TABLE IF EXISTS `auth_group`;
CREATE TABLE `auth_group` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(500) NOT NULL DEFAULT '' COMMENT '权限组名称',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0不可用    1可用',
  `menus` varchar(2000) DEFAULT '' COMMENT '菜单ID列表，多个用逗号隔开',
  `type` int(1) DEFAULT NULL COMMENT '1.前台权限组   2.后台权限组',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限组表';
