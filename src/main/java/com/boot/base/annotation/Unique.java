package com.boot.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库字段唯一性标识，会在JPA保存实体之前进行校验
 * @author mengdexuan on 2020/4/21 11:26.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {
	String value() default "";
}
