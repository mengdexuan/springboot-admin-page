package com.boot.base.util;

import cn.hutool.cron.pattern.CronPattern;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mengdexuan on 2019/6/26 14:47.
 */
@Slf4j
public class CronCheckUtil {

	/**
	 * 校验cron表达式
	 * @param cronExpression
	 */
	public static boolean ok(String cronExpression){
		boolean ok = true;
		try {
			CronPattern cronPattern = new CronPattern(cronExpression);
		} catch (Exception e) {
			ok = false;
			log.error("cron 表达式校验失败！");
		}
		return ok;
	}

}
