package com.boot.shutdown;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.script.ScriptUtil;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 优雅的关闭 tomcat
 * @author mengdexuan on 2019/6/17 22:10.
 */
@Slf4j
@Component
public class GracefulShutdownTomcat implements TomcatConnectorCustomizer,
		ApplicationListener<ContextClosedEvent> {

	private volatile Connector connector;
	private final int waitTime = 30;
	@Override
	public void customize(Connector connector) {
		this.connector = connector;
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
		this.connector.pause();
		Executor executor = this.connector.getProtocolHandler().getExecutor();
		if (executor instanceof ThreadPoolExecutor) {
			try {
				ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
				threadPoolExecutor.shutdown();
				if (!threadPoolExecutor.awaitTermination(waitTime, TimeUnit.SECONDS)) {
					log.info("Tomcat thread pool did not shut down gracefully within "
							+ waitTime + " seconds. Proceeding with forceful shutdown");

					long pid = SystemUtil.getCurrentPID();

					String cmd = "kill "+pid;

					log.info(cmd);

					RuntimeUtil.exec(cmd);
				}
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
