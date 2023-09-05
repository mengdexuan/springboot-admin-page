package com.boot.config;

import com.boot.biz.webshell.ssh.SshShellHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
* @Description: websocket配置
* @Author: NoCortY
* @Date: 2020/3/8
*/
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
    @Autowired
    SshShellHandler sshShellHandler;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //socket通道

        //注意，ws 连接需要使用小写 , /webLog 前端无法连接，而 /weblog 连接正常
        webSocketHandlerRegistry.addHandler(sshShellHandler, "/webssh")
                .setAllowedOrigins("*");
    }
}
