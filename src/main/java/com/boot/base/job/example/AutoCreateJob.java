package com.boot.base.job.example;

import com.boot.base.job.annotation.JobCron;
import com.boot.base.job.entity.Job;
import com.boot.base.job.service.JobService;
import com.boot.biz.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自动创建任务	使用示例，无需手动添加，项目启动后，自动添加到调度；这种类型的 job 要求是无参数的
 * @author mengdexuan on 2020/5/4 15:11.
 */
@Slf4j
@Component
public class AutoCreateJob {


	@Autowired
	JobService jobService;

	@Autowired
	MailService mailService;

	@JobCron(name = "自动创建任务",cron = "0/2 * * * * *",delWhenSuccess = false,autoCreate = true)
	public void run(Long jobId)throws Exception{
		log.info("AutoCreateJobTest run...");

		boolean flag = false;
		if (flag){
			//满足一定的条件后，不再进行调度
			removeSelfJob(jobId);
		}
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
