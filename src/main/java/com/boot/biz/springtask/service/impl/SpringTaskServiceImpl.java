package com.boot.biz.springtask.service.impl;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.boot.base.BaseServiceImpl;
import com.boot.base.util.HelpMe;
import com.boot.biz.schedule.annotation.QuartzJob;
import com.boot.biz.schedule.entity.ScheduleJob;
import com.boot.biz.springtask.annotation.CronTask;
import com.boot.biz.springtask.entity.SpringTask;
import com.boot.biz.springtask.repository.SpringTaskRepository;
import com.boot.biz.springtask.runner.SpringTaskRunnable;
import com.boot.biz.springtask.service.SpringTaskService;
import com.boot.biz.springtask.util.SpringTaskUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * spring定时任务 服务实现类
 * </p>
 *
 * @author code-make-tool
 * @since 2020-03-15
 */
@Slf4j
@Service
public class SpringTaskServiceImpl extends BaseServiceImpl<SpringTask,SpringTaskRepository> implements SpringTaskService {


	/***
	 * 添加 cron 任务，相同的任务（指的是2条任务的：bean名称和method名称相同）可以重复添加
	 * @param springTask
	 */
	@Override
	@Transactional
	public void addSpringTask(SpringTask springTask){

		if (springTask.getStatus()==SpringTask.Status.PAUSE.getValue()){

		}else {
			//添加到 spring 调度
			try {
				SpringTaskUtil.scheduleCronTask(springTask);
			} catch (Exception e) {
				log.error("添加到 spring 调度任务失败！",e);
			}
		}

		this.save(springTask);
	}



	/**
	 * 添加 cron 任务，相同的任务（指的是2条任务的：bean名称和method名称相同）只可以添加1条记录，不会重复添加
	 * @param springTask
	 */
	@Override
	@Transactional
	public void addSpringTaskOnce(SpringTask springTask){
		SpringTask temp = new SpringTask();

		temp.setBean(springTask.getBean());
		temp.setMethod(springTask.getMethod());

		SpringTask entity = this.one(temp);

		if (entity==null){
			addSpringTask(springTask);
		}else {
			log.error("不允许重复添加相同的Task!");
		}
	}


	/**
	 * 删除 cron 任务
	 * @param taskId
	 */
	@Override
	@Transactional
	public void delSpringTask(String taskId) {

		SpringTask springTask = this.getByFieldEqual("taskId", taskId);

		SpringTaskUtil.cancelCronTask(taskId);

		this.delete(springTask);
	}


	/**
	 * 暂停 cron 任务
	 *
	 * @param taskId
	 */
	@Override
	@Transactional
	public void parseSpringTask(String taskId) {
		SpringTask springTask = this.getByFieldEqual("taskId", taskId);
		springTask.setStatus(SpringTask.Status.PAUSE.getValue());

		SpringTaskUtil.cancelCronTask(taskId);

		this.save(springTask);
	}

	/**
	 * 重启 cron 任务
	 *
	 * @param taskId
	 */
	@Override
	@Transactional
	public void restartSpringTask(String taskId) throws Exception{

		SpringTask springTask = this.getByFieldEqual("taskId", taskId);
		springTask.setStatus(SpringTask.Status.RUN.getValue());

		SpringTaskUtil.scheduleCronTask(springTask);

		this.save(springTask);
	}


	/**
	 * 修改任务的 cron 表达式
	 *
	 * @param taskId
	 * @param newCron
	 * @throws Exception
	 */
	@Override
	@Transactional
	public void changeSpringTaskCron(String taskId, String newCron) throws Exception {
		SpringTask springTask = this.getByFieldEqual("taskId", taskId);
		springTask.setCron(newCron);

		//先取消任务
		SpringTaskUtil.cancelCronTask(taskId);

		if (SpringTask.Status.RUN.getValue()==springTask.getStatus().intValue()){
			//再添加任务
			SpringTaskUtil.scheduleCronTask(springTask);
		}

		this.save(springTask);
	}


	/**
	 * 手动触发一次任务
	 *
	 * @param taskId
	 */
	@Override
	public void manualTrigger(String taskId) throws Exception{

		SpringTask springTask = this.getByFieldEqual("taskId", taskId);

		SpringTaskRunnable springTaskRunnable = new SpringTaskRunnable(springTask.getBean(),springTask.getMethod(),springTask.getParams(),springTask.getTaskId());

		springTaskRunnable.setManualTrigger(true);

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		executorService.execute(springTaskRunnable);
	}



	/**
	 *	解析1个Bean，提取并转换由 CronTask 注解标识的方法，生成 SpringTask
	 * @param type spring管理的1个Bean
	 * @param <T>
	 * @return  返回 List<SpringTask> 任务列表（1个Bean中，可以有多个 CronTask 标注的方法）
	 */
	@Override
	public <T> List<SpringTask> getTask(Class<T> type) {

		//获取 spring bean 名称，默认类名首字母小写
		String beanName = StrUtil.lowerFirst(type.getSimpleName());

		Service service = type.getAnnotation(Service.class);
		if (service!=null && HelpMe.isNotNull(service.value())){
			beanName = service.value();
		}

		Component component = type.getAnnotation(Component.class);
		if (component!=null && HelpMe.isNotNull(component.value())){
			beanName = component.value();
		}

		log.info("{} springBean名称：{}",type.getName(),beanName);

		Method[] methods = ReflectUtil.getMethods(type);

		int count = 0 ;
		for (Method method:methods){
			CronTask cronTask = method.getAnnotation(CronTask.class);
			if (cronTask!=null){
				count++;
			}
		}
		if (count==0){
			throw new RuntimeException(beanName+" 中不存在 CronTask 标注的方法！");
		}

		List<SpringTask> springTaskList = Lists.newArrayList();

		Date now = new Date();

		for (Method method:methods){
			CronTask cronTask = method.getAnnotation(CronTask.class);
			if (cronTask!=null){

				SpringTask springTask = new SpringTask();

				springTask.setTaskId(HelpMe.uuid());
				springTask.setBean(beanName);
				springTask.setMethod(method.getName());
				springTask.setCron(cronTask.cron());
				springTask.setName(cronTask.taskName());
				springTask.setRemark(cronTask.remark());

				if (cronTask.delWhenSuccess()){
					springTask.setDelWhenSuccess(SpringTask.DelWhenSuccess.YES.getValue());
				}else {
					springTask.setDelWhenSuccess(SpringTask.DelWhenSuccess.NO.getValue());
				}

				springTask.setStatus(SpringTask.Status.DEFAULT.getValue());
				springTask.setCreateTime(now);

				springTaskList.add(springTask);
			}
		}

		return springTaskList;
	}

	/**
	 * 获取任务，调用的是 getTask方法，获取第 1 个值
	 *
	 * @param type
	 * @return
	 */
	@Override
	public <T> SpringTask getSingleTask(Class<T> type) {
		return getTask(type).get(0);
	}
}
