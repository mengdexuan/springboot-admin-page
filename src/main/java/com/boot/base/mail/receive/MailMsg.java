package com.boot.base.mail.receive;

import lombok.Data;

import java.util.Date;

/**
 * @author mengdexuan on 2021/8/11 12:25.
 */
@Data
public class MailMsg {

	//邮件主题
	private String subject;
	//邮件发件人
	private String from;
	//根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
	private String receiveAddress;
	//邮件发送时间
	private Date sentDate;

	//邮件中是否包含附件
	private boolean isContainAttachment;

	//邮件文本内容
	private String content;

	//邮件大小
	private int size;

	@Override
	public String toString() {
		return "MailMsg{" +
				"subject='" + subject + '\'' +
				", from='" + from + '\'' +
				", receiveAddress='" + receiveAddress + '\'' +
				", sentDate='" + sentDate + '\'' +
				", isContainAttachment=" + isContainAttachment +
				", content='" + content + '\'' +
				", size=" + size +
				'}';
	}
}
