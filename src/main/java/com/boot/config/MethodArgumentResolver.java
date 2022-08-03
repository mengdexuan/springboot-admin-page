package com.boot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author mengdexuan on 2022/8/2 9:56.
 */
@Slf4j
@Component
public class MethodArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        HttpServletRequest servletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);

        Enumeration<String> names = servletRequest.getAttributeNames();
        Map<String, String[]> map = servletRequest.getParameterMap();

        String aesHeader = servletRequest.getHeader("AES");

        return null;
    }
}
