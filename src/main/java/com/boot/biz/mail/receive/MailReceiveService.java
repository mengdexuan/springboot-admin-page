package com.boot.biz.mail.receive;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author mengdexuan on 2021/8/10 18:24.
 */
@Slf4j
@Service
public class MailReceiveService implements CommandLineRunner {

	@Value("${spring.mail.username}")
	public String userName;
	@Value("${spring.mail.password}")
	public String pwd;

	Store store = null;

	@Override
	public void run(String... args) throws Exception {
		try {
			store = getConn();
		} catch (Exception e) {
			log.error("获取邮件连接失败！",e);
		}
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

		Folder folder = openFolder(store);

		Message[] msgArr = null;

		try {
			int newMsgCount = folder.getNewMessageCount();
			int delMsgCount = folder.getDeletedMessageCount();
			int msgCount = folder.getMessageCount();

//			log.info("msgCount:{},delMsgCount:{},newMsgCount:{}", msgCount, delMsgCount, newMsgCount);

			if (msgCount > 0) {
				List<Message> tempList = Lists.newArrayList();
				Message[] arr = folder.getMessages();
				for (Message item : arr) {
					if (item.getFlags().contains(Flags.Flag.DELETED)) {
					} else {
						tempList.add(item);
					}
				}
				msgArr = new Message[tempList.size()];

				tempList.toArray(msgArr);
			}
		} catch (MessagingException e) {
			log.error("获取邮件失败！", e);
		}

		List<MailMsg> mailMsgList = Lists.newArrayList();

		if (msgArr != null) {
			for (Message item : msgArr) {
				try {
					MimeMessage msg = (MimeMessage) item;

					MailMsg dto = new MailMsg();
					dto.setSize(msg.getSize());
					dto.setContainAttachment(MailUtil.isContainAttachment(msg));
					dto.setFrom(MailUtil.getFrom(msg));
					dto.setReceiveAddress(MailUtil.getReceiveAddress(msg, null));
					dto.setSentDate(MailUtil.getSentDate(msg, null));
					dto.setSubject(MailUtil.getSubject(msg));

					StringBuffer sb = new StringBuffer();
					MailUtil.getMailTextContent(msg, sb);

					dto.setContent(sb.toString());

					mailMsgList.add(dto);
				} catch (Exception e) {
					log.error("解析邮件失败！", e);
				}
			}
		}


		try {
			MailUtil.markedMsg(Flags.Flag.DELETED, msgArr);
		} catch (Exception e) {
			log.error("标记邮件删除失败！", e);
		}

		closeFolder(folder);

		return mailMsgList;
	}


	private Folder openFolder(Store store) {
		Folder folder = null;
		try {
			// 获得收件箱
			folder = store.getFolder("INBOX");
			/* Folder.READ_ONLY：只读权限
			 * Folder.READ_WRITE：可读可写（可以修改邮件的状态）
			 */
			folder.open(Folder.READ_WRITE); //打开收件箱
		} catch (Exception e) {
			log.error("获取 folder 失败！", e);
		}
		return folder;
	}

	private void closeFolder(Folder folder) {
		try {
			folder.close(true);
		} catch (MessagingException e) {
		}
	}



	private void closeStore() {
		//释放资源
		if (store != null) {
			try {
				store.close();
			} catch (Exception e) {
			}
			log.info("释放邮件服务器连接...");
		}
	}





	private Store getConn() {
		Store store = null;

		String port = "110";   // 端口号
		String servicePath = "pop3.163.com";   // 服务器地址

		// 准备连接服务器的会话信息
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "pop3");       // 使用pop3协议
		props.setProperty("mail.pop3.port", port);           // 端口
		props.setProperty("mail.pop3.host", servicePath);       // pop3服务器

		try {
			// 创建Session实例对象
			Session session = Session.getInstance(props);
			store = session.getStore("pop3");
			store.connect(userName, pwd); //163邮箱程序登录属于第三方登录所以这里的密码是163给的授权密码而并非普通的登录密码
		} catch (Exception e) {
			log.error("获取邮件服务器连接失败！", e);
		}

		log.info("获取邮件服务器连接...");

		return store;
	}




}
