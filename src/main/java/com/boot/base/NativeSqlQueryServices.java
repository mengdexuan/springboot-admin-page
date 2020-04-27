package com.boot.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.boot.base.util.HelpMe;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 本地sql查询
 *
 * @author mengdexuan on 2020/2/11 15:49.
 */
@Slf4j
@Service
public class NativeSqlQueryServices {

	@Autowired
	EntityManager em;


	/**
	 * 执行本地sql查询
	 * @param sql
	 * @return
	 */
	public List<Map> query(String sql) {

		Query query = em.createNativeQuery(sql);

		//将查询结果封装到Map中
		List<Map> list = query.unwrap(NativeQueryImpl.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();

		log.info("执行本地sql查询：{}  查询结果：{}",sql,list);

		return list;
	}



	/**
	 * 执行本地sql查询
	 * @param sql
	 * @param tClass 查询结果封装成指定的类
	 * @return
	 */
	public <X> List<X> query(String sql,Class<X> tClass) {

		Query query = em.createNativeQuery(sql);

		//将查询结果封装到Map中
		List<Map> list = query.unwrap(NativeQueryImpl.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();

		List<X> result = Lists.newArrayList();

		//将Map转换为Bean
		for (Map map:list){
			X bean = BeanUtil.mapToBean(map, tClass, true);
			result.add(bean);
		}

		log.info("执行本地sql查询：{}  查询结果：{}",sql,result);

		return result;
	}


	/**
	 * 执行本地sql更新
	 * @param sql
	 * @return
	 */
	@Transactional
	public int update(String sql) {
		Query query = em.createNativeQuery(sql);
		int count = query.executeUpdate();

		log.info("执行本地sql更新：{}  影响的行数：{}",sql,count);

		return count;
	}



	/**
	 * 根据给定的 sql ，查询数据条数
	 * @param sql，eg: select count(*) from sys_dictionary where id > 9;
	 * @return
	 */
	public long count(String sql) {
		Query query = em.createNativeQuery(sql);

		//将查询结果封装到Map中
		List<Map> list = query.unwrap(NativeQueryImpl.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();

		if (HelpMe.isNotNull(list)){
			Map map = list.get(0);
			Collection coll = map.values();
			Object obj = CollUtil.get(coll, 0);

			return Long.parseLong(obj.toString());
		}

		return 0;
	}


	/**
	 * 根据给定的 sql ，查询1个字符串值
	 * @param sql，eg：select dict_key from sys_dictionary where id = 1;
	 * @return
	 */
	public String queryStr(String sql) {
		List<String> list = queryStrList(sql);

		if (HelpMe.isNotNull(list)){
			return list.get(0);
		}

		return "";
	}



	/**
	 * 根据给定的 sql ，查询字符串值列表
	 * @param sql，eg：select dict_key from sys_dictionary where id > 1;
	 * @return
	 */
	public List<String> queryStrList(String sql) {
		List<String> result = Lists.newArrayList();

		Query query = em.createNativeQuery(sql);

		//将查询结果封装到Map中
		List<Map> list = query.unwrap(NativeQueryImpl.class)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();

		if (HelpMe.isNotNull(list)){
			for (Map map:list){
				Collection coll = map.values();
				Object obj = CollUtil.get(coll, 0);
				String val = obj == null ? "" : obj.toString();
				result.add(val);
			}
		}

		return result;
	}



	/**
	 * 根据给定的 sql ，查询1个字int值
	 * @param sql，eg：select id from sys_dictionary where dict_key = 'login_code';
	 * @return
	 */
	public Integer queryInt(String sql){
		String str = queryStr(sql);
		if (HelpMe.isNull(str)){
			return null;
		}else {
			return Integer.parseInt(str);
		}
	}

}
