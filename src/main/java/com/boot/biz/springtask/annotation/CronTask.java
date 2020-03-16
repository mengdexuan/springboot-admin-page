package com.boot.biz.springtask.annotation;

import java.lang.annotation.*;

/**
 * 注解在方法上，描述待执行的任务
 *
 * 使用方法，请参考 SpringTaskTest.java
 *
 * @author mengdexuan on 2019/5/16 15:43.
 */

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CronTask {

	/**
	 * 任务名称
	 * @return
	 */
	String taskName();

	/**
	 * 任务 cron 表达式
	 * @return
	 */
	String cron();

	/**
	 * 执行成功是否删除该任务  true:删除   false:不删除（继续下次执行）
	 * @return
	 */
	boolean delWhenSuccess();


	/**
	 * 任务描述信息
	 * @return
	 */
	String remark();

}
