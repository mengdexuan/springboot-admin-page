package com.boot.biz.springtask.config;

import com.boot.biz.springtask.entity.SpringTask;
import com.boot.biz.springtask.service.SpringTaskService;
import com.boot.biz.springtask.util.SpringTaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author mengdexuan on 2020/3/15 19:03.
 */
@Slf4j
@Component
public class SpringTaskInit implements CommandLineRunner {

	@Autowired
	SpringTaskService springTaskService;

	@Override
	public void run(String... args) throws Exception {

		List<SpringTask> list = springTaskService.findAll();

		for (SpringTask springTask:list){
			if (SpringTask.Status.RUN.getValue()==springTask.getStatus().intValue()){
				SpringTaskUtil.scheduleCronTask(springTask);
			}
		}
	}
}
