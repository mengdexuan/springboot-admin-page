package com.boot.biz.requestfollow.service.impl;

import com.boot.base.BaseServiceImpl;
import com.boot.base.util.HelpMe;
import com.boot.biz.requestfollow.entity.RequestFollow;
import com.boot.biz.requestfollow.repository.RequestFollowRepository;
import com.boot.biz.requestfollow.service.RequestFollowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 请求追踪记录 服务实现类
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-12-16
 */
@Transactional
@Service
@Slf4j
public class RequestFollowServiceImpl extends BaseServiceImpl<RequestFollow,RequestFollowRepository> implements RequestFollowService {

	private static final int count = 100;

	@Autowired
	RequestFollowRepository requestFollowRepository;

	/**
	 * 每1分钟执行一次，保留最近记录的前 count 条记录，其它删除
	 */
	@Scheduled(cron = "0 */1 * * * ?")
	public void delRequestFollowData(){
		RequestFollow requestFollow = new RequestFollow();
		Example<RequestFollow> example = Example.of(requestFollow);
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		Pageable pageable = PageRequest.of(0,count,sort);

		Page<RequestFollow> page = requestFollowRepository.findAll(example, pageable);

		List<RequestFollow> list = page.getContent();

		if (HelpMe.isNotNull(list)){
			RequestFollow last = list.get(list.size()-1);
			Long id = last.getId();
			requestFollowRepository.delData(id);
		}

	}


}
