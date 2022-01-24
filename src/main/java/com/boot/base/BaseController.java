package com.boot.base;

import cn.hutool.core.util.ReflectUtil;
import com.boot.base.annotation.StatusMap;
import com.boot.base.annotation.StatusMapProperty;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 基础控制器
 * 
 */
public class BaseController {


	/**
	 * 返回实体中被 StatusMap 标注的状态映射关系
	 * @param clz
	 * @return
	 */
	public Map<String,Object> statusMap(Class clz) {

		Map<String,Object> result = Maps.newLinkedHashMap();
		Field[] fields = ReflectUtil.getFields(clz);

		for (Field field : fields) {
			StatusMap statusMap = field.getAnnotation(StatusMap.class);
			if (statusMap != null ) {
				String fieldName = field.getName();
				Map<String,Object> map = Maps.newLinkedHashMap();
				StatusMapProperty[] kvList = statusMap.kvList();
				for (StatusMapProperty kv:kvList){
					String key = kv.key();
					String val = kv.val();
					map.put(key,val);
				}
				result.put(fieldName,map);
			}
		}

		return result;
	}



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
