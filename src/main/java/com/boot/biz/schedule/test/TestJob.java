package com.boot.biz.schedule.test;

import com.boot.biz.schedule.entity.ScheduleJob;
import com.boot.biz.schedule.service.ScheduleJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author mengdexuan on 2018/8/23 15:05.
 */
@Service
@Slf4j
public class TestJob {

	@Autowired
	ScheduleJobService scheduleJobService;

	private void run(String name){
		/*try {
			Thread.sleep(1000*2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		log.info("name:{}",name);
	}


	public void createTestJob(){

		ScheduleJob scheduleJob = new ScheduleJob();
		scheduleJob.setBean("testJob");
		scheduleJob.setMethod("run");
		scheduleJob.setExpression("0/1 * * * * ? ");
		scheduleJob.setName("非并发任务");
		scheduleJob.setParams("123");
		scheduleJob.setRemark("测试任务");
		scheduleJob.setDelOnSuccess(ScheduleJob.DelOnSuccess.NO.getValue());
		scheduleJob.setAllowConcurrent(ScheduleJob.AllowConcurrent.NO.getValue());

		scheduleJobService.createJobOnce(scheduleJob);

	}

	@PostConstruct
	public void init(){
		createTestJob();
	}

}
