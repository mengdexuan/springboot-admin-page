package com.boot.biz.keyval.service;


import com.boot.base.BaseService;
import com.boot.biz.keyval.entity.KeyVal;

import java.util.List;


public interface KeyValService extends BaseService<KeyVal> {


	/**
	 * 删除数据
	 * @param dataId
	 */
	public void delByDataId(String dataId);

	/**
	 * 通过数据ID，获取到指定对象
	 * @param dataId
	 * @param beanClass
	 * @param <T>
	 * @return
	 */
	public <T> T getByDataId(String dataId,Class<T> beanClass);

	/**
	 * 通过数据key，获取到指定对象列表
	 * @param dataKey
	 * @param beanClass
	 * @param <T>
	 * @return
	 */
	public <T> List<T> getByDataKey(String dataKey, Class<T> beanClass);

}
