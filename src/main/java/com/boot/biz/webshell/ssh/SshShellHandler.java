package com.boot.biz.webshell.ssh;

import cn.hutool.json.JSONUtil;
import com.boot.base.util.ConcurrentLRUCache;
import com.boot.biz.webshell.entity.SshServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class SshShellHandler extends TextWebSocketHandler{

	private static ConcurrentLRUCache<String, SshClient> sessionMap = new ConcurrentLRUCache<>(20);

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		super.handleTextMessage(session, message);

		String payload = message.getPayload();
//		log.info("receive socket msg:{}",payload);

		SshServerInfo sshServerInfo = null;
		boolean isFirstConn = false;

		try {
			sshServerInfo = JSONUtil.toBean(payload,SshServerInfo.class);
			isFirstConn = true;
		} catch (Exception e) {
		}


		if (isFirstConn){
			//初始化客户端
			SshClient client = sshConnect(session, sshServerInfo);

			sessionMap.put(session.getId(),client);

			session.sendMessage(new TextMessage("firstConn"));
		}else {

			SshClient sshClient = sessionMap.get(session.getId());

			//处理连接
			try {
				//当客户端不为空的情况
				if (sshClient != null) {

					//receive a close cmd ?
					if (Arrays.equals("exit".getBytes(), message.asBytes())) {

						if (sshClient != null) {
							sshClient.disconnect();
						}

						session.close();
						return ;
					}
					//写入前台传递过来的命令，发送到目标服务器上
					sshClient.write(new String(message.asBytes(), "UTF-8"));
				}
			} catch (Exception e) {
				log.error("",e);
				session.sendMessage(new TextMessage("An error occured, websocket is closed."));
				session.close();
			}
		}


	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);

		log.info("关闭 webSocket 连接:{}",session.getId());

		SshClient client = sessionMap.remove(session.getId());

		//关闭连接
		if (client != null) {
			client.disconnect();
			client = null;
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
	}

	/**
	 * 连接服务端
	 * @param session
	 * @param machine
	 * @param spkey
	 */
	private SshClient sshConnect(WebSocketSession session, SshServerInfo sshServerInfo) {

		SshClient sshClient = new SshClient();

		try {
			session.sendMessage(new TextMessage("Try to connect...\r"));

			//连接服务器
			if (sshClient.connect(sshServerInfo)) {
				//开启线程，用于写数据到服务器上的
				sshClient.startShellOutPutTask(session);
			}else {
				//关闭连接
				if (sshClient != null) {
					sshClient.disconnect();
					sshClient = null;
				}
				session.sendMessage(new TextMessage("Connect failed, please confirm the username or password try agin."));
				session.close();
			}
		} catch (IOException e) {
			log.error("",e);
		}

		return sshClient;
	}
}
