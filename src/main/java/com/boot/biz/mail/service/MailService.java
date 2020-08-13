package com.boot.biz.mail.service;


import com.boot.biz.mail.service.dto.Email;

import java.util.List;

public interface MailService {
	/**
	 * 纯文本
	 */
	void send(Email mail) throws Exception;

	/**
	 * 富文本
	 */
	void sendHtml(Email mail) throws Exception;

	/**
	 * 测试方法
	 * @param emailList
	 */
	void test(List<String> emailList);
}
