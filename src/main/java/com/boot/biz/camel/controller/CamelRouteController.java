package com.boot.biz.camel.controller;


import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.biz.camel.entity.CamelRoute;
import com.boot.biz.camel.service.CamelRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	/**
	 * 获取camel路由列表
	 */
	@GetMapping(value = "/list")
	public Result<List<CamelRoute>> list() {
		return ResultUtil.buildSuccess(camelRouteService.findAll());
	}


	@PostMapping("/update")
	public Result update(String routeId) {

		camelRouteService.triggerRouteStatus(routeId);

		return ResultUtil.buildSuccess();
	}


}

