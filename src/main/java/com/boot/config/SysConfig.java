package com.boot.config;

import com.boot.biz.dict.entity.DictKey;
import com.boot.biz.dict.service.DictService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 系统配置
 * @author mengdexuan on 2019/4/20 17:01.
 */
@Configuration
@Data
public class SysConfig {

	@Autowired
	private DictService dictService;

	public Boolean loginInterceptor;
	public Integer loginTimeOut;

	@PostConstruct
	private void init(){
		String loginInterceptorTemp = dictService.dictVal(DictKey.login_interceptor);
		String loginTimeOutTemp = dictService.dictVal(DictKey.login_time_out);

		loginInterceptor = Boolean.parseBoolean(loginInterceptorTemp);
		loginTimeOut = Integer.parseInt(loginTimeOutTemp);
	}





}
