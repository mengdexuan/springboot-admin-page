package com.boot.biz.ws;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author mengdexuan on 2022/3/29 18:57.
 */
@Slf4j
@Component
@ServerEndpoint("/back/ws/time")
public class SysTimeHandle {

    Map<String,Thread> sessionMap = Maps.newConcurrentMap();

    /**
     * 新的WebSocket请求开启
     */
    @OnOpen
    public void onOpen(Session session) {
        log.info("打开 time session:{}",session.getId());
        try {
            Thread thread = ThreadUtil.newThread(()->{
                while (true){
                    if (session.isOpen()){
                        try {
                            session.getBasicRemote().sendText(DateUtil.now());
                        } catch (IOException e) {
                            log.error("send time fail!",e);
                        }
                    }else {
                        break;
                    }
                    ThreadUtil.safeSleep(1000);
                }
            },"time-thread");

            thread.start();

            sessionMap.put(session.getId(),thread);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * WebSocket请求关闭
     */
    @OnClose
    public void onClose(Session session) {
        log.info("关闭 time session:{}",session.getId());
        sessionMap.remove(session.getId());
    }

    @OnError
    public void onError(Throwable thr) {
        log.error("ws error:",thr);
    }


}
