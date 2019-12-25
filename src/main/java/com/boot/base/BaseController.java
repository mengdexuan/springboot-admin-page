package com.boot.base;

/**
 * 基础控制器
 * 
 */
public class BaseController {

	/**
	 *构建成功结果
	 * @param msg
	 * @param data
	 * @return
	 */
	public <T> Result<T> success(String msg, T data) {
		return ResultUtil.build(Boolean.TRUE,msg,data );
	}

	/**
	 * 构建失败结果
	 * @param msg
	 * @param data
	 * @return
	 */
	public <T> Result<T> failure(String msg,T data){
		return ResultUtil.buildFailure(msg ,data);
	}
	/**
	 * 构建失败结果
	 * @param code
	 * @param msg
	 * @param data
	 * @return
	 */
	public <T> Result<T> failure(Integer code,String msg,T data){
		return ResultUtil.buildFailure(code,msg,data);
	}
	/**
	 * 构建失败结果
	 * @param code
	 * @param data
	 * @return
	 */
	public <T> Result<T> failure(Integer code,T data){
		return ResultUtil.buildFailure(code,null,data);
	}

	/**
	 * 构建失败结果
	 * @param code
	 * @return
	 */
	public <T> Result<T> failure(Integer code){
		return ResultUtil.buildFailure(code,null,null);
	}
	/**
	 * 构建成功结果
	 * @return
	 */
	public <T> Result<T> success(){
		return success(null,null);
	}

	/**
	 * 构建成功结果带信息
	 * @param msg
	 * @return
	 */
	public <T> Result<T> success(String msg){
		return success(msg,null);
	}

	/**
	 * 构建成功结果待数据
	 * @param data
	 * @return
	 */
	public  <T> Result<T> success(T data){
		return success(null,data);
	}

	/**
	 * 构建失败结果
	 * @return
	 */
	public <T> Result<T> failure(){
		return failure(0,null,null);
	}

	/**
	 * 构建失败结果待数据
	 * @param msg
	 * @return
	 */
	public <T> Result<T> failure(String msg){
		return failure(msg,null);
	}

	/**
	 * 构建失败结果带数据
	 * @param data
	 * @return
	 */
	public <T> Result<T> failure(T data){
		return failure(0,null,data);
	}
}
