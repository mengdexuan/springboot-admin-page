package com.boot.config;

import com.boot.biz.webshell.ssh.SshShellHandler;
import com.boot.biz.wstest.EchoHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author mengdexuan on 2020/4/1 13:21.
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer ,WebSocketConfigurer {

	public static String sysTime = "/sysTime";
	public static String sysInfoLog = "/sysInfoLog";
	public static String sysErrorLog = "/sysErrorLog";

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 注册一个端点，前端通过这个端点进行连接
		registry.addEndpoint(sysTime)
				//解决跨域问题
				.setAllowedOrigins("*")
				.withSockJS();

		registry.addEndpoint(sysInfoLog)
				//解决跨域问题
				.setAllowedOrigins("*")
				.withSockJS();

		registry.addEndpoint(sysErrorLog)
				//解决跨域问题
				.setAllowedOrigins("*")
				.withSockJS();

	}


	@Autowired
	EchoHandler echoHandler;
	@Autowired
	SshShellHandler sshShellHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

		// withSockJS 声明启用支持 sockJS
		webSocketHandlerRegistry.addHandler(echoHandler, "/echo").withSockJS();

		webSocketHandlerRegistry.addHandler(sshShellHandler, "/sshShellHandler").setAllowedOrigins("*").withSockJS();
	}


	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

}
