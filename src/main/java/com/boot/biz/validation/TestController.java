package com.boot.biz.validation;

import cn.hutool.crypto.SecureUtil;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.base.annotation.PrintTime;
import com.boot.base.util.HelpMe;
import com.boot.biz.springtask.entity.SpringTask;
import com.boot.biz.springtask.service.SpringTaskService;
import com.boot.biz.urllimit.service.UrlLimitService;
import com.boot.biz.userauthgroup.service.UserAuthGroupService;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;

/**
 * @author mengdexuan on 2019/6/19 18:28.
 */
@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

	@Qualifier("taskExecutor")
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

	@Autowired
	SpringTaskService springTaskService;

	@Autowired
	DataSource dataSource;

	@GetMapping("/test1")
	@ResponseBody
	public Result test1(HttpServletRequest request) throws Exception {

		Object obj = null;


		SpringTask springTask = new SpringTask();

		springTask.setTaskId(HelpMe.uuid());
		springTask.setCron("0/1 * * * * *");
		springTask.setStatus(1);
		springTask.setBean("springTaskTest");
		springTask.setMethod("run2");
		springTask.setRemark("测试任务");
		springTask.setName("测试名称");
		springTask.setParams("abc");
		springTask.setCreateTime(new Date());
		springTask.setDelWhenSuccess(1);


		springTaskService.addSpringTask(springTask);


		return ResultUtil.buildSuccess(obj);
	}






	@GetMapping("/test2")
	@ResponseBody
	public Result test2(HttpServletRequest request,String taskId) {

		Object obj = null;


		String sql = "C:\\Users\\18514\\Desktop\\test\\test.sql";
		FileSystemResource resource = new FileSystemResource(sql);

		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScripts(resource);


		DatabasePopulatorUtils.execute(populator,dataSource);

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
