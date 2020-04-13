package com.boot.biz.log;

import com.boot.config.WebSocketConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author mengdexuan on 2020/4/9 17:38.
 */
@Slf4j
@Component
public class SysLogMessaging {

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


	public void initLog() {
		tailInfoLog();
		tailErrorLog();
	}

	Process infoLogProcess = null;
	InputStream infoLogInputStream = null;
	Process errorLogProcess = null;
	InputStream errorLogInputStream = null;

	//监控输出 info 日志
	private void tailInfoLog() {
		try {

			if (infoLogProcess!=null){
				infoLogProcess.destroyForcibly();
				infoLogProcess = null;
			}
			if (infoLogInputStream!=null){
				infoLogInputStream.close();
				infoLogInputStream = null;
			}

			File file = new File(infoLog);

			// 执行tail -f命令
			infoLogProcess = Runtime.getRuntime().exec("tail -f " + file.getAbsolutePath());

			infoLogInputStream = infoLogProcess.getInputStream();

			// 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
			TailLogThread thread = new TailLogThread(WebSocketConfig.sysInfoLog,infoLogInputStream, simpMessagingTemplate);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//监控输出 error 日志
	private void tailErrorLog() {
		try {

			if (errorLogProcess!=null){
				errorLogProcess.destroyForcibly();
				errorLogProcess = null;
			}
			if (errorLogInputStream!=null){
				errorLogInputStream.close();
				errorLogInputStream = null;
			}

			File file = new File(errorLog);

			// 执行tail -f命令
			errorLogProcess = Runtime.getRuntime().exec("tail -f " + file.getAbsolutePath());

			errorLogInputStream = errorLogProcess.getInputStream();

			// 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
			TailLogThread thread = new TailLogThread(WebSocketConfig.sysErrorLog,errorLogInputStream, simpMessagingTemplate);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}





















}
