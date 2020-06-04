package com.boot.base.job.test;

import com.boot.base.job.annotation.JobCron;
import com.boot.base.job.entity.Job;
import com.boot.base.job.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author mengdexuan on 2020/3/15 17:44.
 */
@Slf4j
//@Component
public class JobTest {

	@Autowired
	JobService jobService;

	@JobCron(name = "测试任务1",cron = "0/3 * * * * *",delWhenSuccess = false,remark = "这是测试任务1")
	public void run(){
		log.info("run1...");
	}

	@JobCron(name = "测试任务2",cron = "0/5 * * * * *",delWhenSuccess = false,remark = "这是测试任务2")
	public void run2(String param){
		log.info("run2，参数：{}",param);
	}



//	@PostConstruct
	private void init(){
		List<Job> taskList = jobService.getJob(JobTest.class);

		for (Job job:taskList){

			if (job.getMethod().equals("run2")){
				job.setParams("123");
			}

			jobService.addJobOnce(job);
		}
	}





}
