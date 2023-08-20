package com.boot.biz.test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.word.Word07Writer;
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
import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;
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

	@Autowired
	Environment environment;




	@GetMapping("/test1")
	@ResponseBody
	public Result test1(String name,int age,HttpServletRequest request) throws Exception {
		Object obj = null;

		log.info("name:"+name);
		log.info("age:"+age);

		return ResultUtil.buildSuccess(obj);
	}


	@GetMapping(value = "/test3/{taskCode}/basic")
	public Result test3(@PathVariable("taskCode") String taskCode) {




		return ResultUtil.buildSuccess(taskCode);
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






	public void write() {

		List<String> row0 = CollUtil.newArrayList("冠字号码", "交易时间", "机构号", "设备编号");
		List<String> row1 = CollUtil.newArrayList("aa", "bb", "cc", "dd");
		List<String> row2 = CollUtil.newArrayList("aa1", "bb1", "cc1", "dd1");
		List<String> row3 = CollUtil.newArrayList("aa2", "bb2", "cc2", "dd2");
		List<String> row4 = CollUtil.newArrayList("aa3", "bb3", "cc3", "dd3");
		List<String> row5 = CollUtil.newArrayList("aa4", "bb4", "cc4", "dd4");
		List<List<String>> rows = CollUtil.newArrayList(row0,row1, row2, row3, row4, row5);


		//通过工具类创建writer
		ExcelWriter writer = ExcelUtil.getWriter("C:\\Users\\18514\\Desktop\\test4\\test.xls");
//合并单元格后的标题行，使用默认标题样式
		writer.merge(row1.size() - 1, "数据");
//一次性写出内容，强制输出标题
		writer.write(rows, true);
//关闭writer，释放内存
		writer.close();
	}




	public static void main2(String[] args) throws ImageProcessingException,IOException {

		String str = "2021-09-10T00:07:57.352779975+00:00";

		String temp = str.split("\\.")[0];

		DateTime date = DateUtil.parse(temp, DatePattern.UTC_SIMPLE_FORMAT);

		System.out.println(temp);
	}

	public static void mainaa(String[] args) throws Exception {

		Word07Writer writer = new Word07Writer();

		List<File> list = FileUtil.loopFiles(FileUtil.file("D:\\Project\\zhilian"), new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (FileUtil.extName(pathname).equals("java")) {
					return true;
				}
				return false;
			}
		});

		int i = 1;
		for (File item:list){
			i++;

			if (i>120&&i<150){
				List<String> contentList = FileUtil.readUtf8Lines(item);

				for (String str:contentList){
					writer.addText(new Font("宋体", Font.PLAIN, 10), str);
				}
			}

		}


		writer.flush(FileUtil.file("C:\\Users\\18514\\Desktop\\test3\\b.docx"));
		writer.close();

	}


	public static void main(String[] args) {

		String uploadUrl = "http://test.zjsjwyt2axfs.ticket.iciyun.net/gateway/xfs/exchange";

		Date date = new Date();

		Map<String,Object> param = new HashMap<>();
		param.put("orderId","asfasf");
		param.put("userId","1234");
		param.put("orderAmount","123");
		param.put("orderTime","2023-07-11 15:11:56");
		param.put("shopOrgNum","asfasfasfsaf");

		HttpRequest post = HttpUtil.createPost(uploadUrl);

		post.header("time",date.getTime()+"");
		post.header("route","/syncOrder");

		String result = post.execute().body();

		System.out.println(result);

	}



	public static void main3(String[] args) throws Exception {
		ExcelReader reader = ExcelUtil.getReader(FileUtil.file("C:\\Users\\18514\\Desktop\\test3\\经销商采购数据模版.xlsx"));

		int rowCount = reader.getRowCount();


		int startRow = 2;

		for (int rowIndex=startRow;rowIndex<rowCount;rowIndex++){

			List<Object> tempList = reader.readRow(rowIndex);

			if (CollUtil.isNotEmpty(tempList)){
				log.info("{}",tempList);
			}
		}

		System.out.println(rowCount);
	}







}
