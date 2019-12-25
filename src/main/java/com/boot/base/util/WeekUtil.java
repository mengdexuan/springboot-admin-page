package com.boot.base.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author mengdexuan on 2019/9/3 17:38.
 */
@Slf4j
public class WeekUtil {


	/**
	 * 查询指定年份的周列表
	 * @param year
	 * @return
	 */
	public static List<WeekInfo> weekInfoList(int year){

		String dateStr = year + "-01-01";
		cn.hutool.core.date.DateTime firstDay = DateUtil.parse(dateStr, "yyyy-MM-dd");

		cn.hutool.core.date.DateTime weekDay = DateUtil.endOfWeek(firstDay);

		List<WeekInfo> weekInfoList = Lists.newArrayList();

		WeekInfo weekInfo = new WeekInfo();
		weekInfo.setBeginDate(DateUtil.format(firstDay,"yyyy-MM-dd"));

		weekInfo.setEndDate(DateUtil.format(weekDay,"yyyy-MM-dd"));

		weekInfo.setIndex(1);

		weekInfoList.add(weekInfo);

		for (int i=1;i<=52;i++){
			WeekInfo weekInfoTemp = new WeekInfo();

			weekDay = DateUtil.offsetDay(weekDay, 1);

			weekInfoTemp.setBeginDate(DateUtil.format(weekDay,"yyyy-MM-dd"));

			weekDay = DateUtil.offsetDay(weekDay, 6);

			weekInfoTemp.setEndDate(DateUtil.format(weekDay,"yyyy-MM-dd"));

			weekInfoTemp.setIndex(i+1);

			weekInfoList.add(weekInfoTemp);
		}


		WeekInfo last = weekInfoList.get(weekInfoList.size() - 1);

		DateTime temp = DateUtil.parse(last.getBeginDate(), "yyyy-MM-dd");
		int year2 = DateUtil.year(temp);

		if (year2>year){
			weekInfoList.remove(weekInfoList.size()-1);

			last = weekInfoList.get(weekInfoList.size() - 1);

			temp = DateUtil.parse(last.getBeginDate(), "yyyy-MM-dd");
			year2 = DateUtil.year(temp);

			if (year2>year) {
				weekInfoList.remove(weekInfoList.size() - 1);
			}
		}

		return weekInfoList;
	}



}
