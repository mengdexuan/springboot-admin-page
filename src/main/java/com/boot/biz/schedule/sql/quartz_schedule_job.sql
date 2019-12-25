/*
Navicat MySQL Data Transfer

Source SshServerInfo         : localhost
Source SshServerInfo Version : 50527
Source Host           : localhost:3306
Source Database       : my_test_project

Target SshServerInfo Type    : MYSQL
Target SshServerInfo Version : 50527
File Encoding         : 65001

Date: 2019-07-07 10:41:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for quartz_schedule_job
-- ----------------------------
DROP TABLE IF EXISTS `quartz_schedule_job`;
CREATE TABLE `quartz_schedule_job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `bean` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `method` varchar(100) DEFAULT NULL COMMENT '方法名',
  `params` mediumtext COMMENT '参数',
  `expression` varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  `status` tinyint(4) DEFAULT NULL COMMENT '任务状态  0 : 正常  1：暂停',
  `remark` varchar(2048) DEFAULT NULL COMMENT '备注',
  `log` text COMMENT '错误日志',
  `del_on_success` int(1) DEFAULT NULL COMMENT '执行成功是否删除该任务  0：删除    1：不删除',
  `allow_concurrent` int(1) DEFAULT '0' COMMENT '是否允许任务并发执行   0：是     1：否',
  `allow_del` int(1) DEFAULT NULL COMMENT '是否允许任务删除     0：是     1：否',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='定时任务';
