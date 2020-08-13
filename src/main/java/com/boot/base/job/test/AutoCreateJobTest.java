package com.boot.base.job.test;

import com.boot.base.job.annotation.JobCron;
import com.boot.biz.mail.service.MailService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 测试自动创建任务
 * @author mengdexuan on 2020/5/4 15:11.
 */
@Slf4j
@Component
public class AutoCreateJobTest {

	@Autowired
	MailService mailService;

	@JobCron(name = "自动创建任务",cron = "0/2 * * * * *",delWhenSuccess = false,autoCreate = true)
	public void run(){
		log.info("AutoCreateJobTest run...");


//		mailService.test(Lists.newArrayList("m18888041745@163.com"));

	}



}
