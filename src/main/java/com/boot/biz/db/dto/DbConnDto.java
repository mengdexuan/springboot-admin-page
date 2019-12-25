package com.boot.biz.db.dto;

import lombok.Data;

/**
 * 数据库连接信息
 * @author mengdexuan on 2019/12/17 14:04.
 */
@Data
public class DbConnDto {

	public static final String dataKey = "dbInfo";

	private String id;

	private String dbType;//数据库类型，mysql,oracle,sqlServer等
	private String dbName;//数据库名称
	private String tableName;//表名称
	private String ip;
	private String port;
	private String userName;
	private String pwd;
	private String connName;//连接名称

}
