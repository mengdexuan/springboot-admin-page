package com.boot.biz.log;

import com.boot.config.WebSocketConfig;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import sun.nio.cs.ext.MS874;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author mengdexuan on 2018/8/30 17:50.
 */
public class TailLogThread extends Thread{
	private String url;
	private BufferedReader reader;
	private SimpMessagingTemplate simpMessagingTemplate;

	public TailLogThread(String url,InputStream in, SimpMessagingTemplate simpMessagingTemplate) {
		this.url = url;
		this.reader = new BufferedReader(new InputStreamReader(in));
		this.simpMessagingTemplate = simpMessagingTemplate;

	}

	@Override
	public void run() {
		String line;
		try {
			while((line = reader.readLine()) != null) {
				// 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
				String message = line + "<br>";
				simpMessagingTemplate.convertAndSend(url, message);
			}
		} catch (IOException e) {
		}
	}
}
