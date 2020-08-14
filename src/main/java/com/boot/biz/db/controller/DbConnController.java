package com.boot.biz.db.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.MySqlUtils;
import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.base.util.HelpMe;
import com.boot.biz.db.*;
import com.boot.biz.db.dto.DbConnDto;
import com.boot.biz.keyval.entity.KeyVal;
import com.boot.biz.keyval.service.KeyValService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mengdexuan on 2019/12/17 14:02.
 */
@Slf4j
@RestController
@RequestMapping("/back/dbConn")
public class DbConnController extends BaseController {


	@Autowired
	KeyValService keyValService;

	@RequestMapping("/async")
	public Object async(DbTree dbTree) {

		log.info("tree 节点信息:{}", dbTree);

		List<DbTree> list = Lists.newArrayList();

		if (dbTree.getId() == null) {//数据库连接

			KeyVal keyVal = new KeyVal();
			keyVal.setDataKey(DbConnDto.dataKey);

			List<KeyVal> tempList = keyValService.list(keyVal);

			list = tempList.stream().map(item -> {
				DbTree temp = new DbTree();
				temp.setIsParent(true);
				temp.setId(item.getDataId());

				DbConnDto dbConnDto = JSONUtil.toBean(item.getDataVal(), DbConnDto.class);

				temp.setName(dbConnDto.getConnName());
				temp.setIconOpen("img/mysql.png");
				temp.setIconClose("img/mysql_close.png");

				return temp;
			}).collect(Collectors.toList());
		} else {

			if (dbTree.getLevel() == 0) {//数据库

				DbConnDto dbConnDto = keyValService.getByDataId(dbTree.getId(), DbConnDto.class);

				List<String> dbNameList = DBMSMetaUtil.dbList(dbConnDto);

				for (String dbName : dbNameList) {
					DbTree temp = new DbTree();
					temp.setIsParent(true);
					temp.setName(dbName);
					temp.setId(dbName + "|" + dbTree.getId());
					temp.setIconOpen("img/db.png");
					temp.setIconClose("img/db_close.png");
					list.add(temp);
				}
			} else if (dbTree.getLevel() == 1) {//数据库表

				List<String> tempList = HelpMe.easySplit(dbTree.getId(), '|');

				String dbName = tempList.get(0);
				String connId = tempList.get(1);

				log.info("数据库名：{},连接id:{}", dbName, connId);

				DbConnDto dbConnDto = keyValService.getByDataId(connId, DbConnDto.class);

				dbConnDto.setDbName(dbName);

				List<String> tableList = DBMSMetaUtil.tableList(dbConnDto);

				for (String tableName : tableList) {
					DbTree temp = new DbTree();
					temp.setIsParent(false);
					temp.setName(tableName);
					temp.setId(tableName + "|" + dbTree.getId());
					temp.setIcon("img/table.png");
					list.add(temp);
				}
			}
		}

		return list;
	}


	@PostMapping("/add")
	public Result add(DbConnDto dbConnDto) {

		String uuid = HelpMe.uuid();

		dbConnDto.setId(uuid);

		KeyVal keyVal = new KeyVal();
		keyVal.setCreateTime(new Date());
		keyVal.setDataId(uuid);
		keyVal.setDataKey(DbConnDto.dataKey);
		keyVal.setDataVal(JSONUtil.toJsonStr(dbConnDto));

		keyValService.save(keyVal);

		log.info("添加数据库连接信息：{}", dbConnDto);

		return this.success();
	}


	@PostMapping("/check")
	public Result check(DbConnDto dbConnDto) {

		boolean flag = DBMSMetaUtil.tryLink("mysql", dbConnDto.getIp(), dbConnDto.getPort(), "", dbConnDto.getUserName(), dbConnDto.getPwd());

		if (flag) {
			return success();
		} else {
			return failure();
		}
	}


	/**
	 * 获取数据库中的表记录
	 *
	 * @param dbTree
	 * @return
	 */
	@RequestMapping("/tableList")
	public Result tableList(DbTree dbTree) {

//		buss_base_center|04dffdf337624aedbed79b23f00b9061
		String id = dbTree.getId();
		List<String> tempList = HelpMe.easySplit(id, '|');

		String dbName = tempList.get(0);
		String dataId = tempList.get(1);

		DbConnDto dbConnDto = keyValService.getByDataId(dataId, DbConnDto.class);
		dbConnDto.setDbName(dbName);

		List<TableDesc> list = DBMSMetaUtil.tableDescList(dbConnDto);
		list.stream().forEach(item -> {
			item.setId(item.getTableName() + "|" + id);
		});

		return success(list);
	}


	/**
	 * 操作表
	 *
	 * @param id
	 * @param type 1:删除表		2：复制表		3:修改表名称及表注释	4：数据库表字段
	 * @return
	 */
	@PostMapping("/operTable")
	public Result delTable(String id, Integer type, TableDesc tableDesc) {

//		adi_attachment|dev_pbank|a16eeb2d967d45c48d9488be8bd98110

		log.info("操作表，id:{}，type:{}", id, type);

		List<String> tempList = HelpMe.easySplit(id, '|');
		String tableName = tempList.get(0);
		String dbName = tempList.get(1);
		String connId = tempList.get(2);

		DbConnDto dbConnDto = keyValService.getByDataId(connId, DbConnDto.class);
		dbConnDto.setDbName(dbName);
		dbConnDto.setTableName(tableName);

		boolean flag = false;

		String sql = "";

		if (type == 1) {//删除表
			sql = "drop table `{}`";
			sql = StrUtil.format(sql, tableName);
			flag = DBMSMetaUtil.update(dbConnDto, sql);
		} else if (type == 2) {//复制表
			String newTable = tableName + "_copy";

			sql = "create table `{}` like `{}`";
			sql = StrUtil.format(sql, newTable, tableName);
			flag = DBMSMetaUtil.update(dbConnDto, sql);

			if (!flag) {
				return failure();
			}

			sql = "insert into `{}` select * from `{}`";
			sql = StrUtil.format(sql, newTable, tableName);
			flag = DBMSMetaUtil.update(dbConnDto, sql);
		} else if (type == 3) {//修改表名称及表注释

			log.info("tableName:{},tableComment:{}", tableDesc.getTableName(), tableDesc.getTableComment());

			sql = "alter table `{}` comment '{}'";
			sql = StrUtil.format(sql, tableName, tableDesc.getTableComment().trim());
			flag = DBMSMetaUtil.update(dbConnDto, sql);

			if (!tableName.equals(tableDesc.getTableName().trim())) {
				sql = "rename table `{}` to `{}`";
				sql = StrUtil.format(sql, tableName, tableDesc.getTableName().trim());
				flag = DBMSMetaUtil.update(dbConnDto, sql);

				if (!flag) {
					return failure();
				}
			}
		} else if (type == 4) {//数据库表字段

			sql = "show full columns from `{}`";
			sql = StrUtil.format(sql, tableName);
			List<Map<String, Object>> list = DBMSMetaUtil.queryInfo(dbConnDto, sql);

			List<TableColumnDesc> columnDescList = Lists.newArrayList();

			for (Map<String, Object> map : list) {
				TableColumnDesc tableColumnDesc = new TableColumnDesc();
				tableColumnDesc.setId(id);
				tableColumnDesc.setField(MapUtil.getStr(map, "Field"));
				tableColumnDesc.setType(MapUtil.getStr(map, "Type"));
				tableColumnDesc.setNullable(MapUtil.getStr(map, "Null"));
				tableColumnDesc.setKey(MapUtil.getStr(map, "Key"));
				tableColumnDesc.setExtra(MapUtil.getStr(map, "Extra"));
				tableColumnDesc.setComment(MapUtil.getStr(map, "Comment"));

				columnDescList.add(tableColumnDesc);
			}

			sql = "show create table `{}`";
			sql = StrUtil.format(sql, tableName);
			Map<String, Object> map = DBMSMetaUtil.queryInfo(dbConnDto, sql).get(0);

			String ddlInfo = MapUtil.getStr(map, "Create Table");

			String formatSql = SQLUtils.formatMySql(ddlInfo);

			log.info("ddlInfo:{}", formatSql);

			Map<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("ddlInfo", formatSql);
			resultMap.put("columnDescList", columnDescList);

			return success(resultMap);
		}


		if (flag) {
			return success();
		} else {
			return failure();
		}
	}


	/**
	 * 导出表结构及数据
	 * @param id
	 */
	@GetMapping("/exportTable/{id}")
	public void exportTable(@PathVariable String id,HttpServletResponse response)throws Exception{

		log.info("操作表，id:{}", id);

		List<String> tempList = HelpMe.easySplit(id, '|');
		String tableName = tempList.get(0);
		String dbName = tempList.get(1);
		String connId = tempList.get(2);

		DbConnDto dbConnDto = keyValService.getByDataId(connId, DbConnDto.class);
		dbConnDto.setDbName(dbName);
		dbConnDto.setTableName(tableName);

		Connection conn = DBMSMetaUtil.getConn(dbConnDto);
		List<String> sqlData = Lists.newArrayList();

		sqlData.add("SET FOREIGN_KEY_CHECKS=0;");
		sqlData.add("DROP TABLE IF EXISTS `" + tableName + "`;");

		try {
			List<String> ddl = MySqlUtils.getTableDDL(conn, Lists.newArrayList(tableName));

			sqlData.add(ddl.get(0) + ";");

			List<Map<String, Object>> mapList = JdbcUtils.executeQuery(conn, "select * from " + tableName, Lists.newArrayList());

			for (Map<String, Object> map : mapList) {
				String tempSql = JdbcUtils.makeInsertToTableSql(tableName, map.keySet());
				List<Object> parameters = new ArrayList(map.values());
				tempSql = tempSql.replaceAll("\\?", "{}");
				List<String> paramList = parameters.stream().map(item -> {
					if (item == null) {
						return null;
					} else {
						return "'" + item.toString() + "'";
					}
				}).collect(Collectors.toList());

				tempSql = StrUtil.format(tempSql, HelpMe.easyList2Arr(paramList)) + ";";

				sqlData.add(tempSql);
			}

		} catch (SQLException e) {
			log.error("导出表失败！", e);
		} finally {
			JdbcUtils.close(conn);
		}

		File file = new File(tableName+".sql");

		FileUtil.writeUtf8Lines(sqlData,file);

		BufferedInputStream in = FileUtil.getInputStream(file);
		ServletOutputStream out = getOut(file.getName(),response);

		IoUtil.copyByNIO(in, out, 1024, (StreamProgress) null);
		IoUtil.close(in);
		IoUtil.close(out);

		//删除生成的临时文件
		FileUtil.del(file);
	}


	//获取输出流
	private ServletOutputStream getOut(String filename, HttpServletResponse response) throws Exception {
		filename = URLEncoder.encode(filename, "UTF-8");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		response.setCharacterEncoding("utf-8");
		return response.getOutputStream();
	}



	@PostMapping("/dbInfo")
	public Result dbInfo(String id) {

//		dev_pbank|a16eeb2d967d45c48d9488be8bd98110
		log.info("id:{}", id);

		List<String> tempList = HelpMe.easySplit(id, '|');
		String dbName = tempList.get(0);
		String connId = tempList.get(1);

		DbConnDto dbConnDto = keyValService.getByDataId(connId, DbConnDto.class);
		dbConnDto.setDbName(dbName);

		return this.success(dbConnDto);
	}


	@PostMapping("/exeSqlScript")
	public Result exeSqlScript(String id, String sql) {

//		dev_pbank|a16eeb2d967d45c48d9488be8bd98110
		log.info("id:{},sql:{}", id, sql);

		List<String> tempList = HelpMe.easySplit(id, '|');
		String dbName = tempList.get(0);
		String connId = tempList.get(1);

		DbConnDto dbConnDto = keyValService.getByDataId(connId, DbConnDto.class);
		dbConnDto.setDbName(dbName);

		List<String> sqlList = HelpMe.easySplit(sql, ';');

		if (HelpMe.isNull(sqlList)) {
			return failure("sql语句为空！");
		}

		List<QueryResult> list = Lists.newArrayList();
		for (String item : sqlList) {
			String tempSql = item.trim();
			if (tempSql.startsWith("--")) {//跳过注释的sql
				continue;
			}
			QueryResult queryResult = DBMSMetaUtil.exeSql(dbConnDto, tempSql);
			list.add(queryResult);
		}

		return this.success(list);
	}


	@PostMapping("/delConn")
	public Result delConn(String id) {

		keyValService.delByDataId(id);

		return success();
	}


	//sql脚本文件存储目录
	public static String sqlScriptDir = "sqlScript";


	@PostMapping(value = "/uploadSqlScript")
	public Result uploadSqlScript(HttpServletRequest request) {

		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("sqlScriptFile");
		MultipartFile file = null;
		BufferedOutputStream out = null;

		file = files.get(0);
		String fileName = file.getOriginalFilename();
		String extName = FileUtil.extName(fileName);

		FileUtil.mkdir(new File(sqlScriptDir));

		String storePath = sqlScriptDir + File.separator + fileName;

		try {
			File sqlScript = new File(storePath);

			log.info("sql脚本文件存储路径：{}", sqlScript.getAbsolutePath());

			out = new BufferedOutputStream(new FileOutputStream(sqlScript));

			InputStream in = file.getInputStream();
			IoUtil.copyByNIO(in, out, 1024, (StreamProgress) null);
			IoUtil.close(in);
			IoUtil.close(out);

		} catch (Exception e) {
			log.error("上传附件失败！", e);
			return failure("上传文件失败！");
		}

		return success();
	}


	@PostMapping(value = "/exeScript")
	public Result exeScript(String id, String fileName) {
		//dev_pbank|a16eeb2d967d45c48d9488be8bd98110，test.sql
		log.info("id:{},fileName:{}", id, fileName);

		String storePath = sqlScriptDir + File.separator + fileName;
		File sqlScript = new File(storePath);

		try {
			List<String> tempList = HelpMe.easySplit(id, '|');
			String dbName = tempList.get(0);
			String connId = tempList.get(1);

			DbConnDto dbConnDto = keyValService.getByDataId(connId, DbConnDto.class);
			dbConnDto.setDbName(dbName);

			Connection conn = DBMSMetaUtil.getConn(dbConnDto);

			FileSystemResource resource = new FileSystemResource(sqlScript);

			ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
			populator.addScripts(resource);

			populator.populate(conn);

			JdbcUtils.close(conn);
		} catch (Exception e) {
			log.error("执行sql脚本失败！" + e.getMessage());

			if (!sqlScript.exists()) {
				return failure("SQL文件不存在，请先上传脚本！");
			}

			return failure(e.getMessage());
		} finally {
			FileUtil.del(sqlScript);
		}

		return success();
	}


}
