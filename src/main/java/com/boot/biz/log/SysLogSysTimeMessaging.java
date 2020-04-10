package com.boot.biz.log;

import com.boot.config.WebSocketConfig;
import com.boot.config.WsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author mengdexuan on 2020/4/9 17:38.
 */
@Slf4j
@Component
public class SysLogSysTimeMessaging implements CommandLineRunner {

	@Value("${logging.error-log}")
	private String errorLogTemp;
	private static String errorLog = "log/error.log";

	@Value("${logging.info-log}")
	private String infoLogTemp;
	private static String infoLog = "log/info.log";

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;


	@PostConstruct
	private void init(){
		infoLog = infoLogTemp;
		log.info("info日志路径：{}",infoLog);

		errorLog = errorLogTemp;
		log.info("error日志路径：{}",errorLog);
	}


	@Override
	public void run(String... args) throws Exception {
		tailInfoLog();
		tailErrorLog();
	}



	//监控输出 info 日志
	private void tailInfoLog() {
		try {
			File file = new File(infoLog);

			// 执行tail -f命令
			Process process = Runtime.getRuntime().exec("tail -f " + file.getAbsolutePath());

			InputStream inputStream = process.getInputStream();

			// 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
			TailLogThread thread = new TailLogThread(WebSocketConfig.sysInfoLog,inputStream, simpMessagingTemplate);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//监控输出 error 日志
	private void tailErrorLog() {
		try {
			File file = new File(errorLog);

			// 执行tail -f命令
			Process process = Runtime.getRuntime().exec("tail -f " + file.getAbsolutePath());

			InputStream inputStream = process.getInputStream();

			// 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
			TailLogThread thread = new TailLogThread(WebSocketConfig.sysErrorLog,inputStream, simpMessagingTemplate);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}





















}
