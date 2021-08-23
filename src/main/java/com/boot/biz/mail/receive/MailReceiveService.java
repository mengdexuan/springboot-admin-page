package com.boot.biz.mail.receive;

import cn.hutool.core.date.DatePattern;
import com.google.common.collect.Lists;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author mengdexuan on 2021/8/10 18:24.
 */
@Slf4j
@Service
public class MailReceiveService implements CommandLineRunner {

	@Value("${spring.mail.receive.protocol}")
	private String protocol;

	@Value("${spring.mail.receive.host}")
	private String host;

	@Value("${spring.mail.receive.port}")
	private String port;

	@Value("${spring.mail.receive.username}")
	private String username;

	@Value("${spring.mail.receive.password}")
	private String password;

	IMAPStore store = null;
	IMAPFolder folder = null;


	@Override
	public void run(String... args) throws Exception {

		Properties props = new Properties();
		props.setProperty("mail.store.protocol", protocol);
		props.setProperty("mail.imap.host", host);
		props.setProperty("mail.imap.port", port);

		HashMap IAM = new HashMap();
//带上IMAP ID信息，由key和value组成，例如name，version，vendor，support-email等。
		IAM.put("name", "myname");
		IAM.put("version", "1.0.0");
		IAM.put("vendor", "myclient");
		IAM.put("support-email", username);
		Session session = Session.getInstance(props);

		store = (IMAPStore) session.getStore(protocol);
		store.connect(username, password);

		store.id(IAM);
		folder = (IMAPFolder) store.getFolder("Inbox");
		folder.open(Folder.READ_WRITE);

		log.info("mail conn success...");
	}


/**
 * 邮件连接缓存
 */
	/*Cache<String, Store> cache = Caffeine.newBuilder()
			.expireAfterWrite(30, TimeUnit.MINUTES)
			.maximumSize(10)
			.build();*/

	/**
	 * 初始化邮件 store ，如果缓存中不存在，则再次获取连接（会自动放入缓存）
	 */
	/*private void initStore() {
		store = cache.get(userName, new Function<String, Store>() {
			@Override
			public Store apply(String name) {
				//获取新连接之前，将之前的释放掉
				closeStore();
				return getConn();
			}
		});
	}*/


	/**
	 * 获取新邮件
	 *
	 * @return
	 */
	public List<MailMsg> receive() {

		List<Message> tempList = Lists.newArrayList();
		List<MailMsg> mailMsgList = Lists.newArrayList();

		try {
			int msgCount = folder.getMessageCount();
			int newMsgCount = folder.getNewMessageCount();

//			int delMsgCount = folder.getDeletedMessageCount();
//			int unreadMessageCount = folder.getUnreadMessageCount();
//			log.info("msgCount:{},newMsgCount:{},unreadMessageCount:{}", msgCount, newMsgCount, unreadMessageCount);

			for (int i = 1, j = msgCount; i <= newMsgCount; i++) {
				Message msg = folder.getMessage(j--);
				//设为已读
				msg.setFlag(Flags.Flag.SEEN, true);
				tempList.add(msg);
			}

		} catch (Exception e) {
			log.error("获取邮件失败！", e);
		}


		for (Message item : tempList) {
			try {
				MimeMessage msg = (MimeMessage) item;

				MailMsg dto = new MailMsg();
				dto.setSize(msg.getSize());
				dto.setContainAttachment(MailUtil.isContainAttachment(msg));
				dto.setFrom(MailUtil.getFrom(msg));
				dto.setReceiveAddress(MailUtil.getReceiveAddress(msg, null));
				dto.setSentDate(msg.getSentDate());
				dto.setSubject(MailUtil.getSubject(msg));

				StringBuffer sb = new StringBuffer();
				MailUtil.getMailTextContent(msg, sb);

				dto.setContent(sb.toString());

				mailMsgList.add(dto);
			} catch (Exception e) {
				log.error("解析邮件失败！", e);
			}
		}

		return mailMsgList;
	}




}
