package com.boot.biz.ws;

import com.boot.base.util.SpringContextUtil;
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
@ServerEndpoint("/back/ws/log")
public class LogWebSocketHandle {

    Map<String,DataDto> sessionMap = Maps.newConcurrentMap();

    /**
     * 新的WebSocket请求开启
     */
    @OnOpen
    public void onOpen(Session session) {
        log.info("打开 session:{}",session.getId());
        try {
            String qs = session.getQueryString();
            log.info("ws 请求参数：{}",qs);
            String dataId = qs.split("=")[1];

            String logFile = "";

            if (dataId.equals("info")){
                logFile = LogPathBean.infoLog;
            }else if (dataId.equals("error")){
                logFile = LogPathBean.errorLog;
            }

            // 执行tail -f命令
            Process process = Runtime.getRuntime().exec("tail -f "+logFile);
            InputStream inputStream = process.getInputStream();

            DataDto dataDto = new DataDto();
            dataDto.setInputStream(inputStream);
            dataDto.setProcess(process);

            sessionMap.put(session.getId(),dataDto);

            // 一定要启动新的线程，防止InputStream阻塞处理WebSocket的线程
            TailLogThread thread = new TailLogThread(inputStream, session);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * WebSocket请求关闭
     */
    @OnClose
    public void onClose(Session session) {
        log.info("关闭 session:{}",session.getId());
        DataDto dto = sessionMap.remove(session.getId());
        try {
            InputStream inputStream = dto.getInputStream();
            if(inputStream != null)
                inputStream.close();
        } catch (Exception e) {
        }
        try{
            Process process = dto.getProcess();
            if(process != null)
                process.destroy();
        }catch (Exception e){
        }
    }

    @OnError
    public void onError(Throwable thr) {
        log.error("ws error:",thr);
    }


}
