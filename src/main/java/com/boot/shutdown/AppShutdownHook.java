package com.boot.shutdown;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author mengdexuan on 2019/6/17 9:51.
 */
@Slf4j
@Component
public class AppShutdownHook {

	@Qualifier("schedulerFactoryBean")
	@Autowired
	Scheduler quartzScheduler;

	@Autowired
	ThreadPoolExecutor threadPoolExecutor;

	@PostConstruct
	private void init(){
		/**
		 * ============================
		 * Linux kill 命令
		 * ============================
		 * kill 命令常用的信号选项:
		 * (1) kill -2 pid 向指定 pid 发送 SIGINT 中断信号, 等同于 ctrl+c.
		 * (2) kill -9 pid, 向指定 pid 发送 SIGKILL 立即终止信号.
		 * (3) kill -15 pid, 向指定 pid 发送 SIGTERM 终止信号.
		 * (4) kill pid 等同于 kill 15 pid
		 *
		 * SIGINT/SIGKILL/SIGTERM 信号的区别:
		 * (1) SIGINT (ctrl+c) 信号 (信号编号为 2), 信号会被当前进程树接收到, 也就说, 不仅当前进程会收到该信号, 而且它的子进程也会收到.
		 * (2) SIGKILL 信号 (信号编号为 9), 程序不能捕获该信号, 最粗暴最快速结束程序的方法.
		 * (3) SIGTERM 信号 (信号编号为 15), 信号会被当前进程接收到, 但它的子进程不会收到, 如果当前进程被 kill 掉, 它的的子进程的父进程将变成 init 进程 (init 进程是那个 pid 为 1 的进程)
		 *
		 * 一般要结束某个进程, 我们应该优先使用 kill pid , 而不是 kill -9 pid. 如果对应程序提供优雅关闭机制的话, 在完全退出之前, 先可以做一些善后处理.
		 *
		 *
		 * jvm 钩子的触发时机
			 1）程序正常退出
			 2）使用System.exit()
			 3）终端使用Ctrl+C触发的中断
			 4）系统关闭
			 5）使用Kill pid命令干掉进程
		  注：在使用kill -9 pid时JVM注册的钩子不会被触发
		 */
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			hook();
		}));
	}



	private void hook(){
		log.info("系统退出，触发jvm钩子函数...");

		/**
		 * 关闭 quartz 调度器
		 *
		 * shutdown(true)，直到线程中的job执行完毕才关闭调度，所以要求 job
		 * 中的代码不能出现一直阻塞状态，不然永远无法关闭调度器
		 */
		try {
			quartzScheduler.shutdown(true);
		} catch (SchedulerException e) {
			log.error("关闭 quartzScheduler 失败！",e);
		}

	}































}










































