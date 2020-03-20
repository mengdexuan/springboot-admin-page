package com.boot.config;

import com.boot.biz.urllimit.UrlLimitInterceptor;
import com.boot.biz.user.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author mengdexuan on 2019/6/23 16:18.
 */
@Slf4j
@Configuration
public class WebMvc  implements WebMvcConfigurer {

	@Autowired
	UrlLimitInterceptor urlLimitInterceptor;
	@Autowired
	LoginInterceptor loginInterceptor;
	@Autowired
	SysConfig sysConfig;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(urlLimitInterceptor).addPathPatterns("/**");
		if (sysConfig.loginInterceptor){
			registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
		}
	}


	/**
	 * Add handlers to serve static resources such as images, js, and, css
	 * files from specific locations under web application root, the classpath,
	 * and others.
	 *
	 * @param registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

	}


	/**
	 * 允许所有请求跨域访问
	 *
	 * 当配置自定义的 Interceptor 后，此处的配置就不生效了；需要在 filter 中单独配置，参看：CorsFilterConfig
	 * @param registry
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		/*registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE")
				.maxAge(3600)
				.allowCredentials(true);*/
	}
}
