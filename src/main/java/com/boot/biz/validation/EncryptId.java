package com.boot.biz.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 * 业务需求总是比框架提供的这些简单校验要复杂的多，我们可以自定义校验来满足我们的需求。
 *
 * 自定义spring validation非常简单，假设我们自定义加密id（由数字或者a-f的字母组成，1-8长度）校验，主要分为两步：
 *
 * 自定义约束注解
 *
 * @author mengdexuan on 2022/1/4 18:23.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {EncryptIdValidator.class})
public @interface EncryptId {

	// 默认错误消息
	String message() default "加密id格式错误";

	// 分组
	Class<?>[] groups() default {};

	// 负载
	Class<? extends Payload>[] payload() default {};

}
