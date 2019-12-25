package com.boot.biz.user;

import com.boot.biz.user.entity.SysUser;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mengdexuan on 2019/10/25 11:52.
 */
@Slf4j
public class RequestUserHolder {

	private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<>();
	private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

	public static void add(SysUser pbankUser) {
		userHolder.set(pbankUser);
	}

	public static void add(HttpServletRequest request) {
		requestHolder.set(request);
	}

	public static SysUser getCurrentUser() {
		return userHolder.get();
	}

	public static HttpServletRequest getCurrentRequest() {
		return requestHolder.get();
	}

	public static void remove() {
		userHolder.remove();
		requestHolder.remove();
	}
}
