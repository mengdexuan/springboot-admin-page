package com.boot.biz.mail.receive;

import cn.hutool.core.collection.CollUtil;
import com.boot.base.job.annotation.JobCron;
import com.github.jsonzou.jmockdata.utils.MockUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mengdexuan on 2021/8/10 23:05.
 */
@Slf4j
@Component
public class MailReceiveJob {

	@Autowired
	MailReceiveService mailReceiveService;

	@Autowired
	ApplicationContext applicationContext;


	@JobCron(name = "定时收取邮件",cron = "0/3 * * * * *",delWhenSuccess = false,autoCreate = true)
	public void run(Long jobId)throws Exception{

		List<MailMsg> msgList = mailReceiveService.receive();

		if (CollUtil.isNotEmpty(msgList)){
			MailMsgWrapper mailMsgWrapper = new MailMsgWrapper();
			mailMsgWrapper.setMailMsgList(msgList);
			//发送事件
			applicationContext.publishEvent(mailMsgWrapper);
		}

	}


}
