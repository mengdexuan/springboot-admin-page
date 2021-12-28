package com.boot.base.mail.send.dto;

import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class Email implements Serializable {
	private static final long serialVersionUID = 1L;

	//必填参数
	private List<String> emailList;//接收方邮件列表
	private String subject;//主题
	private String content;//邮件内容


	//选填
	private String template;//模板
	private Map<String, Object> kvMap;// 自定义参数
	private String personal;//发件人名称
	private Map<String,File> imageWithHtml;//key: 图片id，在img标签里使用  val:图片实体
	private List<File> attachmentList;//附件列表

}
