package com.boot.base.job.runner;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.boot.base.job.entity.Job;
import com.boot.base.job.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author mengdexuan on 2020/3/15 16:23.
 */
@Slf4j
public class JobRunnable implements Runnable{
	private Object target;
	private Method method;
	private String params;
	private Long jobId;
	/**
	 * 手动触发
	 */
	private Boolean manualTrigger = false;
	private JobService jobService;

	public JobRunnable(Object bean, String methodName, String params, Long jobId,JobService jobService) throws NoSuchMethodException, SecurityException {
 		this.target = bean;
		this.params = params;
		if (StrUtil.isNotBlank(params)) {
			this.method = target.getClass().getDeclaredMethod(methodName, String.class);
		} else {
			this.method = target.getClass().getDeclaredMethod(methodName);
		}

		this.jobId = jobId;
		this.jobService = jobService;

		Field taskIdFiled = ReflectUtil.getField(target.getClass(), "id");

		if (taskIdFiled!=null){
			ReflectUtil.setFieldValue(target,taskIdFiled,jobId);
		}
	}

	@Override
	public void run() {
		Job job = jobService.get(jobId);
		try {
			ReflectionUtils.makeAccessible(method);
			if (StrUtil.isNotBlank(params)) {
				method.invoke(target, params);
			} else {
				method.invoke(target);
			}
			if (manualTrigger){
				log.info("手动触发任务执行，任务ID:{}，任务名称：{}",job.getId(),job.getName());
			}

			//任务执行成功后，删除该任务
			if (Job.DelWhenSuccess.YES.getValue()==job.getDelWhenSuccess().intValue()){
				jobService.delJob(jobId);
			}
		} catch (Exception e) {
			log.error("执行定时任务失败！",e.getCause().getMessage());
			job.setErrLog(e.getCause().getMessage());
			jobService.save(job);
		}
	}

	public void setManualTrigger(Boolean manualTrigger) {
		this.manualTrigger = manualTrigger;
	}
}
