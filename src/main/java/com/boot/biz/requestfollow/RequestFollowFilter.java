package com.boot.biz.requestfollow;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.requestfollow.entity.RequestFollow;
import com.boot.biz.requestfollow.service.RequestFollowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 请求追踪过滤器
 * @author mengdexuan on 2019/12/16 11:51.
 */
@Slf4j
@WebFilter(filterName = "requestFollowFilter",urlPatterns = "/*")
@Configuration
public class RequestFollowFilter implements Filter {


	@Autowired
	RequestFollowService requestFollowService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		Date date = new Date();

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String path = request.getServletPath();

		long time1 = System.currentTimeMillis();
		filterChain.doFilter(request, response);
		long time2 = System.currentTimeMillis();
		Long spendTime = time2 - time1;

		String method = request.getMethod();
		int status = response.getStatus();


		String inStr = "";
		Object inMsg = request.getAttribute("_inMsg_");
		if (inMsg!=null){
			inStr = inMsg.toString();
			request.removeAttribute("_inMsg_");
		}else {
			inStr = JSONUtil.toJsonStr(ServletUtil.getParamMap(request));
		}


		boolean flag = false;

		String outStr = "";
		Object outMsg = request.getAttribute("_outMsg_");
		if (outMsg!=null){
			outStr = outMsg.toString();
			request.removeAttribute("_outMsg_");
			flag = true;
		}
		if (HelpMe.isNull(outStr)){
			outStr = "{}";
		}


		log.info("请求及响应：path:{},method:{},requestParam:{},status:{},spendTime:{},responseMsg:{}",
				path,method,inStr,status,spendTime,outStr);


		//带有 @ResponseBody 注解的 controller 接口，才记录请求响应信息；返回页面跳转的不进行记录
		if (flag){

			if (!path.startsWith("/requestFollow/get/")){
				RequestFollow requestFollow = new RequestFollow();
				requestFollow.setCreateTime(date);
				requestFollow.setPath(path);
				requestFollow.setMethod(method);
				requestFollow.setStatus(status);
				requestFollow.setSpendTime(spendTime.intValue());

				if (inStr.length()>2000){
					inStr = StrUtil.maxLength(inStr,1997);
				}
				if (outStr.length()>2000){
					outStr = StrUtil.maxLength(outStr,1997);
				}
				requestFollow.setRequestParam(inStr);
				requestFollow.setResponseMsg(outStr);

				requestFollowService.save(requestFollow);
			}
		}
	}

	@Override
	public void destroy() {

	}


}
