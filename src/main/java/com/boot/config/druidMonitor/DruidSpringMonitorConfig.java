package com.boot.config.druidMonitor;

import com.alibaba.druid.support.spring.stat.BeanTypeAutoProxyCreator;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.boot.base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * druid spring监控
 * @author mengdexuan on 2019/12/24 10:44.
 */
@Slf4j
@Configuration
public class DruidSpringMonitorConfig {


	@Bean("druidStatInterceptor")
	public DruidStatInterceptor druidStatInterceptor() {
		DruidStatInterceptor dsInterceptor = new DruidStatInterceptor();
		return dsInterceptor;
	}

	@Bean
	public BeanTypeAutoProxyCreator druidStatPointcut() {

		BeanTypeAutoProxyCreator typeAutoProxyCreator = new BeanTypeAutoProxyCreator();
		typeAutoProxyCreator.setInterceptorNames("druidStatInterceptor");
//		所有BaseService的派生类被拦截监控
		typeAutoProxyCreator.setTargetBeanType(BaseService.class);

		typeAutoProxyCreator.setProxyTargetClass(true);

		return typeAutoProxyCreator;
	}

}
