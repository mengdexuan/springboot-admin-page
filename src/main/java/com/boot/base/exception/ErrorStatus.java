package com.boot.base.exception;

/**
 * ErrorStatus.
 */
public enum ErrorStatus {


	//1000 - 1999   , sys
	//2000 - 2999   , app
	//3000 - 3999   , web
	//4000 - 4999   , biz

	/**
	 * 系统内部错误
	 */
	INTERNAL_SERVER_ERROR(1000, "系统错误"),
	/**
	 * 参数错误
	 */
	ILLEGAL_ARGUMENT(1001, "参数错误"),
	/**
	 * 业务错误
	 */
	SERVICE_EXCEPTION(1002, "业务错误"),
	/**
	 * 非法的数据格式，参数没有经过校验
	 */
	ILLEGAL_DATA(1003, "数据错误"),

	MULTIPART_TOO_LARGE(1004, "文件太大"),
	/**
	 * 非法状态
	 */
	ILLEGAL_STATE(10005, "非法状态"),
	/**
	 * 缺少参数
	 */
	MISSING_ARGUMENT(1006, "缺少参数"),
	/**
	 * 非法访问
	 */
	ILLEGAL_ACCESS(1007, "非法访问,没有认证"),
	/**
	 * 权限不足
	 */
	UNAUTHORIZED(1008, "权限不足"),

	/**
	 * 错误的请求
	 */
	METHOD_NOT_ALLOWED(1009, "不支持的方法"),


	/**
	 * 参数错误
	 */
	ILLEGAL_ARGUMENT_TYPE(1010, "参数类型错误"),


	/**
	 * 接口超出请求限制
	 */
	URL_REQUEST_LIMIT(2000, "接口超出请求限制");


	private final int value;

	private final String message;


	ErrorStatus(int value, String message) {
		this.value = value;
		this.message = message;
	}


	/**
	 * Return the integer value of this status code.
	 */
	public int value() {
		return this.value;
	}

	/**
	 * Return the reason phrase of this status code.
	 */
	public String getMessage() {
		return this.message;
	}

}
