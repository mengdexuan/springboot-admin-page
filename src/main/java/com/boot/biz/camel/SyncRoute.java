package com.boot.biz.camel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.camel.entity.CamelRoute;
import com.boot.biz.camel.repository.CamelRouteRepository;
import com.boot.biz.camel.service.CamelRouteService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.ServiceStatus;
import org.apache.camel.impl.EventDrivenConsumerRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 同步路由
 * @author mengdexuan on 2019/6/23 12:21.
 */
@Slf4j
@Component
public class SyncRoute {

	@Autowired
	CamelContext camelContext;
	@Autowired
	CamelRouteService camelRouteService;
	@Autowired
	CamelRouteRepository camelRouteRepository;
	@Autowired
	RouteUtil routeUtil;


	public void sync()   {

		List<CamelRoute> dbList = camelRouteService.findAll();

		List<Route> memoryList = camelContext.getRoutes();

		if (HelpMe.isNotNull(memoryList)&&HelpMe.isNull(dbList)){

			List<CamelRoute> camelRouteList = Lists.newArrayList();

			memoryList.stream().forEach(item->{
				CamelRoute camelRoute = trans2CamelRoute(item);
				camelRouteList.add(camelRoute);
			});

			camelRouteService.save(camelRouteList);
			return;
		}

		if (HelpMe.isNull(memoryList)){
			camelRouteRepository.deleteAll();
			return;
		}

		if (HelpMe.isNotNull(memoryList)){
			memoryList.stream().map(item->{
				return item.getId();
			}).collect(Collectors.toList());

			List<String> memoryIdList = memoryList.stream().map(item -> {
				return item.getId();
			}).collect(Collectors.toList());

			List<String> dbIdList = dbList.stream().map(item -> {
				return item.getRouteId();
			}).collect(Collectors.toList());

			List<String> diff = (List)CollUtil.disjunction(memoryIdList, dbIdList);

			if (HelpMe.isNull(diff)){
				//db 与 memory 数据一致
			}else {
				for (String item:dbIdList){
					if (!memoryIdList.contains(item)){
						//memory 中不存在，db 中存在，需要从 db 中删除
						CamelRoute camelRoute = new CamelRoute();
						camelRoute.setRouteId(item);
						camelRouteService.delete(camelRoute);
					}
				}

				Map<String,Route> routeMap = Maps.newHashMap();
				memoryList.stream().forEach(item->{
					routeMap.put(item.getId(),item);
				});

				memoryIdList.forEach(item->{
					if (!dbIdList.contains(item)){
						//db 中不存在，memory 中存在，需要加入到 db 中

						Route route = routeMap.get(item);

						CamelRoute camelRoute = trans2CamelRoute(route);

						camelRouteService.save(camelRoute);
					}
				});
			}

		}

		dbList.clear();
		dbList = camelRouteService.findAll();

		dbList.stream().forEach(item->{
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
		});



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
