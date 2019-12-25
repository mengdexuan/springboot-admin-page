package com.boot.config.druidMonitor;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mengdexuan on 2019/12/23 18:56.
 */
@Configuration
public class DruidMonitorConfig {


	/**
	 * 配置Druid监控
	 * 后台管理Servlet
	 * @return
	 */
	@Bean
	public ServletRegistrationBean<StatViewServlet> druidStatViewServlet() {
		ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
		registrationBean.addInitParameter("allow", "");// IP白名单 (没有配置或者为空，则允许所有访问)
		registrationBean.addInitParameter("deny", "");// IP黑名单 (存在共同时，deny优先于allow)
		registrationBean.addInitParameter("loginUsername", "admin");
		registrationBean.addInitParameter("loginPassword", "admin");
//		禁用HTML页面上的“Reset All”功能
		registrationBean.addInitParameter("resetEnable", "false");

		//项目启动后访问监控页面		http://ip:port/{projectName}/druid/login.html

		return registrationBean;

	}


	/**
	 * 配置web监控的filter
	 * @return
	 */
	@Bean
	public FilterRegistrationBean druidStatFilter(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
		//添加过滤规则.
		filterRegistrationBean.addUrlPatterns("/*");
		//添加不需要忽略的格式信息.
		filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		return filterRegistrationBean;
	}




}
