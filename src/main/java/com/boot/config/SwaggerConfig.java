package com.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author mengdexuan on 2019/10/23 18:41.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {


	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				// 自行修改为自己的包路径
				.apis(RequestHandlerSelectors.basePackage("com.boot"))
				.paths(PathSelectors.any())
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("springboot-admin-page")
				.description("springboot-admin-page项目 API 1.0 操作文档")
				//服务条款网址
				.termsOfServiceUrl("http://www.itangquan.com")
				.version("1.0")
				.contact(new Contact("mengdexuan", "http://www.itangquan.com", "18514756315@163.com"))
				.build();
	}



}
