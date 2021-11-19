package com.boot.base.hook;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author mengdexuan on 2021/11/19 10:04.
 */
@Slf4j
@Component
public class ShutdownHook implements Runnable,CommandLineRunner {

	@Override
	public void run() {
		log.info("jvm exit...");
	}



	@Override
	public void run(String... args) throws Exception {
		RuntimeUtil.addShutdownHook(this);
	}


}
