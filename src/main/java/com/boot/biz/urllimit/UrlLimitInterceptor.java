package com.boot.biz.urllimit;

import cn.hutool.json.JSONUtil;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.base.exception.ErrorStatus;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author mengdexuan on 2019/6/23 16:01.
 */
@Slf4j
@Component
public class UrlLimitInterceptor implements HandlerInterceptor {

	//获取许可超时时间
	private static Integer acquireTimeOut = 100;


	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

		NativeWebRequest webRequest = new ServletWebRequest(httpServletRequest);

		//获取请求接口路径
		String url = (String) webRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

		String urlKey = UrlRateLimiterCache.urlKey(url);

		RateLimiter rateLimiter = UrlRateLimiterCache.get(urlKey);

		boolean flag = true;

		if (rateLimiter==null){
			//没有限流
		}else {
//			if (rateLimiter.tryAcquire(acquireTimeOut, TimeUnit.MILLISECONDS)){
			if (rateLimiter.tryAcquire()){

			}else {
				flag = false;
				log.error("接口 {} 超出请求限制！",url);
			}
		}

		if (!flag){
			Result result = ResultUtil.buildFailure(ErrorStatus.URL_REQUEST_LIMIT);
			String json = JSONUtil.toJsonStr(result);

			httpServletResponse.setHeader("Content-type", "text/html;charset=UTF-8");
			httpServletResponse.setCharacterEncoding("UTF-8");
			PrintWriter pw = httpServletResponse.getWriter();
			pw.write(json);
		}

//		如果返回false，表示请求结束，后续的Interceptor和Controller都不会再执行了
		return flag;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
	}
}
