package com.boot.biz.mail.receive.process;

import com.boot.biz.mail.receive.MsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 监听新邮件的到来并处理
 * @author mengdexuan on 2021/8/11 16:37.
 */
@Slf4j
@Service
public class MailProcess {


	@Async
	@EventListener
	public void listener(List<MsgDto> msgList){

		List<String> list = msgList.stream().map(item -> {
			return item.getSubject();
		}).collect(Collectors.toList());

		log.info("listener_1:{}",list);

	}



	@Async
	@EventListener
	public void listener2(List<MsgDto> msgList){

		List<String> list = msgList.stream().map(item -> {
			return item.getSubject();
		}).collect(Collectors.toList());

		log.info("listener_2:{}",list);

	}

















}
