package com.boot.biz.webshell.ftp;

import lombok.Data;

import java.util.List;

/**
 * @author mengdexuan on 2019/11/30 17:42.
 */
@Data
public class FtpBean {
	private List<FtpFile> ftpFileList;
	private String currDir = "/";//当前目录
	private Integer dirCount = 0;
	private Integer fileCount = 0;
	private Boolean showFirstRow;
}
