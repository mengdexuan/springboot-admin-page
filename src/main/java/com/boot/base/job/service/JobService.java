package com.boot.base.job.service;


import com.boot.base.BaseService;
import com.boot.base.job.entity.Job;

import java.util.List;

/**
 * <p>
 * spring定时任务 服务类
 * </p>
 *
 * @author code-make-tool
 * @since 2020-03-15
 */
public interface JobService extends BaseService<Job> {

	/***
	 * 添加 cron 任务，相同的任务（指的是2条任务的：bean名称和method名称相同）可以重复添加
	 * @param job
	 */
	void addJob(Job job);


	/**
	 * 添加 cron 任务，相同的任务（指的是2条任务的：bean名称和method名称相同）只可以添加1条记录，不会重复添加
	 * @param job
	 */
	void addJobOnce(Job job);

	/**
	 * 删除 cron 任务
	 * @param jobId
	 */
	void delJob(Long jobId);

	/**
	 * 暂停 cron 任务
	 * @param jobId
	 */
	void parseJob(Long jobId);


	/**
	 * 重启 cron 任务
	 * @param jobId
	 * @throws Exception
	 */
	void restartJob(Long jobId) throws Exception;


	/**
	 * 修改任务的 cron 表达式
	 * @param jobId
	 * @param newCron
	 * @throws Exception
	 */
	void changeJobCron(Long jobId, String newCron) throws Exception;


	/**
	 * 手动触发一次任务
	 * @param jobId
	 * @throws Exception
	 */
	void manualTrigger(Long jobId) throws Exception;


	/**
	 *	解析1个Bean，提取并转换由 CronJob 注解标识的方法，生成 SpringJob
	 * @param type spring管理的1个Bean
	 * @param <T>
	 * @return  返回 Job
	 */
	<T> Job getJob(Class<T> type);



}
