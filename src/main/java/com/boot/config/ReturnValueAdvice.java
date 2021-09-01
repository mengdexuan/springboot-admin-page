package com.boot.config;

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
public class ReturnValueAdvice  implements ResponseBodyAdvice<Object> {

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

		Object requestBody = request.getAttribute("requestBody");
		String url = request.getRequestURI();

		log.info("请求地址：{}，请求(url中)参数：{}，请求(@PathVariable)参数：{}，(post)请求体：{}，响应体：{}",
				url,queryParam,pathMap,requestBody,responseBody);

		return o;
	}


}
