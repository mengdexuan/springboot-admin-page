package com.boot.biz.test;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.boot.base.NativeSqlQueryServices;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.base.annotation.PrintTime;
import com.boot.base.job.service.JobService;
import com.boot.biz.mail.send.MailSendService;
import com.boot.biz.validation.ValidatedBean;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.common.collect.Lists;
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

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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


	@GetMapping(value = "/download")
	public void download(HttpServletResponse response, String audioPath) throws Exception {

		ClassPathResource file = getTempFile();

		String filename = file.getName();
		filename = java.net.URLEncoder.encode(filename, "UTF-8");

		response.setHeader("Content-Disposition", "filename=" + filename);
		response.setCharacterEncoding("utf-8");
		ServletOutputStream out = response.getOutputStream();

		InputStream in = file.getStream();

//		IoUtil.copyByNIO(in, out, 1024, (StreamProgress) null);
//		IoUtil.close(in);
//		IoUtil.close(out);

		byte[] b = new byte[1024];
		int len;
		//从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
		while ((len = in.read(b)) > 0) {
			out.write(b, 0, len);
		}
		in.close();
		out.close();
	}


	//临时文件
	private ClassPathResource getTempFile(){
		List<ClassPathResource> tempList = Lists.newLinkedList();

		ClassPathResource classPathResource = new ClassPathResource("audio/川井憲次大団円.mp3");
		ClassPathResource classPathResource2 = new ClassPathResource("audio/纯音乐温馨时刻.wma");

		tempList.add(classPathResource);
		tempList.add(classPathResource2);

		int index = RandomUtil.randomInt(0, 2);

		return tempList.get(index);
	}


	@GetMapping("/test1")
	@ResponseBody
	public Result test1() throws Exception {
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

			com.boot.biz.mail.receive.MailUtil.parseMessage(msg);

			ThreadUtil.safeSleep(2000);
		}


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
