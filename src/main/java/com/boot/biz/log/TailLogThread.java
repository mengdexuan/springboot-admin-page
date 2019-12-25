package com.boot.biz.log;

import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author mengdexuan on 2018/8/30 17:50.
 */
public class TailLogThread extends Thread{
	private BufferedReader reader;
	private Session session;

	public TailLogThread(InputStream in, Session session) {
		this.reader = new BufferedReader(new InputStreamReader(in));
		this.session = session;

	}

	@Override
	public void run() {
		String line;
		try {
			while((line = reader.readLine()) != null) {
				// 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
				session.getBasicRemote().sendText(line + "<br>");
			}
		} catch (IOException e) {
		}
	}
}
