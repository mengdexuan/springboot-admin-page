package com.boot.biz.springtask.util;

import org.springframework.scheduling.support.CronTrigger;

/**
 * @author mengdexuan on 2020/3/15 17:59.
 */
public class CronCheckUtil {


	public static boolean ok(String cron){
		boolean ok = true;

		try {
			CronTrigger cronTrigger = new CronTrigger(cron);
		} catch (Exception e) {
			ok = false;
		}

		return ok;
	}

}
