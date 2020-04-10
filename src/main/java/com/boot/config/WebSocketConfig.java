package com.boot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author mengdexuan on 2020/4/1 13:21.
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

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



}
