package com.boot.config;

import com.boot.biz.webshell.ssh.SshShellHandler;
import com.boot.biz.ws.SysTimeHandler;
import com.boot.biz.wstest.EchoHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author mengdexuan on 2019/9/18 17:20.
 */
@Configuration
@EnableWebSocket
public class WsConfig implements WebSocketConfigurer {

	@Autowired
	EchoHandler echoHandler;
	@Autowired
	SshShellHandler sshShellHandler;
	@Autowired
	SysTimeHandler sysTimeHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

		// withSockJS 声明启用支持 sockJS
		webSocketHandlerRegistry.addHandler(sysTimeHandler, "/sysTime").withSockJS();
		webSocketHandlerRegistry.addHandler(echoHandler, "/echo").withSockJS();

		webSocketHandlerRegistry.addHandler(sshShellHandler, "/sshShellHandler").setAllowedOrigins("*").withSockJS();
	}



}
