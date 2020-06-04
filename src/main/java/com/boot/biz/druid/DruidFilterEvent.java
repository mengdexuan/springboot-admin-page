package com.boot.biz.druid;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import com.boot.base.util.HelpMe;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * druid 提供的sql执行过滤器，可在sql执行前后进行一些控制逻辑，或当特定的表更新时，执行其它相关操作
 * @author mengdexuan on 2018/11/11 21:56.
 */
@Slf4j
@Component
public class DruidFilterEvent extends FilterEventAdapter implements CommandLineRunner {

	@Autowired
	DataSource dataSource;

	@Override
	public void run(String... args) throws Exception {
		DruidDataSource druidDataSource = (DruidDataSource) dataSource;
		druidDataSource.setProxyFilters(Lists.newArrayList(this));
	}

	private void printSql(String sql){
		// 新建 MySQL Parser
		SQLStatementParser parser = new MySqlStatementParser(sql);

		// 使用Parser解析生成AST，这里SQLStatement就是AST
		SQLStatement statement = parser.parseStatement();

		// 使用visitor来访问AST
		MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
		statement.accept(visitor);

		Map<TableStat.Name, TableStat> map = visitor.getTables();

//		log.info("table:{}",map);
	}

	@Override
	public ResultSetProxy preparedStatement_executeQuery(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
		ResultSetProxy resultSetProxy = super.preparedStatement_executeQuery(chain, statement);

		String sql = statement.getSql();
//		String tableName = HelpMe.extractTableNameFromSql(sql);
//		log.info("tableName:{}",tableName);

		printSql(sql);

		return resultSetProxy;
	}


	@Override
	public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
		String tableName = HelpMe.extractTableNameFromSql(sql);

//		log.info("tableName:{}",tableName);
		printSql(sql);

		return super.statement_executeQuery(chain, statement, sql);
	}



	@Override
	public int preparedStatement_executeUpdate(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
		//insert update delete
		int result = super.preparedStatement_executeUpdate(chain, statement);

//		String tableName = HelpMe.extractTableNameFromSql(statement.getSql());
//		log.info("tableName:{}",tableName);
//		log.info("sql:{}",SqlFormatter.format(statement.getSql()));

		printSql(statement.getSql());

		return result;
	}

	@Override
	public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
		//insert update delete
		int result = super.statement_executeUpdate(chain, statement, sql);

//		String tableName = HelpMe.extractTableNameFromSql(sql);
//		log.info("tableName:{}",tableName);

		printSql(sql);

		return result;
	}






}
