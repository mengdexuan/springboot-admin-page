package com.boot.biz.serverinfo;

import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.biz.serverinfo.info.Jvm;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Map;

/**
 * @author mengdexuan on 2020/3/27 21:04.
 */
@Slf4j
@RequestMapping("/serverInfo")
@RestController
@Api(tags = "服务监控统计 接口")
public class ServerController extends BaseController {

	private static String cacheKey = "server";
	private static Map<String,Server> cache = Maps.newHashMap();

	@GetMapping(value = "/get")
	@ApiOperation("服务监控统计")
	public Result get() {

		Server server = serverInfo();

		return ResultUtil.buildSuccess(server);
	}



//	@Scheduled(cron = "0/59 * * * * *")
//	@PostConstruct
	public void init(){

		Server server = new Server();
		try {
			server.copyTo();
		} catch (Exception e) {
		}

		cache.put(cacheKey,server);
	}


	public static Server serverInfo(){
		Server server = cache.get(cacheKey);

		Jvm jvm = server.getJvm();

		long time = ManagementFactory.getRuntimeMXBean().getStartTime();
		Date date = new Date(time);

		String runTime = Jvm.getDatePoor(new Date(), date);

		jvm.setRunTime(runTime);

		return server;
	}
}
