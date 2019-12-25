package com.boot.biz.dict.service.impl;

import com.boot.base.BaseServiceImpl;
import com.boot.biz.dict.entity.Dict;
import com.boot.biz.dict.repository.DictRepository;
import com.boot.biz.dict.service.DictService;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 数据字典表 服务实现类
 * </p>
 *
 * @author adi
 * @since 2019-05-24
 */
@Service
public class DictServiceImpl extends BaseServiceImpl<Dict,DictRepository> implements DictService {

	private Map<String,String> dictCache = Maps.newHashMap();

	@PostConstruct
	private void init(){
		List<Dict> list = findAll();
		for (Dict dict:list){
			dictCache.put(dict.getDictKey(),dict.getDictValue());
		}
	}


	@Override
	public String dictVal(String dictKey) {
		return dictCache.get(dictKey);
	}

	@Override
	public void reloadDictCache() {
		init();
	}

}
