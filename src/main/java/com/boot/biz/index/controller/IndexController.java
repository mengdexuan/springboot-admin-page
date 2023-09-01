package com.boot.biz.index.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.system.SystemUtil;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.base.job.entity.Job;
import com.boot.base.job.service.JobService;
import com.boot.base.util.HelpMe;
import com.boot.biz.dict.entity.Dict;
import com.boot.biz.dict.service.DictService;
import com.boot.biz.index.dto.ConfigFileDto;
import com.boot.biz.serverinfo.Server;
import com.boot.biz.serverinfo.ServerController;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
public class IndexController {

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	Environment environment;


	@Autowired
	JobService jobService;

	@Autowired
	CamelContext camelContext;

	@Autowired
	DictService dictService;


	@GetMapping("/")
	@ResponseBody
	public Result index() {

//		Server serverInfo = ServerController.serverInfo();
//		serverInfo.setPid(SystemUtil.getCurrentPID());

		return ResultUtil.buildSuccess();
	}



	@GetMapping("/back/jump2Page")
	public String jump2Page(String page) {

		log.info("跳转至页面：{}",page);

		return "back/"+page;
	}


	@RequestMapping("/back/test")
	public String test(Model model) {

		log.info("测试页面");

		return "back/test/webSocketTest";
	}



	@ResponseBody
	@GetMapping("/initLog")
	public String initLog() {

		return "ok";
	}



	@RequestMapping("/back")
	public String index(Model model) {

		log.info("首页被访问...");

//		Server serverInfo = ServerController.serverInfo();
//		model.addAttribute("sysTime", System.currentTimeMillis());
//		model.addAttribute("serverInfo", serverInfo);

		return "back/index";
	}



	@GetMapping(value = "/exit")
	@ResponseBody
	public String exit() {

		log.info("系统退出...");

//		System.exit(0);
		SpringApplication.exit(applicationContext);

		return "ok";
	}




	@GetMapping(value = "/back/dbList")
	public String dbList(Model model) {

		return "back/db/dbList";
	}



	@GetMapping(value = "/back/configFile")
	public String configFile(Model model) {

		List<ConfigFileDto> configFileList = configFileList();

		model.addAttribute("configFileList",configFileList);

		return "back/sys/configFile";
	}





	@GetMapping(value = "/back/taskList")
	public String taskList(Model model) {

		List<Job> taskList = jobService.findAll();

		taskList.stream().forEach(item->{
			item.setRemark(StrUtil.maxLength(item.getRemark(),10));
		});

		model.addAttribute("taskList",taskList);

		return "back/job/taskList";
	}


	public static void main(String[] args) {

		HttpRequest post = HttpUtil.createPost("http://localhost:8500/anxinqian/outter/fore/signPdfFile.do");
		post.form("signJson","[{\"contractName\":\"清偿承诺函\",\"userId\":\"CFF184C4D0C45B96E05312016B0A0F0A\",\"sealId\":\"422\",\"pageNo\":1,\"x\":325,\"y\":414,\"x1\":0,\"y1\":328,\"typeId\":1}]");
		post.form("file",new File("C:\\Users\\18514\\Desktop\\download\\清偿承诺函.pdf"));

		HttpResponse resp = post.execute();

		String body = resp.body();

		System.out.println(body);
	}




	@GetMapping(value = "/back/dictList")
	public String dictList(Model model) {

		List<Dict> dictList = dictService.findAll(Sort.by(Sort.Order.asc("orderNo")));

		model.addAttribute("dictList",dictList);

		return "back/dict/dictList";
	}





	private List<ConfigFileDto> configFileList(){

		List<ConfigFileDto> dtoList = Lists.newArrayList();

		List<String> list = Lists.newArrayList();

		MutablePropertySources sources = ((StandardServletEnvironment) environment).getPropertySources();

		Iterator<PropertySource<?>> it = sources.iterator();

		while (it.hasNext()){
			PropertySource<?> item = it.next();
			String name = item.getName();

			if (name.startsWith("applicationConfig")){
				list.add(name);
			}
		}

		log.info("配置文件列表：{}",list);

		list = list.stream().map(item->{
			return HelpMe.removeFirstChar(HelpMe.removeLastChar(item.split("applicationConfig: ")[1]));
		}).collect(Collectors.toList());


		List<String> fileList = list.stream().filter(item -> {
			return item.startsWith("file");
		}).collect(Collectors.toList());


		List<String> classpathList = list.stream().filter(item -> {
			return item.startsWith("classpath");
		}).collect(Collectors.toList());


		try {
			fileList = fileList.stream().map(item->{
				return item.split(":")[1];
			}).collect(Collectors.toList());
		} catch (Exception e) {
		}


		if (HelpMe.isNotNull(fileList)){
			for (String item:fileList){
				File file = new File(item);
				ConfigFileDto dto = new ConfigFileDto();
				dto.setName(file.getName());
				dto.setContent(FileUtil.readUtf8String(file));

				dtoList.add(dto);
			}
		}else {
			if (HelpMe.isNotNull(classpathList)){
				for (String item:classpathList){
					ConfigFileDto dto = new ConfigFileDto();
					dto.setName(item);
					dto.setContent(ResourceUtil.readUtf8Str(item));

					dtoList.add(dto);
				}
			}
		}

		return dtoList;
	}



}



















