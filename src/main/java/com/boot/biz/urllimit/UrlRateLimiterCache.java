package com.boot.biz.urllimit;

import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;

import java.util.Map;

/**
 * @author mengdexuan on 2019/6/22 19:57.
 */
public class UrlRateLimiterCache {

	private static Map<String,RateLimiter> limitMap = Maps.newConcurrentMap();

	public static RateLimiter get(String urlKey){
		return limitMap.get(urlKey);
	}


	public static void set(String urlKey,Integer urlLimit){
		RateLimiter limiter = RateLimiter.create(urlLimit);
		limitMap.put(urlKey,limiter);
	}

	public static void clear(){
		limitMap.clear();
	}


	public static void remove(String urlKey){
		limitMap.remove(urlKey);
	}


	//md5摘要
	public static String urlKey(String url){
		return DigestUtil.md5Hex(url);
	}











}
