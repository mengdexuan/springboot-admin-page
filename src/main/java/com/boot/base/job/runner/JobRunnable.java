package com.boot.base.job.runner;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.boot.base.job.entity.Job;
import com.boot.base.job.service.JobService;
import com.boot.base.util.HelpMe;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

		this.method = ReflectUtil.getMethodByName(target.getClass(),methodName);

		this.jobId = jobId;
		this.jobService = jobService;

		Field taskIdFiled = ReflectUtil.getField(target.getClass(), "id");

	}

	@Override
	public void run() {
		Job job = jobService.get(jobId);
		try {
			ReflectionUtils.makeAccessible(method);
			if (StrUtil.isNotBlank(params)) {
				method.invoke(target, params,jobId);
			} else {
				method.invoke(target,jobId);
			}
			if (manualTrigger){
				log.info("手动触发任务执行，任务ID:{}，任务名称：{}",job.getId(),job.getName());
			}

			//任务执行成功后，删除该任务
			if (Job.DelWhenSuccess.YES.getValue()==job.getDelWhenSuccess().intValue()){
				jobService.delJob(jobId);
			}
		} catch (Exception e) {
			String errMsg = "";
			try {
				if (e instanceof InvocationTargetException){
					StackTraceElement element = ((InvocationTargetException) e).getTargetException().getStackTrace()[0];
					String jsonStr = JSONUtil.toJsonStr(element);
					errMsg += "执行定时任务失败，详细信息："+jsonStr;
				}else {
					errMsg = e.getMessage();
				}
				log.error(errMsg);
			}finally {
				job.setErrLog(errMsg);
				jobService.save(job);
			}
		}
	}

	public void setManualTrigger(Boolean manualTrigger) {
		this.manualTrigger = manualTrigger;
	}
}
