package com.boot.biz.index.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.authgroup.entity.AuthGroup;
import com.boot.biz.authgroup.service.AuthGroupService;
import com.boot.biz.camel.entity.CamelRoute;
import com.boot.biz.camel.repository.CamelRouteRepository;
import com.boot.biz.dict.entity.Dict;
import com.boot.biz.dict.service.DictService;
import com.boot.biz.index.dto.ConfigFileDto;
import com.boot.biz.menu.entity.Menu;
import com.boot.biz.menu.service.MenuService;
import com.boot.biz.requestfollow.entity.RequestFollow;
import com.boot.biz.requestfollow.service.RequestFollowService;
import com.boot.biz.schedule.entity.ScheduleJob;
import com.boot.biz.schedule.service.ScheduleJobService;
import com.boot.biz.serverinfo.Server;
import com.boot.biz.serverinfo.ServerController;
import com.boot.biz.springtask.entity.SpringTask;
import com.boot.biz.springtask.service.SpringTaskService;
import com.boot.biz.urllimit.entity.UrlLimit;
import com.boot.biz.urllimit.repository.UrlLimitRepository;
import com.boot.biz.user.entity.SysUser;
import com.boot.biz.user.service.UserService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.annotation.PostConstruct;
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
	ScheduleJobService scheduleJobService;

	@Autowired
	SpringTaskService springTaskService;

	@Autowired
	CamelContext camelContext;

	@Autowired
	DictService dictService;

	@Autowired
	UrlLimitRepository urlLimitRepository;

	@Autowired
	CamelRouteRepository camelRouteRepository;

	@Value("${server.servlet.context-path}")
	private String contextPath;

	@Value("${server.port}")
	private Integer port;


	@Autowired
	MenuService menuService;

	@Autowired
	UserService userService;

	@Autowired
	AuthGroupService authGroupService;

	@Autowired
	RequestFollowService requestFollowService;

	@PostConstruct
	private void init(){
		log.info("");
		log.info("http://localhost:"+port+contextPath);
		log.info("http://localhost:"+port+contextPath+"/doc.html");
		log.info("");
	}



	@GetMapping("/jump2Page")
	public String jump2Page(String page) {

		log.info("跳转至页面：{}",page);

		return page;
	}


	@RequestMapping("/test")
	public String test(Model model) {

		log.info("测试页面");

		return "test/webSocketTest";
	}




	@RequestMapping("/")
	public String index(Model model) {

		log.info("首页被访问...");

		Server serverInfo = ServerController.serverInfo();

		model.addAttribute("sysTime", System.currentTimeMillis());
		model.addAttribute("serverInfo", serverInfo);

		return "index";
	}



	@GetMapping(value = "/exit")
	@ResponseBody
	public String exit() {

		log.info("系统退出...");

//		System.exit(0);
		SpringApplication.exit(applicationContext);

		return "ok";
	}




	@GetMapping(value = "/dbList")
	public String dbList(Model model) {

		return "db/dbList";
	}



	@GetMapping(value = "/configFile")
	public String configFile(Model model) {

		List<ConfigFileDto> configFileList = configFileList();

		model.addAttribute("configFileList",configFileList);

		return "sys/configFile";
	}



	@GetMapping(value = "/requestFollowList")
	public String requestFollowList(RequestFollow requestFollow,Model model) {

		Sort sort = new Sort(Sort.Direction.DESC, "createTime");

		//默认查询前 100 条
		PageRequest pageRequest = PageRequest.of(0,100,sort);

		Page<RequestFollow> page = requestFollowService.list(requestFollow, pageRequest);

		List<RequestFollow> requestFollowList = page.getContent();

		model.addAttribute("tempObj",requestFollow);
		model.addAttribute("requestFollowList",requestFollowList);

		return "requestFollow/requestFollowList";
	}





	@GetMapping(value = "/jobList")
	public String jobList(Model model) {

		List<ScheduleJob> jobList = scheduleJobService.findAll();

		jobList.stream().forEach(item->{
			item.setRemark(StrUtil.maxLength(item.getRemark(),10));
		});

		model.addAttribute("jobList",jobList);

		return "job/jobList";
	}



	@GetMapping(value = "/taskList")
	public String taskList(Model model) {

		List<SpringTask> taskList = springTaskService.findAll();

		taskList.stream().forEach(item->{
			item.setRemark(StrUtil.maxLength(item.getRemark(),10));
		});

		model.addAttribute("taskList",taskList);

		return "job/taskList";
	}




	@GetMapping(value = "/authGroupList")
	public String authGroupList(Model model) {

		AuthGroup authGroup = new AuthGroup();
//		authGroup.setType(2);

		List<AuthGroup> authGroupList = authGroupService.list(authGroup);

		model.addAttribute("authGroupList",authGroupList);

		return "authCenter/authGroupList";
	}



	@GetMapping(value = "/dictList")
	public String dictList(Model model) {

		List<Dict> dictList = dictService.findAll();

		model.addAttribute("dictList",dictList);

		return "dict/dictList";
	}




	@GetMapping(value = "/userList")
	public String userList(Model model) {

		List<SysUser> userList = userService.findAll();

		model.addAttribute("userList",userList);

		return "authCenter/userList";
	}


	@GetMapping(value = "/menuList")
	public String menuList(Model model) {

		List<Menu> menuList = menuService.list("pid",(Object) null,true);
		/*menuList = menuList.stream().filter(item->{
			return item.getType()==2;
		}).collect(Collectors.toList());*/

		model.addAttribute("menuList",menuList);

		return "authCenter/menuList";
	}


	@GetMapping(value = "/urlLimitList")
	public String urlLimitList(Model model) {

		Sort sort1 = new Sort(Sort.Direction.DESC, "urlLimit");
		Sort sort2 = new Sort(Sort.Direction.ASC, "reqUrl");

		Sort sort = sort1.and(sort2);

		List<UrlLimit> urlLimitList = urlLimitRepository.findAll(sort);

		model.addAttribute("urlLimitList",urlLimitList);

		return "urlLimit/urlLimitList";
	}


	@GetMapping(value = "/camelRouteList")
	public String camelRouteList(Model model) {

		Sort sort = new Sort(Sort.Direction.ASC, "routeId");

		List<CamelRoute> camelRouteList = camelRouteRepository.findAll(sort);

		model.addAttribute("camelRouteList",camelRouteList);

		return "camel/camelRouteList";
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


		fileList = fileList.stream().map(item->{
			return item.split("file:./")[1];
		}).collect(Collectors.toList());


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



















