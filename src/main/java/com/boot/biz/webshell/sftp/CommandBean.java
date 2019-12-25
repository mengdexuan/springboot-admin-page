package com.boot.biz.webshell.sftp;

import lombok.Data;

import java.io.File;

/**
 * @author mengdexuan on 2019/11/27 11:36.
 */
@Data
public class CommandBean {

	private String cmd;
	private String cmdParam;
	private Boolean isDir;
	private String fileFileName;
	private Integer permissions;

}
