package com.boot.biz.schedule.test;

import cn.hutool.core.thread.ThreadUtil;
import com.boot.biz.schedule.annotation.QuartzJob;
import com.boot.biz.schedule.entity.ScheduleJob;
import com.boot.biz.schedule.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mengdexuan on 2019/5/16 15:31.
 */

@Service
@Slf4j
public class TestJob2 {


	@Autowired
	ScheduleJobService scheduleJobService;

	@QuartzJob(cron = "0/3 * * * * ? ",jobName = "任务1",
			delOnSuccess = false,allowConcurrent = true)
	private void run(String param){

		log.info("任务参数：{}",param);

		ThreadUtil.safeSleep(5000);
	}


//	@PostConstruct
	public void init(){
		//3种方式创建job
		createJob1();
		createJob2();
		createJob3();
	}

	private void createJob1(){
		ScheduleJob job = scheduleJobService.getJob(TestJob2.class);
		job.setParams("good");

		scheduleJobService.createJobOnce(job);
	}
	private void createJob2(){
		scheduleJobService.createJob(TestJob2.class,"better");
	}
	private void createJob3(){
		scheduleJobService.createJob(TestJob2.class,"best","这是一个测试任务");
	}


}
