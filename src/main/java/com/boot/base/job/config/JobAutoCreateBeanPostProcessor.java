package com.boot.base.job.config;

import cn.hutool.core.util.ReflectUtil;
import com.boot.base.job.annotation.JobCron;
import com.boot.base.job.entity.Job;
import com.boot.base.job.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 处理 JobCron 注解配置为自动创建的任务
 *
 * @author mengdexuan on 2020/5/4 15:19.
 */
@Slf4j
@Component
public class JobAutoCreateBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    JobService jobService;


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Method[] methods = ReflectUtil.getMethods(bean.getClass());

        for (Method method : methods) {
            JobCron cronTask = method.getAnnotation(JobCron.class);
            if (cronTask != null && cronTask.autoCreate()) {
                Job task = jobService.getSingleJob(bean.getClass());
                jobService.addJobOnce(task);
            }
        }
        return bean;
    }


}
