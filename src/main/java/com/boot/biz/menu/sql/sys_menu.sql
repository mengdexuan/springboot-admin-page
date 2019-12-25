/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : springboot-admin-page

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2019-12-09 15:06:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '菜单名称',
  `url` varchar(255) DEFAULT NULL COMMENT '菜单访问url',
  `status` tinyint(1) unsigned NOT NULL DEFAULT '1' COMMENT '状态: 0隐藏   1显示',
  `pid` bigint(20) unsigned DEFAULT NULL COMMENT '父级ID',
  `icon` varchar(100) DEFAULT '' COMMENT '图标',
  `sort` smallint(10) unsigned NOT NULL COMMENT '排序',
  `type` int(1) DEFAULT NULL COMMENT '1.前台菜单   2.后台菜单',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='菜单表';
