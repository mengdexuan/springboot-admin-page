package com.boot.base.util;

import com.boot.base.exception.GlobalServiceException;
import org.quartz.CronExpression;

/**
 * @author mengdexuan on 2019/6/26 14:47.
 */
public class CronCheckUtil {

	/**
	 * 校验cron表达式
	 * @param cronExpression
	 */
	public static void check(String cronExpression){
		try {
			CronExpression cron = new CronExpression(cronExpression);
			cron = null;
		} catch (Exception e) {
			throw new GlobalServiceException("cron 表达式校验失败！");
		}
	}

}
