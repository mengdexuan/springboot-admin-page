package com.boot.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReflectUtil;
import com.boot.base.exception.GlobalServiceException;
import com.boot.base.util.HelpMe;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基础服务实现类
 */
@SuppressWarnings("rawtypes")
@Slf4j
public class BaseServiceImpl<T, R extends BaseRepository<T>> implements BaseService<T> {

	@Autowired
	protected R repository;

	@Autowired
	EntityManager em;



	/**
	 * 执行本地sql查询
	 * @param sql
	 * @param tClass
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


	public long count(T t){
		Example<T> example = Example.of(t);
		long count = repository.count(example);
		return count;
	}

	public boolean exists(T t){
		Example<T> example = Example.of(t);
		boolean exists = repository.exists(example);
		return exists;
	}

	public boolean existsById(Long id){
		return repository.existsById(id);
	}

	@Override
	public T get(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(T t) {
		repository.delete(one(t));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Iterable<T> entities) {
		repository.deleteAll(entities);
	}

	/**
	 * 更新对象
	 * 		1.先通过传入的对象的ID（修改时主键ID必传）查询出数据库中的对象
	 * 		2.再用传入的对象的属性值更新数据库中对象的值
	 * 		3.使用repository.save(persistentObj)将新值更新到数据库
	 * @param t
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(T t) {
		T target = null;

		try {
			target = ((Class<T>)t.getClass()).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		BeanUtil.copyProperties(t,target);

		Field[] fieldArr = ReflectUtil.getFields(t.getClass());

		//过滤掉 final 字段、static 字段
		List<Field> fields = Arrays.stream(fieldArr).filter(field -> {
			String str = Modifier.toString(field.getModifiers());
			if (str.contains("final") || str.contains("static")) {
				return false;
			} else {
				return true;
			}
		}).collect(Collectors.toList());


		//过滤掉 Transient 注解标记的字段
		fields = fields.stream().filter(field->{
			Transient temp = field.getAnnotation(Transient.class);
			if (temp==null){
				return true;
			}else {
				return false;
			}
		}).collect(Collectors.toList());


		for (Field field:fields){
			Id id = field.getAnnotation(Id.class);
			//非主键字段的值全部设置为null
			if (id==null){
				try {
					ReflectUtil.setFieldValue(target,field,null);
				} catch (UtilException e) {
				}
			}else {
				Object val = ReflectUtil.getFieldValue(t, field);
				if (val==null){
					throw new GlobalServiceException("修改数据，id 必填！");
				}
			}
		}

		//只通过主键查询数据库中的对象
		T persistentObj = this.one(target);

		for (Field field:fields){
			Object val = ReflectUtil.getFieldValue(t, field);
			//如果传入的字段不为空，设置到查询出的数据库对象中
			if (val!=null){
				try {
					ReflectUtil.setFieldValue(persistentObj,field,val);
				} catch (UtilException e) {
				}
			}
		}

		repository.save(persistentObj);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(T t) {
		repository.save(t);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public T saveAndFlush(T t) {
		return repository.saveAndFlush(t);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(Iterable<T> entities) {
		repository.saveAll(entities);
	}


	@Override
	public T one(Specification<T> specification) {
		return this.repository.findOne(specification).orElse(null);
	}

	@Override
	public T one(String property, Object value,Boolean equal) {
		Specification<T> specification;
		if (equal){
			if (value==null){
				specification = (root, query, builder) -> builder.isNull(root.get(property));
			}else {
				specification = (root, query, builder) -> builder.equal(root.get(property), value);
			}
		}else {
			if (value==null){
				specification = (root, query, builder) -> builder.isNotNull(root.get(property));
			}else {
				specification = (root, query, builder) -> builder.notEqual(root.get(property), value);
			}
		}

		return this.one(specification);
	}


	@Override
	public List<T> list(String property, List<?> list) {

		if (HelpMe.isNull(list)){
			return Lists.newArrayList();
		}
		Specification specification = new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

				List<Predicate> predicates = new ArrayList<>();

				Expression<String> exp = root.<String>get(property);
				Predicate p = exp.in(list);
				predicates.add(p);

				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		};

		return this.findAll(specification);
	}



	@Override
	public List<T> list(String property, Object val,Boolean equal) {

		Specification specification = new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

				List<Predicate> predicates = new ArrayList<>();

				Predicate p;

				if (equal){
					if (val==null){
						p = criteriaBuilder.isNull(root.get(property));
					}else {
						p = criteriaBuilder.equal(root.get(property), val);
					}
				}else {
					if (val==null){
						p = criteriaBuilder.isNotNull(root.get(property));
					}else {
						p = criteriaBuilder.notEqual(root.get(property), val);
					}
				}

				predicates.add(p);

				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		};

		return this.findAll(specification);
	}


	@Override
	public List<T> list(String property, Comparable begin,Comparable end) {
		Specification specification = new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

				List<Predicate> predicates = new ArrayList<>();

				predicates.add(criteriaBuilder.between(root.get(property),begin,end));

				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		};

		return this.findAll(specification);
	}



	@Override
	public T one(T t) {
		List<T> list = this.list(t);
		if (HelpMe.isNotNull(list)){
			return list.get(list.size()-1);
		}
		return null;
	}

	@Override
	public List<T> list(T t) {
		Example<T> example = Example.of(t);
		List<T> list = this.repository.findAll(example);
		return list;
	}

	/**
	 * 按实体对象中不为null的属性查询，多个属性是 and 关系，带有分页
	 *
	 * @param t
	 * @param pageable
	 * @return
	 */
	@Override
	public Page<T> list(T t, Pageable pageable) {
		Example<T> example = Example.of(t);
		Page<T> page = this.repository.findAll(example, pageable);
		return page;
	}

	/**
	 * 根据Map获取实体
	 * Map:
	 * key			val
	 * 属性名称		属性值
	 *
	 * @param map
	 * @return
	 */
	@Override
	public T one(Map<String, Object> map,Class<T> bean) {
		List<T> list = this.list(map, bean);
		if (HelpMe.isNotNull(list)){
			return list.get(list.size()-1);
		}
		return null;
	}

	/**
	 * 根据Map获取实体
	 * Map:
	 * key			val
	 * 属性名称		属性值
	 *
	 * @param map
	 * @return
	 */
	@Override
	public List<T> list(Map<String, Object> map, Class<T> bean) {
		T instance = null;
		try {
			instance = bean.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		Method[] methods = ReflectUtil.getMethodsDirectly(bean,false);

		List<Method> methodList = Arrays.stream(methods).filter(method -> {
			return method.getName().startsWith("set");
		}).collect(Collectors.toList());


		T finalInstance = instance;
		methodList.stream().forEach(method -> {
			ReflectUtil.invoke(finalInstance,method,new Object[]{null});
		});

		BeanUtil.fillBeanWithMap(map,instance,true);

		return this.list(instance);
	}

	@Override
	public void flush() {
		this.repository.flush();
	}

	@Override
	public List<T> findAll() {
		return this.repository.findAll();
	}

	@Override
	public List<T> findAll(Specification<T> specification) {
		return this.repository.findAll(specification);
	}

	@Override
	public boolean exists(Specification<T> specification) {
		return this.repository.count(specification) > 0;
	}






}
