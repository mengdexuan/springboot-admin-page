package com.boot.biz.job;

import cn.hutool.core.util.RuntimeUtil;
import com.boot.biz.springtask.annotation.CronTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 启动千牛
 */
@Slf4j
@Component
public class StartQianNvAppJob {

	String appDir = "C:\\Program Files (x86)\\AliWorkbench\\AliWorkbench.exe";

	@CronTask(taskName = "启动千牛",cron = "0/20 * * * * *",delWhenSuccess = false,autoCreate = true)
	public void run(){
		RuntimeUtil.exec(appDir);
		log.info("执行	{}	打开千牛 APP...",appDir);
	}


}
