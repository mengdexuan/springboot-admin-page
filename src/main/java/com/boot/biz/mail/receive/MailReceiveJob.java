package com.boot.biz.mail.receive;

import cn.hutool.core.collection.CollUtil;
import com.boot.base.job.annotation.JobCron;
import com.boot.base.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

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

		List<MsgDto> msgList = mailReceiveService.receive();

		if (CollUtil.isNotEmpty(msgList)){
			//发送事件
			applicationContext.publishEvent(msgList);
		}

	}


}
