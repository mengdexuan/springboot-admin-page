package com.boot.biz.db;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QueryResult {
	private String sql;//sql语句
	private Integer updateCount;//影响的行数
	private Boolean hasResultSet;//是否返回 ResultSet
	private Boolean hasDataList;//dataList 是否存在数据
	private List<Map<String, Object>> dataList;
	private Boolean hasErr;//执行sql是否出错
	private String errMsg;//执行sql出错原因
}
