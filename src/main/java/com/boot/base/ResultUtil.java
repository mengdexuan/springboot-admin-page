package com.boot.base;


import com.boot.base.exception.ErrorStatus;

/**
 * ResultUtil.
 *
 * @author Ryan Jiang on 2019/4/23 18:00.
 */
public final class ResultUtil {

	/**
	 * 构建返回结果
	 * @param success
	 * @param msg
	 * @param code
	 * @param data
	 * @return
	 */
	public static <T> Result<T> build(Boolean success, String msg, Integer code,T data){
		return new Result<>(success,msg,code,data);
	}

	/**
	 * 构建返回结果，code默认值为0
	 * @param success
	 * @param msg
	 * @param data
	 * @return
	 */
	public static <T> Result<T> build(Boolean success,String msg,T data){
		return build(success,msg,0,data);
	}

	/**
	 *构建成功结果
	 * @param msg
	 * @param data
	 * @return
	 */
	public static  <T> Result<T> buildSuccess(String msg,T data) {
		return build(Boolean.TRUE,msg,data );
	}

	/**
	 * 构建失败结果
	 * @param msg
	 * @param data
	 * @return
	 */
	public static <T> Result<T> buildFailure(Integer code,String msg,T data){
		return build(Boolean.FALSE,msg ,code,data);
	}

	/**
	 * 构建失败结果
	 * @param msg
	 * @param data
	 * @return
	 */
	public static  <T> Result<T>  buildFailure(String msg,T data){
		return build(Boolean.FALSE,msg ,data);
	}

	/**
	 * 构建成功结果带信息
	 * @return
	 */
	public static <T> Result<T> buildSuccess(){
		return buildSuccess(null,null);
	}


	/**
	 * 构建成功结果待数据
	 * @param data
	 * @return
	 */
	public static  <T> Result buildSuccess(T data){
		return buildSuccess(null,data);
	}

	/**
	 * 构建失败结果待数据
	 * @param msg
	 * @return
	 */
	public static Result buildFailure(String msg){
		return buildFailure(msg,null);
	}

	/**
	 * 构建失败结果待数据
	 * @param code
	 * @param msg
	 * @return
	 */
	public static <T> Result<T> buildFailure(Integer code,String msg){
		return build(Boolean.FALSE,msg,code,null);
	}

	public static <T> Result<T> buildFailure(ErrorStatus errorStatus){
		return build(Boolean.FALSE,errorStatus.getMessage(),errorStatus.value(),null);
	}

	public static <T> Result<T> buildFailure(ErrorStatus errorStatus,String msg){
		return build(Boolean.FALSE,msg,errorStatus.value(),null);
	}

	/**
	 * 构建失败结果带数据
	 * @param data
	 * @return
	 */
	public static  <T> Result buildFailure(T data){
		return buildFailure("",data);
	}

}
