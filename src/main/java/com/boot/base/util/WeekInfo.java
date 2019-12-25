package com.boot.base.util;

import lombok.Data;

/**
 * @author mengdexuan on 2019/9/3 11:57.
 */
@Data
public class WeekInfo {

	//某年的第几周
	private int index;

	//周的开始时间，yyyy-MM-dd
	private String beginDate;

	//周的结束时间，yyyy-MM-dd
	private String endDate;
}
