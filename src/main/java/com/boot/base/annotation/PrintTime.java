package com.boot.base.annotation;

import java.lang.annotation.*;

/**
 * 在需要记录执行时间的方法上加入该注解，以打印方法执行时间
 * @author mengdexuan on 2018/5/8 20:24.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrintTime {

}
