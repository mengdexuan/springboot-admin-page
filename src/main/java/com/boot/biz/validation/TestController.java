package com.boot.biz.validation;

import cn.hutool.crypto.SecureUtil;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.base.annotation.PrintTime;
import com.boot.biz.urllimit.service.UrlLimitService;
import com.boot.biz.userauthgroup.entity.UserAuthGroup;
import com.boot.biz.userauthgroup.service.UserAuthGroupService;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.impl.EventDrivenConsumerRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author mengdexuan on 2019/6/19 18:28.
 */
@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

	@Autowired
	Executor executor;
	@Autowired
	RequestMappingHandlerMapping handlerMapping;


	@Autowired
	CamelContext camelContext;


	@Autowired
	UrlLimitService urlLimitService;

	@Autowired
	UserAuthGroupService userAuthGroupService;


	@GetMapping("/test1")
	@ResponseBody
	public Result test1(HttpServletRequest request) throws Exception {

		Object obj = null;

		List<Route> routes = camelContext.getRoutes();


		routes.stream().forEach(item->{
			EventDrivenConsumerRoute route = ((EventDrivenConsumerRoute) item);
			log.info("路由：{}，参数：{}",item.getId(),route.getStatus());
		});

		UserAuthGroup userAuthGroup = new UserAuthGroup();
		userAuthGroup.setUid(1L);
		userAuthGroup.setGroupId(2L);

		UserAuthGroup one = userAuthGroupService.saveAndFlush(userAuthGroup);

		log.info("{}",one);


		return ResultUtil.buildSuccess(obj);
	}






	@GetMapping("/test2")
	@ResponseBody
	public Result test2(HttpServletRequest request) {

		Object obj = null;

		String flag = request.getParameter("flag");

		if ("1".equals(flag)){
			try {
				camelContext.stopRoute("route2");
			} catch (Exception e) {
				log.error("停止路由失败！",e);
			}
		}else {

			try {
				camelContext.startRoute("route2");
			} catch (Exception e) {
				log.error("启动路由失败！",e);
			}
		}




		return ResultUtil.buildSuccess(obj);
	}



	@RequestMapping("/test3")
	@ResponseBody
	public Result test3() {
		Object obj = null;

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();




		return ResultUtil.buildSuccess(obj);
	}





	@PrintTime
	@PostMapping("/test4")
	public Result<String> test4(@Validated ValidatedBean validatedBean){


		return ResultUtil.buildSuccess("abc");
	}



	@PrintTime
	@PostMapping("/test5")
	public Result test5(@Validated @RequestBody ValidatedBean validatedBean){



		return ResultUtil.buildSuccess();
	}



	public static void main(String[] args) throws Exception{

	}



	public static void test() throws ImageProcessingException,IOException {
		File jpegFile = new File("C:\\Users\\18514\\Desktop\\test\\IMG_1070.JPG");



		String md5 = SecureUtil.md5(jpegFile);

//		a50064d8e91cc367aa27b6203d01cb86
		System.out.println(md5);

		Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				//格式化输出[directory.getName()] - tag.getTagName() = tag.getDescription()
				System.out.format("[%s] - %s = %s\n",
						directory.getName(), tag.getTagName(), tag.getDescription());
			}
			if (directory.hasErrors()) {
				for (String error : directory.getErrors()) {
					System.err.format("ERROR: %s", error);
				}
			}
		}
	}






}
