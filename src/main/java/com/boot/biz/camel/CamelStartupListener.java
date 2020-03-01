package com.boot.biz.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.StartupListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author mengdexuan on 2020/3/1 12:57.
 */
@Slf4j
@Component
public class CamelStartupListener implements StartupListener,CommandLineRunner {

	@Autowired
	CamelContext camelContext;

	@Autowired
	SyncRoute syncRoute;


	@Override
	public void onCamelContextStarted(CamelContext context, boolean alreadyStarted) throws Exception {

		syncRoute.sync();

	}


	@Override
	public void run(String... args) throws Exception {
		camelContext.addStartupListener(this);
	}
}
