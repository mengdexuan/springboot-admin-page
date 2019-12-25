package com.boot.biz.camel;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author mengdexuan on 2019/6/28 9:26.
 */
@Slf4j
@Component
public class RouteUtil {

	@Autowired
	CamelContext camelContext;

	public void start(String routeId){
		try {
			camelContext.startRoute(routeId);
		} catch (Exception e) {
			log.error("启动 Route 失败，RouteId:{}！",routeId,e);
		}
	}

	public void stop(String routeId){
		try {
			camelContext.stopRoute(routeId);
		} catch (Exception e) {
			log.error("停止 Route 失败，RouteId:{}！",routeId,e);
		}
	}

	public void remove(String routeId){
		try {
			camelContext.removeRoute(routeId);
		} catch (Exception e) {
			log.error("删除 Route 失败，RouteId:{}！",routeId,e);
		}
	}

	public Route getRoute(String routeId){
		return camelContext.getRoute(routeId);
	}
}
