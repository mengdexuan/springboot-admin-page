package com.boot.biz.mail.service.impl;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.mail.service.MailService;
import com.boot.biz.mail.service.dto.Email;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class MailServiceImpl implements MailService {

	@Value("${spring.mail.username}")
	public String USER_NAME;//发送者

	@Autowired
	private JavaMailSender mailSender;//执行者


	static {
		System.setProperty("mail.mime.splitlongparameters", "false");
	}

	@Override
	public void send(Email mail) throws Exception {
		long time1 = System.currentTimeMillis();

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(USER_NAME);
		message.setTo(HelpMe.easyList2Arr(mail.getEmailList()));
		message.setSubject(mail.getSubject());
		message.setText(mail.getContent());
		mailSender.send(message);

		long time2 = System.currentTimeMillis();

		log.info("发送文本邮件成功，接收者：{}，用时：{} ms",mail.getEmailList(),(time2-time1));
	}

	@Override
	public void sendHtml(Email mail) throws Exception {

		long time1 = System.currentTimeMillis();

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(USER_NAME, mail.getPersonal());
		helper.setTo(HelpMe.easyList2Arr(mail.getEmailList()));
		helper.setSubject(mail.getSubject());
		helper.setText(mail.getContent(), true);

		//html 中的图片信息
		Map<String, File> imgMap = mail.getImageWithHtml();
		for (Map.Entry<String, File> entry:imgMap.entrySet()){
			helper.addInline(entry.getKey(), entry.getValue());
		}

		//附件信息
		List<File> list = mail.getAttachmentList();
		for (File file:list){
			helper.addAttachment(file.getName(),file);
		}

		mailSender.send(message);

		long time2 = System.currentTimeMillis();

		double second = NumberUtil.div((time2 - time1), 1000);

		log.info("发送 html 邮件成功，接收者：{}，用时：{} s",mail.getEmailList(),second);
	}


	//邮件发送示例
	@Override
	public void test(List<String> emailList){
		Email email = new Email();
		email.setEmailList(emailList);
		email.setSubject("会员报名成功通知邮件");
		String text = "<html><body><div>会员報名信息如下：</div><ul>" +
				"        <li>報名会员：<span>{userName}</span></li>" +
				"        <li>報名課程：<span>{courseTitle}</span></li>" +
				"        <li>選擇類別：<span>{courseCaseName}</span></li>" +
				"        <li>日期時間：<span>{courseDate}</span></li>" +
				"        <li>交易金額：$<span>{totalMoney}</span></li>" +
				"    </ul>" +
				"<img src='cid:test001'/>" +
				"<br/>" +
				"<img src='cid:test002'/>" +
				"</body></html>";

		Map<String,Object> map = Maps.newHashMap();

		map.put("userName","小明");
		map.put("courseTitle","学习课程");
		map.put("courseCaseName","方案A");
		map.put("courseDate","2020-08-13");
		map.put("totalMoney","123");


		Map<String,File> imageWithHtml = Maps.newHashMap();

		File img1 = new File("C:\\Users\\18514\\Desktop\\test2\\NeonLight.jpg");
		File img2 = new File("C:\\Users\\18514\\Desktop\\test2\\ItalianLeatherCoinBag.jpg");

		imageWithHtml.put("test001",img1);
		imageWithHtml.put("test002",img2);

		String content = StrUtil.format(text, map);
		email.setContent(content);
		email.setPersonal("磁云科技");
		email.setImageWithHtml(imageWithHtml);

		List<File> attachmentList = Lists.newArrayList();

		File attachment1 = new File("C:\\Users\\18514\\Desktop\\test\\记录.xlsx");
		File attachment2 = new File("C:\\Users\\18514\\Desktop\\test\\记录.zip");

		attachmentList.add(attachment1);
		attachmentList.add(attachment2);

		email.setAttachmentList(attachmentList);

		try {
			sendHtml(email);
		} catch (Exception e) {
			log.error("发送邮件失败！",e);
		}


	}

}
