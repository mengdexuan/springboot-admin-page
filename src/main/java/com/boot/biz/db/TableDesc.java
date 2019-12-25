package com.boot.biz.db;

import cn.hutool.core.io.FileUtil;
import lombok.Data;

import java.util.Date;

/**
 * 表详情信息
 * @author mengdexuan on 2019/12/18 10:21.
 */
@Data
public class TableDesc {

	private String id;

	private String dbName;//数据库名称
	private String tableName;//表名
	private Long tableRows;//记录数
	private String engine;//存储引擎
	private Long autoIncrement;//当前自增值
	private Long dataLength;//数据长度
	private String dataLengthStr;//数据长度
	private Date updateTime;//表更新时间
	private Date createTime;//表创建时间
	private String tableComment;//表注释

	public String getDataLengthStr() {
		return FileUtil.readableFileSize(dataLength);
	}

}
