package com.boot.biz.serverinfo.info;

import cn.hutool.core.date.DateUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.serverinfo.util.Arith;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * JVM相关信息
 */
@ApiModel("JVM相关信息")
public class Jvm {
	/**
	 * 当前JVM占用的内存总数(M)
	 */
	@ApiModelProperty("当前JVM占用的内存总数(M)")
	private double total;

	/**
	 * JVM最大可用内存总数(M)
	 */
	@ApiModelProperty("JVM最大可用内存总数(M)")
	private double max;

	/**
	 * JVM空闲内存(M)
	 */
	@ApiModelProperty("JVM空闲内存(M)")
	private double free;

	/**
	 * JAVA版本
	 */
	@ApiModelProperty("JAVA版本")
	private String version;

	/**
	 * JAVA路径
	 */
	@ApiModelProperty("JAVA路径")
	private String home;

	/**
	 * JAVA名称
	 */
	@ApiModelProperty("JAVA名称")
	private String name;

	/**
	 * 运行时长
	 */
	@ApiModelProperty("运行时长")
	private String runTime;

	public double getTotal() {
		return Arith.div(total, (1024 * 1024), 2);
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getMax() {
		return Arith.div(max, (1024 * 1024), 2);
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getFree() {
		return Arith.div(free, (1024 * 1024), 2);
	}

	public void setFree(double free) {
		this.free = free;
	}

	public double getUsed() {
		return Arith.div(total - free, (1024 * 1024), 2);
	}

	public double getUsage() {
		return Arith.mul(Arith.div(total - free, total, 4), 100);
	}

	/**
	 * 获取JDK名称
	 */
	public String getName() {
		return ManagementFactory.getRuntimeMXBean().getVmName();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	/**
	 * 启动时间
	 */
	@ApiModelProperty("启动时间")
	public String getStartTime() {
		Date date = startTime();
		return DateUtil.format(date, HelpMe.yyyy_MM_dd_HH_mm_ss);
	}

	/**
	 * JDK运行时间
	 */
	public String getRunTime() {
		return getDatePoor(new Date(), startTime());
	}

	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}

	private Date startTime(){
		long time = ManagementFactory.getRuntimeMXBean().getStartTime();
		Date date = new Date(time);
		return date;
	}


	public static String getDatePoor(Date endDate, Date nowDate) {
		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff % nd / nh;
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		// long sec = diff % nd % nh % nm / ns;
		return day + "天" + hour + "小时" + min + "分钟";
	}
}
