package com.boot.biz.springtask.test;

import com.boot.biz.springtask.annotation.CronTask;
import com.boot.biz.springtask.entity.SpringTask;
import com.boot.biz.springtask.service.SpringTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author mengdexuan on 2020/3/15 17:44.
 */
@Slf4j
@Component
public class SpringTaskTest {

	@Autowired
	SpringTaskService springTaskService;

	@CronTask(taskName = "测试任务1",cron = "0/3 * * * * *",delWhenSuccess = false,remark = "这是测试任务1")
	public void run(){
		log.info("run1...");
	}

	@CronTask(taskName = "测试任务2",cron = "0/5 * * * * *",delWhenSuccess = false,remark = "这是测试任务2")
	public void run2(String param){
		log.info("run2，参数：{}",param);
	}



	@PostConstruct
	private void init(){
		List<SpringTask> taskList = springTaskService.getTask(SpringTaskTest.class);

		for (SpringTask task:taskList){

			if (task.getMethod().equals("run2")){
				task.setParams("123");
			}

			springTaskService.addSpringTaskOnce(task);
		}
	}





}
