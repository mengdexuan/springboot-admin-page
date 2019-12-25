package com.boot.biz.db;

import lombok.Data;

/**
 * 表字段信息
 */
@Data
public class TableColumnDesc {
	private String id;
	private String field;
	private String type;
	private String nullable;
	private String key;
	private String extra;
	private String comment;
}
