package com.boot.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author mengdexuan on 2019/6/15 16:44.
 */
@Configuration
@EnableScheduling
public class ThreadPoolConfig {
	@Bean
	public ThreadPoolExecutor taskExecutor() {
//		Executor executor =  Executors.newScheduledThreadPool(30);

		ThreadFactoryBuilder builder  = new ThreadFactoryBuilder();
		builder.setNameFormat("sysThread");
		ThreadFactory factory = builder.build();
		ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(10,factory);
		poolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		poolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
		poolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(true);


		return poolExecutor;
	}
}
