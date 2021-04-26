package com.boot.base.job.example;

import com.boot.base.job.annotation.JobCron;
import com.boot.base.job.entity.Job;
import com.boot.base.job.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 无业务参数的调度任务使用示例
 * @author mengdexuan on 2020/3/15 17:44.
 */
@Slf4j
@Component
public class NoParamJob {

	@Autowired
	JobService jobService;

	@JobCron(name = "测试任务1",cron = "0/3 * * * * *",delWhenSuccess = false,remark = "这是测试任务1")
	public void run(Long jobId)throws Exception{
		log.info("run1...,jobId:{}",jobId);

		boolean flag = false;
		if (flag){
			//满足一定的条件后，不再进行调度
			removeSelfJob(jobId);
		}
	}




	/**
	 * 如处示例如何创建一个被 @JobCron 标注的调度，可以在具体的业务 Service 中，结合业务要求的调度规则，执行此处的代码
	 */
//	@PostConstruct
	private void init(){
		Job job = jobService.getJob(NoParamJob.class);
		//job 无参数，直接添加到调度
		jobService.addJob(job);
	}



	/**
	 * 删除自身 job ，不再进行下次调度
	 */
	private void removeSelfJob(Long jobId){
		try {
			Job job = jobService.get(jobId);
			log.info("删除 job : {}",job);
			jobService.delJob(jobId);
		}catch (Exception e){
		}
	}


}
