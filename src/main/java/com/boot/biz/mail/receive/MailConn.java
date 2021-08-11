package com.boot.biz.mail.receive;

import lombok.Data;

import javax.mail.Folder;
import javax.mail.Store;

/**
 * @author mengdexuan on 2021/8/11 11:37.
 */
@Data
public class MailConn {
	Store store;
	Folder folder;
}
