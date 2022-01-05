package com.boot.biz.user;

import cn.hutool.core.util.ReflectUtil;
import com.boot.base.exception.GlobalServiceException;
import com.boot.base.jwt.JwtUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.dict.service.DictService;
import com.boot.biz.index.controller.IndexController;
import com.boot.biz.test.TestController;
import com.boot.biz.user.entity.SysUser;
import com.boot.biz.user.service.UserService;
import com.boot.config.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author mengdexuan on 2019/6/23 16:01.
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	UserService userService;
	@Autowired
	DictService dictService;

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
		boolean flag = true;

		String path = httpServletRequest.getServletPath();

		log.info("请求地址：{}",path);

		RequestUserHolder.add(httpServletRequest);

		if (notInterceptor(path)){
			return flag;
		}else {
			String loginKey = httpServletRequest.getHeader("loginKey");

			if (HelpMe.isNull(loginKey)){//从 header 中取不到，则再从请求参数中取
				loginKey = httpServletRequest.getParameter("loginKey");
			}

			Long userId = JwtUtil.validateTokenAndGetUserId(loginKey);

			if (userId!=null){
				SysUser user = userService.get(userId);
				RequestUserHolder.add(user);
			}else {
				//用户未登录
				flag = false;
			}
		}


		if (!flag){
			remove();

			//10000 是字典内置数据
			String dictVal = dictService.dictVal(SysConfig.err_code_10000);
			throw new GlobalServiceException(Integer.parseInt(SysConfig.err_code_10000),dictVal);
		}

//		如果返回false，表示请求结束，后续的Interceptor和Controller都不会再执行了
		return flag;
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
		remove();
	}

	private void remove(){
		RequestUserHolder.remove();
	}



	//不需要登录就可以直接访问的url
	private boolean notInterceptor(String servletPath){
		if (servletPath.startsWith("/v2/api-docs")||
				servletPath.endsWith("/login") ||
				servletPath.startsWith("/csrf") ||
				ignoreController(TestController.class,servletPath)||
				ignoreController(IndexController.class,servletPath)||
				servletPath.contains("swagger") ||
				servletPath.contains(".") ||
				servletPath.startsWith("/info") ||
				servletPath.startsWith("/error")
				) {

			return true;
		}

		return false;
	}


	/**
	 * 指定controller中的接口方法都不需要登录
	 * @param clazz
	 * @param servletPath
	 * @return
	 */
	private boolean ignoreController(Class clazz,String servletPath){

		RequestMapping classRequestMapping = (RequestMapping)clazz.getDeclaredAnnotation(RequestMapping.class);

		HttpServletRequest request = RequestUserHolder.getCurrentRequest();
		NativeWebRequest webRequest = new ServletWebRequest(request);

		String path = (String) webRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

		String classRequestMappingPath = "";
		if (classRequestMapping!=null){
			classRequestMappingPath = classRequestMapping.value()[0];
		}

		Method[] methods = ReflectUtil.getPublicMethods(clazz);
		for (Method method:methods){
			GetMapping getMapping = method.getAnnotation(GetMapping.class);
			if (getMapping!=null){
				String[] valArr = getMapping.value();
				for (String val:valArr){
					if (servletPath.equals(classRequestMappingPath+val))return true;
					if (path.equals(classRequestMappingPath+val))return true;
				}
			}else {
				PostMapping postMapping = method.getAnnotation(PostMapping.class);
				if (postMapping!=null){
					String[] valArr = postMapping.value();
					for (String val:valArr){
						if (servletPath.equals(classRequestMappingPath+val))return true;
					}
				}else {
					RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
					if (requestMapping!=null){
						String[] valArr = requestMapping.value();
						for (String val:valArr){
							if (servletPath.equals(classRequestMappingPath+val))return true;
						}
					}
				}
			}
		}

		return false;
	}
}
