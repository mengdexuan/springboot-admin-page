package com.boot.base.mail.receive;

import lombok.Data;

import java.util.List;

/**
 * @author mengdexuan on 2021/8/12 11:43.
 */
@Data
public class MailMsgWrapper {

	private List<MailMsg> mailMsgList;

}
