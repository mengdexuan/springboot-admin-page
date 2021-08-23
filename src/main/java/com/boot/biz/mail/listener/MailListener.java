package com.boot.biz.mail.listener;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.boot.biz.mail.receive.MailMsgWrapper;
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
public class MailListener {


	@Async
	@EventListener
	public void listener(MailMsgWrapper wrapper){

		List<String> subjectList = wrapper.getMailMsgList().stream().map(item -> {
			return item.getSubject()+","+DateUtil.format(item.getSentDate(),DatePattern.NORM_DATETIME_PATTERN);
		}).collect(Collectors.toList());

		log.info("listener_1:{}",subjectList);

	}

	@Async
	@EventListener
	public void listener2(MailMsgWrapper wrapper){

		List<String> subjectList = wrapper.getMailMsgList().stream().map(item -> {
			return item.getSubject()+","+DateUtil.format(item.getSentDate(),DatePattern.NORM_DATETIME_PATTERN);
		}).collect(Collectors.toList());

		log.info("listener_2:{}",subjectList);
	}


















}
