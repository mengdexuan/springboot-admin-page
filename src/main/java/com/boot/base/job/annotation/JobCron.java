package com.boot.base.job.annotation;

import java.lang.annotation.*;

/**
 * 注解在方法上，描述待执行的任务
 *
 * 使用方法，请参考 JobTest.java
 *
 * @author mengdexuan on 2019/5/16 15:43.
 */

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobCron {

	/**
	 * 任务名称
	 * @return
	 */
	String name();

	/**
	 * 任务 cron 表达式
	 * @return
	 */
	String cron();

	/**
	 * 执行成功是否删除该任务  true:删除   false:不删除（继续下次执行）	默认 false
	 * @return
	 */
	boolean delWhenSuccess() default false;


	//启动时，自动创建Task，默认 false
	boolean autoCreate() default false;


	/**
	 * 任务描述信息
	 * @return
	 */
	String remark() default "";

}
