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
@ServerEndpoint(value="/tail/info")
@Slf4j
@Component
public class InfoLogWebSocketService {

	private Process process;
	private InputStream inputStream;

	@Value("${logging.info-log}")
	private String infoLogTemp;
	private static String infoLog = "log/info.log";

	@PostConstruct
	private void init(){
		infoLog = infoLogTemp;
		log.info("info日志路径：{}",infoLog);
	}




	/**
	 * 新的WebSocket请求开启
	 */
	@OnOpen
	public void onOpen(Session session) {
		try {

			if(process != null){
				process.destroy();
			}

			File file = new File(infoLog);

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
