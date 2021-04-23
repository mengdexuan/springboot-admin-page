CREATE TABLE `attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(64) DEFAULT NULL COMMENT '附件ID',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名',
  `file_ext` varchar(10) DEFAULT NULL COMMENT '文件后缀',
  `file_path` varchar(255) DEFAULT NULL COMMENT '文件存储路径',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `create_time` datetime DEFAULT NULL COMMENT '上传时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='附件';

