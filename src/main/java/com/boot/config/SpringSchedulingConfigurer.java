package com.boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 配置 @Scheduled 注解使用的线程池
 * 使用 @Scheduled 时，需要 @EnableScheduling 开启
 * 若不配置线程池，@Scheduled 默认是单线程执行的
 * @author mengdexuan on 2020/3/7 23:02.
 */
@EnableScheduling
@Configuration
public class SpringSchedulingConfigurer implements SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler());
	}

	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("springSchedulingThread-");
		scheduler.setPoolSize(10);
		return scheduler;
	}
}
