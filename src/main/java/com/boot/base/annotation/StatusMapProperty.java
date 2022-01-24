package com.boot.base.annotation;

/**
 * @author mengdexuan on 2022/1/24 16:33.
 */
public @interface StatusMapProperty {
	String name() default "";
	String key();
	String val();
}
