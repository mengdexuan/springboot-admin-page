package com.boot.biz.urllimit;

import cn.hutool.core.collection.CollUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.urllimit.entity.UrlLimit;
import com.boot.biz.urllimit.repository.UrlLimitRepository;
import com.boot.biz.urllimit.service.UrlLimitService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author mengdexuan on 2019/6/23 12:21.
 */
@Order(1)
@Slf4j
@Component
public class UrlLimitCommandLineRunner implements CommandLineRunner{

	@Autowired
	UrlLimitService urlLimitService;
	@Autowired
	RequestMappingHandlerMapping handlerMapping;
	@Autowired
	UrlLimitRepository urlLimitRepository;




	@Override
	public void run(String... args) throws Exception {

		List<UrlLimit> urlLimitList = urlLimitService.findAll();

		Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
		Set<String> urlSet = Sets.newHashSet();

		for (Map.Entry<RequestMappingInfo, HandlerMethod> entry:handlerMethods.entrySet()){
			RequestMappingInfo key = entry.getKey();
			String url = key.getPatternsCondition().toString();
			url = HelpMe.removeFirstChar(url);
			url = HelpMe.removeLastChar(url);
			urlSet.add(url);
		}

		if (HelpMe.isNull(urlLimitList)){
			Date date = new Date();
			List<UrlLimit> tempList = Lists.newArrayList();
			for (String url:urlSet){
				UrlLimit urlLimit = new UrlLimit();
				urlLimit.setReqUrl(url);
				urlLimit.setCreateTime(date);
				urlLimit.setUrlLimit(0);
				urlLimit.setUrlKey(UrlRateLimiterCache.urlKey(url));
				tempList.add(urlLimit);
			}
			urlLimitService.save(tempList);
		}else {

			List<String> dbUrlLimit = urlLimitList.stream().map(item -> {
				return item.getUrlKey();
			}).collect(Collectors.toList());

			List<String> memoryUrlLimit = urlSet.stream().map(item -> {
				return UrlRateLimiterCache.urlKey(item);
			}).collect(Collectors.toList());

			Map<String, String> memoryUrlLimitMap = Maps.newHashMap();

			urlSet.stream().forEach(item->{
				memoryUrlLimitMap.put(UrlRateLimiterCache.urlKey(item),item);
			});


			List<String> diff = (List)CollUtil.disjunction(dbUrlLimit, memoryUrlLimit);

			if (HelpMe.isNull(diff)){
				//db 与 memory 数据一致
			}else {

				dbUrlLimit.forEach(item->{
					if (!memoryUrlLimit.contains(item)){
						//memory 中不存在，db 中存在，需要从 db 中删除

						UrlLimit urlLimit = new UrlLimit();
						urlLimit.setUrlKey(item);
						urlLimitService.delete(urlLimit);
					}
				});

				Date date = new Date();

				memoryUrlLimit.forEach(item->{
					if (!dbUrlLimit.contains(item)){
						//db 中不存在，memory 中存在，需要加入到 db 中

						UrlLimit urlLimit = new UrlLimit();
						urlLimit.setUrlKey(item);
						String reqUrl = memoryUrlLimitMap.get(item);
						urlLimit.setReqUrl(reqUrl);
						urlLimit.setCreateTime(date);
						urlLimit.setUrlLimit(0);
						urlLimitService.save(urlLimit);
					}
				});
			}

		}


		List<UrlLimit> list = urlLimitRepository.findByUrlLimitGreaterThan(0);
		if (HelpMe.isNotNull(list)){
			list.stream().forEach(item->{
				UrlRateLimiterCache.set(item.getUrlKey(),item.getUrlLimit());
			});
		}

	}



}
