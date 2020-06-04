package com.boot.biz.job;

import cn.hutool.core.util.RuntimeUtil;
import com.boot.base.job.annotation.JobCron;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 启动千牛
 */
@Slf4j
@Component
public class StartQianNvAppJob {

	String appDir = "C:\\Program Files (x86)\\AliWorkbench\\AliWorkbench.exe";

	@JobCron(name = "启动千牛",cron = "0/20 * * * * *",delWhenSuccess = false,autoCreate = true)
	public void run(){
//		RuntimeUtil.exec(appDir);
		log.info("执行	{}	打开千牛 APP...",appDir);
	}


}
