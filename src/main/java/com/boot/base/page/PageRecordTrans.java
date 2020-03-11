package com.boot.base.page;

import org.springframework.data.domain.Page;

/**
 * @author mengdexuan on 2020/2/7 11:48.
 */
public class PageRecordTrans {

	/**
	 * 将 jpa 查询出来的 Page 对象转换为 PageRecord
	 * @param page
	 * @param <T>
	 * @return
	 */
	public static <T> PageRecord<T> page2PageRecord(Page<T> page) {
		PageRecord pageRecord = new PageRecord();

		pageRecord.setDataList(page.getContent());
		pageRecord.setIsFirst(page.isFirst());
		pageRecord.setIsLast(page.isLast());
		pageRecord.setTotalPage(page.getTotalPages());
		pageRecord.setTotalSize(page.getTotalElements());
		//jpa 是从第 0 页开始查询的
		pageRecord.setPageNo(page.getNumber()+1);
		pageRecord.setPageSize(page.getSize());

		return pageRecord;
	}

}
