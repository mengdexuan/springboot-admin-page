package com.boot.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 基础服务接口类，将DAO层的方法进行提升
 * @param <T>
 */
public interface BaseService<T>{

	T get(Long id);

	@Transactional(rollbackFor = Exception.class)
	void delete(Long id);

	@Transactional(rollbackFor = Exception.class)
	void delete(T t);

	@Transactional(rollbackFor = Exception.class)
	void delete(Iterable<T> entities);

	@Transactional(rollbackFor = Exception.class)
	T update(T t);

	@Transactional(rollbackFor = Exception.class)
	void save(T t);

	/**
	 * 保存后，可以返回自增ID
	 * @param t
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	T saveAndFlush(T t);

	@Transactional(rollbackFor = Exception.class)
	void save(Iterable<T> entities);


	/**
	 * 根据Map获取实体
	 * Map:
	 * 		key			val
	 * 		属性名称		属性值
	 * @param map
	 * @return
	 */
	T one(Map<String, Object> map, Class<T> bean);

	/**
	 * 构造 Specification 查询
	 * @param specification
	 * @return
	 */
	T one(Specification<T> specification);

	/**
	 * 查询对象
	 * @param property	属性名称
	 * @param value		属性值
	 * @param equalArr 	true:按属性值等于查询		false：按属性值不等于查询，不传则默认为 true
	 * @return
	 */
	T one(String property, Object value, Boolean... equalArr);

	/**
	 * 按实体对象中不为null的属性查询，多个属性是 and 关系
	 * @param t
	 * @return
	 */
	T one(T t);




	/**
	 * 查询列表（jpa in 的方式）
	 * @param property	属性名称
	 * @param list	属性值
	 * @return
	 */
	List<T> findByFieldIn(String property, List<?> list);


	/**
	 * 查询列表（true:按属性值等于查询		false：按属性值不等于查询）
	 * @param property	属性名称
	 * @param val	属性值
	 * @param equalArr 	true:按属性值等于查询		false：按属性值不等于查询，不传则默认为 true
	 * @return
	 */
	List<T> list(String property, Object val, Boolean... equalArr);

	/**
	 * 查询列表（jpa between...and 的方式）
	 * @param property	属性名称
	 * @param begin	起始值
	 * @param end	结束值
	 * @return
	 */
	List<T> findByFieldBetween(String property, Comparable begin, Comparable end);


	/**
	 * 按实体对象中不为null的属性查询，多个属性是 and 关系
	 * @param t
	 * @return
	 */
	List<T> list(T t);

	/**
	 * 按实体对象中不为null的属性查询，多个属性是 and 关系，带有分页
	 * @param t
	 * @return
	 */
	Page<T> list(T t, Pageable pageable);



	/**
	 * 根据Map获取实体
	 * Map:
	 * 		key			val
	 * 		属性名称		属性值
	 * @param map
	 * @return
	 */

	List<T> list(Map<String, Object> map, Class<T> bean);


	/**
	 * 查询所有
	 * @return
	 */
	List<T> findAll();

	List<T> findAll(Sort sort);

	/**
	 * 按 Specification 查询所有
	 * @param specification
	 * @return
	 */
	List<T> findAll(Specification<T> specification);

	Page<T> findAll(@Nullable Specification<T> var1, Pageable var2);


	void flush();

	boolean exists(Specification<T> specification);

	long count(T t);

	boolean exists(T t);

	boolean existsById(Long id);

}
