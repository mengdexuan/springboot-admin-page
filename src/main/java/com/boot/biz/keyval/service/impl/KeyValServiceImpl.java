package com.boot.biz.keyval.service.impl;

import com.boot.base.BaseServiceImpl;
import com.boot.biz.keyval.entity.KeyVal;
import com.boot.biz.keyval.repository.KeyValRepository;
import com.boot.biz.keyval.service.KeyValService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyValServiceImpl extends BaseServiceImpl<KeyVal,KeyValRepository> implements KeyValService {


	/**
	 * 删除数据
	 *
	 * @param dataId
	 */
	@Override
	public void delByDataId(String dataId) {
		KeyVal temp = new KeyVal();
		temp.setDataId(dataId);
		delete(temp);
	}

	/**
	 * 通过数据ID，获取到指定对象
	 *
	 * @param dataId
	 * @param beanClass
	 * @return
	 */
	@Override
	public <T> T getByDataId(String dataId, Class<T> beanClass) {

		KeyVal temp = new KeyVal();
		temp.setDataId(dataId);

		KeyVal keyVal = this.one(temp);

		return keyVal.trans(beanClass);
	}


	/**
	 * 通过数据key，获取到指定对象列表
	 *
	 * @param dataKey
	 * @param beanClass
	 * @return
	 */
	@Override
	public <T> List<T> getByDataKey(String dataKey, Class<T> beanClass) {
		KeyVal temp = new KeyVal();
		temp.setDataKey(dataKey);

		List<KeyVal> tempList = this.list(temp);

		List<T> list = Lists.newArrayList();
		for (KeyVal keyVal:tempList){
			T t = keyVal.trans(beanClass);
			list.add(t);
		}

		return list;
	}
}
