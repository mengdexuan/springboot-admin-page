package com.boot.biz.springtask.runner;

import com.boot.base.util.HelpMe;
import com.boot.base.util.SpringContextUtil;
import com.boot.biz.springtask.entity.SpringTask;
import com.boot.biz.springtask.service.SpringTaskService;
import com.boot.biz.springtask.util.SpringTaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author mengdexuan on 2020/3/15 16:23.
 */
@Slf4j
public class SpringTaskRunnable implements Runnable{
	private Object target;
	private Method method;
	private String params;
	private String taskId;
	private Boolean manualTrigger = false;//手动触发
	private SpringTaskService springTaskService;

	public SpringTaskRunnable(String beanName, String methodName, String params,String taskId) throws NoSuchMethodException, SecurityException {
		this.target = SpringContextUtil.getBean(beanName);
		this.params = params;
		if (HelpMe.isNotNull(params)) {
			this.method = target.getClass().getDeclaredMethod(methodName, String.class);
		} else {
			this.method = target.getClass().getDeclaredMethod(methodName);
		}

		this.taskId = taskId;
		this.springTaskService = SpringContextUtil.getBean(SpringTaskService.class);
	}

	@Override
	public void run() {
		SpringTask task = springTaskService.getByFieldEqual("taskId", taskId);
		try {
			ReflectionUtils.makeAccessible(method);
			if (HelpMe.isNotNull(params)) {
				method.invoke(target, params);
			} else {
				method.invoke(target);
			}
			if (manualTrigger){
				log.info("手动触发任务执行，任务ID:{}，任务名称：{}",task.getTaskId(),task.getName());
			}

			//任务执行成功后，删除该任务
			if (SpringTask.DelWhenSuccess.YES.getValue()==task.getDelWhenSuccess().intValue()){
				springTaskService.delSpringTask(taskId);
			}
		} catch (Exception e) {
			log.error("执行定时任务失败！",e.getCause().getMessage());
			task.setErrLog(e.getCause().getMessage());
			springTaskService.save(task);
		}
	}

	public void setManualTrigger(Boolean manualTrigger) {
		this.manualTrigger = manualTrigger;
	}
}
