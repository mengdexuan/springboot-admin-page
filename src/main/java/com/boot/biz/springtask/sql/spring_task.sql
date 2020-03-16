/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : springboot-admin-page

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2020-03-15 19:31:48
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for spring_task
-- ----------------------------
DROP TABLE IF EXISTS `spring_task`;
CREATE TABLE `spring_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` varchar(64) DEFAULT NULL COMMENT '任务ID,随机字符串',
  `name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `bean` varchar(255) DEFAULT NULL COMMENT 'spring bean名称',
  `method` varchar(64) DEFAULT NULL COMMENT '方法名',
  `params` text COMMENT '参数',
  `cron` varchar(20) DEFAULT NULL COMMENT 'cron表达式',
  `status` tinyint(1) DEFAULT NULL COMMENT '任务状态  1.运行  2.暂停',
  `remark` varchar(2048) DEFAULT NULL COMMENT '备注',
  `err_log` text COMMENT '错误日志',
  `del_when_success` tinyint(1) DEFAULT NULL COMMENT '执行成功是否删除该任务 1：不删除    2：删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='spring定时任务';
