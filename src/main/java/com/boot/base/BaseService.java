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
	 * 构造 Specification 查询
	 * @param specification
	 * @return
	 */
	T one(Specification<T> specification);


	/**
	 * 查询对象（按属性值等于null查询）
	 * @param property
	 * @return
	 */
	T getByFieldIsNull(String property);


	/**
	 * 查询对象(按属性值等于查询)
	 * @param property
	 * @param obj
	 * @return
	 */
	T getByFieldEqual(String property,Object obj);

	/**
	 * 查询对象(按属性值不等于查询)
	 * @param property
	 * @param obj
	 * @return
	 */
	T getByFieldNotEqual(String property,Object obj);


	/**
	 * 查询对象（按属性值不等于null查询）
	 * @param property
	 * @return
	 */
	T getByFieldIsNotNull(String property);

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
	List<T> listByFieldIn(String property, List<?> list);


	/**
	 * 查询列表(按属性值等于查询)
	 * @param property
	 * @param val
	 * @param sorts
	 * @return
	 */
	List<T> listByFieldEqual(String property, Object val,Sort... sorts);

	/**
	 * 查询列表(按属性值like查询)
	 * @param property
	 * @param val
	 * @param sorts
	 * @return
	 */
	List<T> listByFieldLike(String property, Object val,Sort... sorts);

	/**
	 * 查询列表(按属性值不等于查询)
	 * @param property
	 * @param val
	 * @param sorts
	 * @return
	 */
	List<T> listByFieldNotEqual(String property, Object val,Sort... sorts);


	/**
	 * 查询列表（按属性值等于null查询）
	 * @param property
	 * @param sorts
	 * @return
	 */
	List<T> listByFieldIsNull(String property, Sort... sorts);

	/**
	 * 查询列表（按属性值不等于null查询）
	 * @param property
	 * @param sorts
	 * @return
	 */
	List<T> listByFieldIsNotNull(String property, Sort... sorts);



	/**
	 * 查询列表（jpa between...and 的方式）
	 * @param property	属性名称
	 * @param begin	起始值
	 * @param end	结束值
	 * @return
	 */
	List<T> listByFieldBetween(String property, Comparable begin, Comparable end,Sort... sorts);


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

	void flush();

	boolean exists(Specification<T> specification);

	long count(T t);

	boolean exists(T t);

	boolean existsById(Long id);

	Page<T> findAll(Specification<T> var1, Pageable var2);

}
