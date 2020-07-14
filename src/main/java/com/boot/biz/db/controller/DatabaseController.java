package com.boot.biz.db.controller;

import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import com.boot.base.util.HelpMe;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.*;

@RequestMapping("/back/db")
@Controller
@Slf4j
public class DatabaseController {

	private static OsInfo osInfo = SystemUtil.getOsInfo();

	@Autowired
	DataSource dataSource;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Value("${spring.datasource.password}")
	String dbPwd;

	@RequestMapping("/")
	public String index(Model model) throws Exception {

		Connection conn = dataSource.getConnection();

		DatabaseMetaData metaData = conn.getMetaData();

		String url = metaData.getURL();
		String userName = metaData.getUserName();
		String version = metaData.getDatabaseProductVersion();

		model.addAttribute("url",url);
		model.addAttribute("userName",userName);
		model.addAttribute("password",dbPwd);
		model.addAttribute("version",version);

		conn.close();

		return "back/db/db";
	}


	@RequestMapping("/tables")
	@ResponseBody
	public Object getTables(HttpServletRequest request) throws Exception {

		Connection conn = dataSource.getConnection();

		String tableName = request.getParameter("tableName");
		log.info("tableName --> "+tableName);
		List<String> tableNameList = HelpMe.getTablesFromConnection(conn);
		List<String> tmp = new ArrayList<>();

		if (HelpMe.isNotNull(tableName)){
			/*tableNameList = tableNameList.stream()
					.filter(name -> {
						return name.startsWith(tableName);
					}).collect(toList());*/
			for (String table:tableNameList){
				if (table.startsWith(tableName)){
					tmp.add(table);
				}
			}
		}else {
			tmp = tableNameList;
		}

		//逻辑分页
//		List<List<String>> result = StreamTools.pageByNum(tableNameList, 6);
		List<List<String>> result = Lists.partition(tmp,6);

		conn.close();

		return result;
	}



	@RequestMapping("/results")
	@ResponseBody
	public Object getResults(HttpServletRequest request) throws Exception {

		String sql = request.getParameter("sql");
		sql = sql.trim();
		log.info("传入 sql --> "+sql);
		if (HelpMe.isNull(sql))return null;
		if (sql.endsWith(";")){
			sql = HelpMe.removeLastChar(sql);
		}
		String line = osInfo.getLineSeparator();
		List<String> sqlList = HelpMe.easyStr2List(sql,line);

		StringBuffer sb = new StringBuffer();
		for (String temp:sqlList){
			if (temp.trim().startsWith("--")){// sql 注释
			}else {
				sb.append(temp.trim());
				sb.append(" ");
			}
		}
		sql = sb.toString();
		log.info("处理后 sql --> "+sql);
		if (HelpMe.isNull(sql))return null;

		Map<String, Object> result = new HashMap<>();
		boolean flag = false;

		long time1 = System.currentTimeMillis();
		if (sql.toLowerCase().startsWith("select")){
			flag = true;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
			if (HelpMe.isNotNull(list)){
				Map<String, Object> columns = list.get(0);
				Set<String> set = columns.keySet();
				result.put("columns",set);
				List<List> temp = new ArrayList<>();
				for (Map<String, Object> record:list){
					temp.add(Arrays.asList(record.values().toArray()));
				}
				result.put("data",temp);
			}else {
				result.put("columns",null);
				result.put("data",null);
			}
		}else{
			int count = jdbcTemplate.update(sql);
			result.put("data",count);
		}
		long time2 = System.currentTimeMillis();
		result.put("sql",sql);
		result.put("time",(time2-time1));
		result.put("flag",flag);
		return result;
	}




}



















