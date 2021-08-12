package com.boot.biz.mail.listener;

import com.boot.biz.mail.receive.MailMsgWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 监听新邮件的到来并处理
 * @author mengdexuan on 2021/8/11 16:37.
 */
@Slf4j
@Service
public class MailListener {


	@Async
	@EventListener
	public void listener(MailMsgWrapper wrapper){

		log.info("listener_1:{}",wrapper);

	}

	@Async
	@EventListener
	public void listener2(MailMsgWrapper wrapper){

		log.info("listener_2:{}",wrapper);

	}


















}
