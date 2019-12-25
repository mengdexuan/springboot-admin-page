package com.boot.config;

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

	public static String err_code_10000 = "10000";//数据字典内置数据：登录超时错误码
	public static String login_time_out = "login_time_out";//数据字典内置数据：登录超时时间，默认30分钟
	public static String err_code_10001 = "10001";//数据字典内置数据：登录名或密码错误
	public static String login_interceptor = "login_interceptor";//数据字典内置数据：登录拦截

	@Autowired
	private DictService dictService;

	public Boolean loginInterceptor;
	public Integer loginTimeOut;

	@PostConstruct
	private void init(){
		String loginInterceptorTemp = dictService.dictVal(login_interceptor);
		String loginTimeOutTemp = dictService.dictVal(login_time_out);

		loginInterceptor = Boolean.parseBoolean(loginInterceptorTemp);
		loginTimeOut = Integer.parseInt(loginTimeOutTemp);
	}





}
