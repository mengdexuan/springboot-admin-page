package com.boot.biz.camel.service;


import com.boot.base.BaseService;
import com.boot.biz.camel.entity.CamelRoute;

/**
 * <p>
 * camel路由 服务类
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-06-27
 */
public interface CamelRouteService extends BaseService<CamelRoute> {

	/**
	 * 更新 camel 路由状态
	 * @param routeId
	 */
	void triggerRouteStatus(String routeId);
}
