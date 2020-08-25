package com.boot.biz.dict.listener;

import com.boot.biz.dict.entity.Dict;
import com.boot.biz.dict.entity.DictKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 数据字典更新监听
 * @author mengdexuan on 2020/3/6 9:32.
 */
@Slf4j
@Component
public class DictUpdateListener {


	/**
	 * 监听字典 key 的修改
	 * @param dict
	 */
	@Async
	@EventListener
	public void listenerFileStorage(Dict dict){
		String key = dict.getDictKey();

		log.info("数据字典修改!");
	}


























}
