package com.boot.base.job.test;

import com.boot.base.job.annotation.JobCron;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 测试自动创建任务
 * @author mengdexuan on 2020/5/4 15:11.
 */
@Slf4j
//@Component
public class AutoCreateJobTest {


	@JobCron(name = "自动创建任务",cron = "0/2 * * * * *",delWhenSuccess = false,autoCreate = true)
	public void run(){
		log.info("AutoCreateJobTest run...");
	}



}
