package com.boot.biz.camel;

import cn.hutool.core.map.MapUtil;
import com.boot.biz.camel.entity.CamelRoute;
import com.boot.biz.camel.service.CamelRouteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.EventDrivenConsumerRoute;
import org.apache.camel.support.LifecycleStrategySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mengdexuan on 2020/3/1 16:47.
 */
@Slf4j
@Component
public class CamelLifecycleStrategySupport extends LifecycleStrategySupport implements CommandLineRunner {

	@Autowired
	CamelContext camelContext;
	@Autowired
	CamelRouteService camelRouteService;
	@Autowired
	RouteUtil routeUtil;

	@PostConstruct
	private void init(){
		camelContext.addLifecycleStrategy(this);
	}

	@Override
	public void onRoutesAdd(Collection<Route> routes) {
		super.onRoutesAdd(routes);
		addRouteToDb(routes);
	}

	@Override
	public void onRoutesRemove(Collection<Route> routes) {
		super.onRoutesRemove(routes);
		delRouteFromDb(routes);
	}

	@Override
	public void run(String... args) throws Exception {
		startOrStopRoute();
	}


	private void startOrStopRoute(){
		List<CamelRoute> dbList = camelRouteService.findAll();

		for (CamelRoute item:dbList){

			Route route = camelContext.getRoute(item.getRouteId());
			if (route==null){
				camelRouteService.delete(item);
				continue;
			}

			if (ServiceStatus.Started.name().equals(item.getStatus())){
				routeUtil.start(item.getRouteId());
			}else if (ServiceStatus.Stopped.name().equals(item.getStatus())){
				routeUtil.stop(item.getRouteId());
			}

			Route memRoute = routeUtil.getRoute(item.getRouteId());
			if (memRoute!=null){
				item.setDescription(routeDesc(memRoute));
				//更新描述信息
				camelRouteService.save(item);
			}
		}
	}

	private void addRouteToDb(Collection<Route> routes){
		for (Route route:routes){
			try {
				CamelRoute one = camelRouteService.getByFieldEqual("routeId", route.getId());
				if (one==null){
					CamelRoute camelRoute = trans2CamelRoute(route);
					camelRouteService.save(camelRoute);
				}
			}catch (Exception e){
			}
		}
	}

	private void delRouteFromDb(Collection<Route> routes){
		try {
			List<String> idList = routes.stream().map(item -> {
				return item.getId();
			}).collect(Collectors.toList());

			List<CamelRoute> list = camelRouteService.listByFieldEqual("routeId", idList);

			camelRouteService.delete(list);
		}catch (Exception e){
		}
	}

	private CamelRoute trans2CamelRoute(Route route){
		CamelRoute camelRoute = new CamelRoute();

		EventDrivenConsumerRoute routeTemp = ((EventDrivenConsumerRoute) route);

		String description = routeDesc(route);

		camelRoute.setRouteId(route.getId());
		camelRoute.setDescription(description);
		camelRoute.setStatus(routeTemp.getStatus().toString());
		camelRoute.setCreateTime(new Date());

		return camelRoute;
	}

	private String routeDesc(Route route){

		Map<String, Object> map = route.getProperties();

		String description = MapUtil.getStr(map, "description");

		return description;
	}


}
