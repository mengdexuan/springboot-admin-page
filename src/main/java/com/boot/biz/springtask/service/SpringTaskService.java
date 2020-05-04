package com.boot.biz.springtask.service;


import com.boot.base.BaseService;
import com.boot.biz.springtask.entity.SpringTask;

import java.util.List;

/**
 * <p>
 * spring定时任务 服务类
 * </p>
 *
 * @author code-make-tool
 * @since 2020-03-15
 */
public interface SpringTaskService extends BaseService<SpringTask> {

	/***
	 * 添加 cron 任务，相同的任务（指的是2条任务的：bean名称和method名称相同）可以重复添加
	 * @param springTask
	 */
	void addSpringTask(SpringTask springTask);


	/**
	 * 添加 cron 任务，相同的任务（指的是2条任务的：bean名称和method名称相同）只可以添加1条记录，不会重复添加
	 * @param springTask
	 */
	void addSpringTaskOnce(SpringTask springTask);

	/**
	 * 删除 cron 任务
	 * @param taskId
	 */
	void delSpringTask(String taskId);

	/**
	 * 暂停 cron 任务
	 * @param taskId
	 */
	void parseSpringTask(String taskId);


	/**
	 * 重启 cron 任务
	 * @param taskId
	 */
	void restartSpringTask(String taskId) throws Exception;


	/**
	 * 修改任务的 cron 表达式
	 * @param taskId
	 * @param newCron
	 * @throws Exception
	 */
	void changeSpringTaskCron(String taskId,String newCron) throws Exception;


	/**
	 * 手动触发一次任务
	 * @param taskId
	 */
	void manualTrigger(String taskId) throws Exception;


	/**
	 *	解析1个Bean，提取并转换由 CronTask 注解标识的方法，生成 SpringTask
	 * @param type spring管理的1个Bean
	 * @param <T>
	 * @return  返回 List<SpringTask> 任务列表（1个Bean中，可以有多个 CronTask 标注的方法）
	 */
	<T> List<SpringTask> getTask(Class<T> type);

	/**
	 * 获取任务，调用的是 getTask方法，获取第 1 个值
	 * @param type
	 * @param <T>
	 * @return
	 */
	<T> SpringTask getSingleTask(Class<T> type);
}
