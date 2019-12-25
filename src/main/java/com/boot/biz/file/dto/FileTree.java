package com.boot.biz.file.dto;

import lombok.Data;

/**
 * Created by mengdexuan on 2017/6/22 11:51.
 */
@Data
public class FileTree {
	private String id;
	private String name;
	private String sizeStr;
	private String access;
	private Integer level;
	private Long size;
	private Boolean open;
	private Boolean isParent;
	private Boolean canEdit;
	private String lastModifiedTime;
}
