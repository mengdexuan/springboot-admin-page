package com.boot.biz.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author mengdexuan on 2018/8/30 17:49.
 */
@ServerEndpoint(value="/tail/error")
@Slf4j
@Component
public class ErrorLogWebSocketService {

	private Process process;
	private InputStream inputStream;


	@Value("${logging.error-log}")
	private String errorLogTemp;
	private static String errorLog = "log/error.log";

	@PostConstruct
	private void init(){
		errorLog = errorLogTemp;
		log.info("error日志路径：{}",errorLog);
	}




	/**
	 * 新的WebSocket请求开启
	 */
	@OnOpen
	public void onOpen(Session session) {
		try {
			File file = new File(errorLog);

			if(process != null){
				process.destroy();
			}

			// 执行tail -f命令
			process = Runtime.getRuntime().exec("tail -f "+file.getAbsolutePath());

			inputStream = process.getInputStream();

			// 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
			TailLogThread thread = new TailLogThread(inputStream, session);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message 客户端发送过来的消息*/
	@OnMessage
	public void onMessage(String message, Session session) {
		log.info("来自客户端的消息:" + message);
	}

	/**
	 * WebSocket请求关闭
	 */
	@OnClose
	public void onClose() {
		try {
			if(inputStream != null){
				inputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(process != null){
			process.destroy();
		}
	}

	@OnError
	public void onError(Throwable thr) {
		thr.printStackTrace();
	}
}
