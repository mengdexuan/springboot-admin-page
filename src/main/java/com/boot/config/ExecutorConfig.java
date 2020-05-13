package com.boot.config;

import cn.hutool.core.thread.ThreadUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 应用程序使用的线程池配置
 *
 * @author mengdexuan on 2019/6/15 16:44.
 */
@Configuration
public class ExecutorConfig {

    private static final int nThread = 10;

    @Bean
    public ThreadPoolExecutor taskExecutor() {
        ThreadFactory threadFactory = ThreadUtil.newNamedThreadFactory("appThreadPool-", false);
        ExecutorService executor = Executors.newFixedThreadPool(nThread, threadFactory);
        return (ThreadPoolExecutor) executor;
    }
}
