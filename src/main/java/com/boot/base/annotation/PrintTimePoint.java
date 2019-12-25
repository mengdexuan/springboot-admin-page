package com.boot.base.annotation;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.stereotype.Component;


/**
 * @author mengdexuan on 2018/5/8 20:28.
 */
@Aspect
@Slf4j
@Component
public class PrintTimePoint {

	/**
	 * 7 秒
	 */
	private static int timeLimit = 7*1000;

	@Around("@annotation(PrintTime)")
	public Object printTime(ProceedingJoinPoint pjp) throws Throwable {
		MethodInvocationProceedingJoinPoint point = (MethodInvocationProceedingJoinPoint)pjp;

		Object[] args = point.getArgs();
		Signature signature= point.getSignature();

		String source = signature.getDeclaringTypeName()+"."+signature.getName();

		Object result = null;

		long time = 0L;

		try {
			long time1 = System.currentTimeMillis();
			result = point.proceed(args);
			long time2 = System.currentTimeMillis();

			time = time2 - time1;
		}catch (Exception e){
			log.error("方法异常！",e);
			throw e;
		}
		String jsonArgs = JSONUtil.toJsonStr(args);
		String jsonResult = JSONUtil.toJsonStr(result==null?"":result);

		log.info("方法入参-->{}",jsonArgs);
		log.info("{} 方法用时-->{}(ms)",source,time);
		if (time>timeLimit){
			log.info("方法执行时间太长！");
		}
		log.info("返回结果-->{}",jsonResult);

		return result;
	}











}
