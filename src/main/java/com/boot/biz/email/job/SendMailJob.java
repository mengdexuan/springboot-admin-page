package com.boot.biz.email.job;

import cn.hutool.core.util.StrUtil;
import com.boot.base.job.annotation.JobCron;
import com.boot.base.mail.send.MailSendService;
import com.boot.base.mail.send.dto.Email;
import com.boot.base.util.HelpMe;
import com.boot.biz.email.entity.SysEmail;
import com.boot.biz.email.service.SysEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mengdexuan on 2021/12/28 10:35.
 */
@Slf4j
@Service
public class SendMailJob {

	@Autowired
	SysEmailService emailService;

	@Autowired
	MailSendService mailSendService;


	@JobCron(name = "邮件发送",cron = "0/3 * * * * *",delWhenSuccess = false,
			remark = "邮件发送",autoCreate = true)
	public void run(Long jobId){
		SysEmail sysEmail = new SysEmail();
		sysEmail.setStatus(0);
		Pageable pageable = PageRequest.of(0,3);
		Page<SysEmail> page = emailService.list(sysEmail, pageable);

		List<SysEmail> list = page.getContent();

		for (SysEmail item:list){
			sendMail(item);
		}
	}


	
	private void sendMail(SysEmail sysEmail){

		Email email = new Email();
		email.setFrom(sysEmail.getFromAddr());
		email.setContent(sysEmail.getContent());
		email.setSubject(sysEmail.getTitle());
		email.setEmailList(HelpMe.easySplit(sysEmail.getToAddrs()));

		String errMsg = "";

		try {
			mailSendService.send(email);
		} catch (Exception e) {
			errMsg = e.getMessage();
			errMsg = StrUtil.sub(errMsg,0,1000);
		}

		if (StrUtil.isEmpty(errMsg)){
			sysEmail.setStatus(1);
		}else {
			sysEmail.setStatus(2);
			sysEmail.setErrMsg(errMsg);
		}

		emailService.save(sysEmail);
	}









}
