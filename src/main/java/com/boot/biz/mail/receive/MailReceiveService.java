package com.boot.biz.mail.receive;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author mengdexuan on 2021/8/10 18:24.
 */
@Slf4j
@Service
public class MailReceiveService {

	@Value("${spring.mail.username}")
	public String userName;
	@Value("${spring.mail.password}")
	public String pwd;

	MailConn mailConn = null;

	/**
	 * 邮箱连接获取后，一段时间后需要释放，原因是：
	 *
	 * 一个连接打开后无法持续接收到新邮件，必须是新邮件已发送了，然后再打开另一个连接才能收到
	 */
	Cache<String, MailConn> cache = Caffeine.newBuilder()
			.expireAfterWrite(20, TimeUnit.SECONDS)
			.maximumSize(10)
			.build();


	/**
	 * 获取新邮件
	 * @return
	 */
	public List<MsgDto> receive(){
		open();

		Folder folder = mailConn.getFolder();
		Message[] msgArr = null;

		try {
			int newMsgCount = folder.getNewMessageCount();
			int delMsgCount = folder.getDeletedMessageCount();
			int msgCount = folder.getMessageCount();

			log.info("msgCount:{},delMsgCount:{},newMsgCount:{}",msgCount,delMsgCount,newMsgCount);

			if (msgCount>0){
				List<Message> tempList = Lists.newArrayList();
				Message[] arr = folder.getMessages();
				for (Message item:arr){
					if (!item.getFlags().contains(Flags.Flag.DELETED)){
						tempList.add(item);
					}
				}
				msgArr = new Message[tempList.size()];

				tempList.toArray(msgArr);
			}
		} catch (MessagingException e) {
			log.error("获取邮件失败！",e);
		}

		List<MsgDto> msgDtoList = Lists.newArrayList();

		if (msgArr!=null){
			for (Message item:msgArr){
				try {
					MimeMessage msg = (MimeMessage) item;

					MsgDto dto = new MsgDto();
					dto.setSize(msg.getSize());
					dto.setContainAttachment(MailUtil.isContainAttachment(msg));
					dto.setFrom(MailUtil.getFrom(msg));
					dto.setReceiveAddress(MailUtil.getReceiveAddress(msg,null));
					dto.setSentDate(MailUtil.getSentDate(msg,null));
					dto.setSubject(MailUtil.getSubject(msg));

					StringBuffer sb = new StringBuffer();
					MailUtil.getMailTextContent(msg,sb);

					dto.setContent(sb.toString());

					msgDtoList.add(dto);
				}catch (Exception e){
					log.error("解析邮件失败！",e);
				}
			}
		}


		try {
			MailUtil.markedMsg(Flags.Flag.DELETED,msgArr);
		} catch (Exception e) {
			log.error("标记邮件删除失败！",e);
		}

		return msgDtoList;
	}



	private void close(){
		//释放资源
		if(mailConn!=null){
			try {
				mailConn.getFolder().close(true);
				mailConn.getStore().close();
//				log.info("释放邮件服务器连接...");
			} catch (Exception e) {
			}
		}
	}


	private void open(){
		mailConn = cache.get(userName,new Function<String, MailConn>() {
			@Override
			public MailConn apply(String name) {
				close();
//				log.info("{} 重新获取邮件服务器连接...",name);
				return getMailConn();
			}
		});
//		System.out.println(mailConn.getStore().hashCode());
	}


	private MailConn getMailConn(){
		MailConn mailConn = new MailConn();

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
			Store store = session.getStore("pop3");
			store.connect(userName, pwd); //163邮箱程序登录属于第三方登录所以这里的密码是163给的授权密码而并非普通的登录密码

			// 获得收件箱
			Folder folder = store.getFolder("INBOX");
			/* Folder.READ_ONLY：只读权限
			 * Folder.READ_WRITE：可读可写（可以修改邮件的状态）
			 */
			folder.open(Folder.READ_WRITE); //打开收件箱

			mailConn.setFolder(folder);
			mailConn.setStore(store);
		}catch (Exception e){
			log.error("获取邮件服务器连接失败！",e);
		}

		return mailConn;
	}
}
