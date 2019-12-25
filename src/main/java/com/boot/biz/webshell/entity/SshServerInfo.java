package com.boot.biz.webshell.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "server_info")
public class SshServerInfo {
	@Id
	private String id;
	//主机名称
	private String hostname;
	//端口号
	private Integer port;
	//用户名
	private String username;
	//密码
	private String password;
	//描述
	private String note;

	//1：ssh/sftp	2：ftp
	private Integer type;

	private Date createTime;

	public SshServerInfo(){

	}
	public SshServerInfo(String hostname, String username, String password) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}


}
