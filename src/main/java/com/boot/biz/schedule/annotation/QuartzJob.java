package com.boot.biz.schedule.annotation;

import java.lang.annotation.*;

/**
 * 注解在方法上，描述待执行的任务
 *
 * 使用方法，请参考 TestJob2.java
 *
 * @author mengdexuan on 2019/5/16 15:43.
 */

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QuartzJob {

	/**
	 * 任务 cron 表达式
	 * @return
	 */
	String cron();

	/**
	 * 任务名称
	 * @return
	 */
	String jobName();

	/**
	 * 执行成功是否删除该任务  true:删除   false:不删除（继续下次执行）
	 * @return
	 */
	boolean delOnSuccess();

	/**
	 *是 否允许任务并发执行  true:并发   false:非并发
	 * @return
	 */
	boolean allowConcurrent();
}
