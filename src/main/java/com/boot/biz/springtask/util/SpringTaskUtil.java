package com.boot.biz.springtask.util;

import cn.hutool.core.thread.ThreadUtil;
import com.boot.biz.springtask.entity.SpringTask;
import com.boot.biz.springtask.runner.SpringTaskRunnable;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author mengdexuan on 2020/3/15 17:50.
 */
@Slf4j
public class SpringTaskUtil {

	public static ScheduledTaskRegistrar scheduledTaskRegistrar = null;
	public static TaskScheduler taskScheduler = null;

	public static Map<String,ScheduledFuture> cache = Maps.newConcurrentMap();


	//执行 cron 调度
	public static void scheduleCronTask(String taskId,Runnable runnable,String cron){
		ScheduledFuture future = cache.get(taskId);

		if (future!=null){
			return;
		}

		CronTrigger cronTrigger = new CronTrigger(cron);
		if (taskScheduler!=null){
			ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(runnable, cronTrigger);
			cache.put(taskId,scheduledFuture);
		}
	}


	public static void scheduleCronTask(SpringTask springTask)throws Exception{
		SpringTaskRunnable runnable = new SpringTaskRunnable(springTask.getBean(),
				springTask.getMethod(),springTask.getParams(),springTask.getTaskId());

		scheduleCronTask(springTask.getTaskId(),runnable,springTask.getCron());
	}


	//取消 cron 调度
	public static void cancelCronTask(String taskId){

		ScheduledFuture future = cache.get(taskId);

		if (future!=null){
			future.cancel(true);
			cache.remove(taskId);
		}
	}


}
