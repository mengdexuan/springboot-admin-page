package com.boot.biz.webshell.sftp;

import lombok.Data;

@Data
public class SftpFileBean implements Comparable<SftpFileBean>{
	
	private String filename = null;
	private String size = null;
	private Integer intPermissions = null;
	private String strPermissions = null;
	private String octalPermissions = null;
	private String mtime = null;
	private boolean directory;
	
	public SftpFileBean() {
		super();
	}
	

	public SftpFileBean(String filename, String size, Integer intPermissions, String strPermissions,
			String octalPermissions, String mtime, boolean directory) {
		super();
		this.filename = filename;
		this.size = size;
		this.intPermissions = intPermissions;
		this.strPermissions = strPermissions;
		this.octalPermissions = octalPermissions;
		this.mtime = mtime;
		this.directory = directory;
	}


	@Override
	public int compareTo(SftpFileBean o) {
		if (this.directory && !o.directory) {
			return -1;
		} else if (!(this.directory ^ o.directory)) {
			return this.filename.compareTo(o.filename);
		} else if (!this.directory && o.directory) {
			return 1;
		}
		return 0;
	}
}
