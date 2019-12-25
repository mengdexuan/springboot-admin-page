package com.boot.biz.webshell.sftp;

import lombok.Data;

import java.util.List;

@Data
public class SftpBean {

	private String currentCatalog;
	private Integer fileCount;
	private Integer dirCount;
	private List<SftpFileBean> files;
	
	public SftpBean() {
		super();
	}
	public SftpBean(String currentCatalog, List<SftpFileBean> files) {
		super();
		this.currentCatalog = currentCatalog;
		this.files = files;
	}
	
	
}
