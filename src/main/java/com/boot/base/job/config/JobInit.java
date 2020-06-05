package com.boot.base.job.config;

import com.boot.base.job.entity.Job;
import com.boot.base.job.service.JobService;
import com.boot.base.job.util.JobUtil;
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
public class JobInit implements CommandLineRunner {

	@Autowired
	JobService jobService;

	@Autowired
	JobUtil jobUtil;

	@Override
	public void run(String... args) throws Exception {

		List<Job> list = jobService.findAll();

		for (Job job:list){
			if (Job.Status.RUN.getValue()==job.getStatus().intValue()){
				jobUtil.scheduleCronTask(job);
			}
		}
	}
}
