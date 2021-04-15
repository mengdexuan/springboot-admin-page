package com.boot.base.job.test;

import com.boot.base.job.annotation.JobCron;
import com.boot.base.job.service.JobService;
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

	/**
	 * 本job的id，在 JobRunnable 中设置
	 */
	Long id;

	@Autowired
	JobService jobService;

	@Autowired
	MailService mailService;

	@JobCron(name = "自动创建任务",cron = "0/2 * * * * *",delWhenSuccess = false,autoCreate = true)
	public void run(){
		log.info("AutoCreateJobTest run...");


//		mailService.test(Lists.newArrayList("m18888041745@163.com"));

	}



	/**
	 * 删除自身 job ，不再进行下次调度
	 */
	private void removeSelfJob(){
		jobService.delJob(id);
	}


}
