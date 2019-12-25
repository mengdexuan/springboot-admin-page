package com.boot.biz.webshell.ftp;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author mengdexuan on 2019/11/30 16:44.
 */
@Data
public class FtpFile {
	private String name;
	private String size;
	private Boolean dir;
	private Date date;
}
