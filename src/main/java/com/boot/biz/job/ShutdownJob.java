package com.boot.biz.job;

import com.boot.biz.springtask.annotation.CronTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 定时关机
 */
@Slf4j
@Component
public class ShutdownJob {

	//60秒后关机
	String cmd = "shutdown -s -t 60";

	@CronTask(taskName = "定时关机",cron = "0/15 * * * * *",delWhenSuccess = false,autoCreate = true)
	public void run(){
		try {
			Runtime.getRuntime().exec(cmd);
			log.info("执行关机...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



}
