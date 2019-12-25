package com.boot.biz.db;

import lombok.Data;

@Data
public class DbTree {
	private String id;
	private String name;
	private String icon;
	private String iconClose;
	private String iconOpen;
	private Integer level;
	private Boolean isParent;
}
