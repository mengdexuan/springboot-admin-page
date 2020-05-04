package com.boot.biz.springtask.config;

import cn.hutool.core.util.ReflectUtil;
import com.boot.biz.springtask.annotation.CronTask;
import com.boot.biz.springtask.entity.SpringTask;
import com.boot.biz.springtask.service.SpringTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 处理 CronTask 注解配置为自动创建的任务
 * @author mengdexuan on 2020/5/4 15:19.
 */
@Slf4j
@Component
public class SpringTaskAutoCreateBeanPostProcessor implements BeanPostProcessor {

	@Autowired
	SpringTaskService springTaskService;


	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		Method[] methods = ReflectUtil.getMethods(bean.getClass());

		for (Method method:methods){
			CronTask cronTask = method.getAnnotation(CronTask.class);

			if (cronTask!=null&&cronTask.autoCreate()){
				SpringTask task = springTaskService.getSingleTask(bean.getClass());
				springTaskService.addSpringTaskOnce(task);
			}
		}


		return bean;
	}










}
