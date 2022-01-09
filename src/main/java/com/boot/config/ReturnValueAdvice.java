package com.boot.config;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * ResponseBodyAdvice : 可以在controller的方法在用@ResponseBody把返回值转换为json对象之前捕捉到返回的对象,
 * 以便我们在它内置的方法中去进行统一的更新操作
 *
 * @author mengdexuan on 2019/1/9 16:23.
 */
@Component
@ControllerAdvice
@Slf4j
public class ReturnValueAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		NativeWebRequest webRequest = new ServletWebRequest(request);

		Map<String, String> pathMap = (Map<String, String>) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

		String responseBody = JSONUtil.toJsonStr(o);
		String queryParam = request.getQueryString();

		Object requestBody = request.getAttribute("_inMsg_");
		String url = request.getRequestURI();

		String controllerName = methodParameter.getMember().getDeclaringClass().getName();
		String methodName = methodParameter.getMember().getName();

		String mediaTypeStr = mediaType.toString();

		String ip = ServletUtil.getClientIP(request);

		if (!url.endsWith("error")){
			log.info("\n");
			log.info("客户端IP：{}",ip);
			log.info("请求地址：{}",url);
			log.info("请求参数(url中)：{}",queryParam);
			log.info("请求参数(@PathVariable中)：{}",pathMap);
			log.info("请求媒体类型：{}",mediaTypeStr);
			log.info("请求体(post)：{}",requestBody);
			log.info("响应体：{}",responseBody);
			log.info("请求处理类：{}",controllerName);
			log.info("请求处理方法：{}",methodName);
			log.info("\n");
		}


		return o;
	}


}
