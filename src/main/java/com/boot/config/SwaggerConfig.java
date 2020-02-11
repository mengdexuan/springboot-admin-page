package com.boot.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mengdexuan on 2019/10/23 18:41.
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {


	@Bean
	public Docket api() {

		ParameterBuilder ticketPar = new ParameterBuilder();
		List<Parameter> pars = new ArrayList<Parameter>();
		ticketPar.name("loginKey").description("用户登录key")
				.modelRef(new ModelRef("string")).parameterType("header")
				.required(false).build(); //header中的ticket参数非必填，传空也可以
		pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数


		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				// 自行修改为自己的包路径
				.apis(RequestHandlerSelectors.basePackage("com.boot"))
				.paths(PathSelectors.any())
				.build().globalOperationParameters(pars);

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
