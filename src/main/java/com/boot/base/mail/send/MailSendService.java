package com.boot.base.mail.send;


import com.boot.base.mail.send.dto.Email;

import java.util.List;

public interface MailSendService {
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
