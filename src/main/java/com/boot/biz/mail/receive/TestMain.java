package com.boot.biz.mail.receive;

import com.sun.mail.imap.IMAPStore;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Session;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author mengdexuan on 2021/8/23 10:58.
 */
public class TestMain {


	public static void main(String[] args) throws Exception {

		// 准备连接服务器的会话信息
		/*Properties props = new Properties();
		Session session = Session.getInstance(props);
		Store store = session.getStore("imaps");
		store.connect("imap.126.com", 993, "mengdexuan_test@126.com", "ABFVBHJMSOWLEAIT");
		Folder inbox = store.getFolder("Inbox");
		System.out.println();*/


		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imap");
		props.setProperty("mail.imap.host", "imap.126.com");
		props.setProperty("mail.imap.port", "143");

		HashMap IAM = new HashMap();
//带上IMAP ID信息，由key和value组成，例如name，version，vendor，support-email等。
		IAM.put("name","myname");
		IAM.put("version","1.0.0");
		IAM.put("vendor","myclient");
		IAM.put("support-email","mengdexuan_test@126.com");
		Session session = Session.getInstance(props);

		IMAPStore store = (IMAPStore) session.getStore("imap");
		store.connect("mengdexuan_test@126.com", "ABFVBHJMSOWLEAIT");

		store.id(IAM);
		Folder inbox = store.getFolder("Inbox");

		inbox.open(Folder.READ_WRITE);

//		item.setFlag(Flags.Flag.SEEN,true);

		System.out.println();


	}
































}
