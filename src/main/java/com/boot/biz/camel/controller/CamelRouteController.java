package com.boot.biz.camel.controller;


import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.biz.camel.RouteUtil;
import com.boot.biz.camel.entity.CamelRoute;
import com.boot.biz.camel.service.CamelRouteService;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * camel路由 控制器
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-06-27
 */
@RestController
@RequestMapping("/camel/camelRoute")
public class CamelRouteController {

	@Autowired
	private CamelRouteService camelRouteService;

	@Autowired
	CamelContext camelContext;

	@Autowired
	RouteUtil routeUtil;

	/**
	 * 获取camel路由列表
	 */
	@GetMapping(value = "/list")
	public Result<List<CamelRoute>> list() {
		return ResultUtil.buildSuccess(camelRouteService.findAll());
	}


	@GetMapping(value = "/get/{routeId}")
	public Result get(@PathVariable("routeId")String routeId) {

		Route route = camelContext.getRoute(routeId);

		String routeStr = route.getRouteContext().getRoute().toString();

		return ResultUtil.buildSuccess((Object) routeStr);
	}


	@PostMapping("/update")
	public Result update(String routeId) {

		camelRouteService.triggerRouteStatus(routeId);

		return ResultUtil.buildSuccess();
	}


	@DeleteMapping("/del/{routeId}")
	public Result del(@PathVariable("routeId")String routeId) {

		try {
			routeUtil.stop(routeId);

			camelContext.removeRoute(routeId);
		} catch (Exception e) {

		}

		return ResultUtil.buildSuccess();
	}


}

