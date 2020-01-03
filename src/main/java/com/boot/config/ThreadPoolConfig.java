package com.boot.config;

import cn.hutool.core.thread.ThreadUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.*;

/**
 * @author mengdexuan on 2019/6/15 16:44.
 */
@Configuration
public class ThreadPoolConfig {
	@Bean
	public ThreadPoolExecutor taskExecutor() {
		ThreadPoolExecutor executor = ThreadUtil.newExecutor(10,20);
		return executor;
	}
}
