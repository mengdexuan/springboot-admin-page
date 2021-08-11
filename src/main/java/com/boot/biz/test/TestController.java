package com.boot.biz.test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.boot.base.NativeSqlQueryServices;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.base.annotation.PrintTime;
import com.boot.base.job.entity.Job;
import com.boot.base.job.service.JobService;
import com.boot.base.job.example.NoParamJob;
import com.boot.biz.mail.send.MailSendService;
import com.boot.biz.validation.ValidatedBean;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
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
	MailSendService mailSendService;

	@Autowired
	CamelContext camelContext;


	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	DataSource dataSource;

	@Autowired
	JobService jobService;

	@Autowired
	NativeSqlQueryServices nativeSqlQueryServices;

	@GetMapping("/test1")
	@ResponseBody
	public Result test1() throws Exception {
		Object obj = null;

		Job job = jobService.getJob(NoParamJob.class);

		jobService.addJob(job);

		return ResultUtil.buildSuccess(obj);
	}


	@GetMapping(value = "/sendMailTest")
	public Result sendMailTest() {




		return ResultUtil.buildSuccess();
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



	@GetMapping("/test3")
	@ResponseBody
	public Result test3(Long jobId) {
		Object obj = null;

		jobService.delJob(jobId);

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

		System.out.println(DateUtil.now());

	}


	public static void test2() throws Exception{

		String str = "abc";

		Integer abc = 123;
		byte[] test = abc.toString().getBytes();


		System.out.println();
	}



	public static void test() throws ImageProcessingException,IOException {
		File jpegFile = new File("C:\\Users\\18514\\Desktop\\test\\2020-06-24 222643.JPG");


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
