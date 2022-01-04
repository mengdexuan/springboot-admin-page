package com.boot.biz.test;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.boot.base.NativeSqlQueryServices;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.base.annotation.PrintTime;
import com.boot.base.job.service.JobService;
import com.boot.base.mail.send.MailSendService;
import com.boot.biz.validation.ValidatedBean;
import com.drew.imaging.ImageProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
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

	@Autowired
	Environment environment;




	@GetMapping("/test1")
	@ResponseBody
	public Result test1(String name) throws Exception {
		Object obj = null;

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






	public static void main1(String[] args) throws Exception{


//		host: smtp.163.com    #客户端授权码
//		username: mengdexuan_test@163.com   #发件人邮箱
//		password: YAIMDENAYOULWVWH  #客户端授权码
//		port: 465

		MailAccount account = new MailAccount();
		account.setHost("mail.pop3.host");
		account.setUser("mengdexuan_test@163.com");
		account.setPass("YAIMDENAYOULWVWH");
		account.setPort(110);
		account.setAuth(true);

		Session session = MailUtil.getSession(account, true);
		Store store = session.getStore("pop3");
		store.connect(account.getHost(),account.getUser(),account.getPass());

		while (true){

			Folder folder = store.getFolder("INBOX");

			Message[] msg = folder.getMessages();

			com.boot.base.mail.receive.MailUtil.parseMessage(msg);

			ThreadUtil.safeSleep(2000);
		}


	}


	public static void test2() throws Exception{

		String str = "abc";

		Integer abc = 123;
		byte[] test = abc.toString().getBytes();


		System.out.println();
	}



	public static void main(String[] args) throws ImageProcessingException,IOException {

		String str = "2021-09-10T00:07:57.352779975+00:00";

		String temp = str.split("\\.")[0];

		DateTime date = DateUtil.parse(temp, DatePattern.UTC_SIMPLE_FORMAT);

		System.out.println(temp);
	}






}
