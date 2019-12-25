package com.boot.biz.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

/**
 * @author mengdexuan on 2019/6/28 11:57.
 */
@Slf4j
@Component
public class EchoBean {

	@Handler
	public String echo(String echo) {
		log.info(echo);


		return echo + " " + echo;
	}

	public String bar(String bar) {
		return bar + " " + bar;
	}

}
