package com.boot.biz.ws;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.boot.base.util.HelpMe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class SysTimeHandler extends AbstractWebSocketHandler {

    ExecutorService singleExe = ThreadUtil.newSingleExecutor();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        singleExe.execute(()->{
            while (true){

                if (session.isOpen()){
                    try {
                        session.sendMessage(new TextMessage(sysTime()));
                    } catch (IOException e) {
                        break;
                    }
                }else {
                    break;
                }

                ThreadUtil.safeSleep(1000);
            }
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("a text msg received: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }


    //获取当前时间
    private static String sysTime(){

        LocalDateTime localDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");

        String result = localDateTime.format(formatter);


        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();

        result += " ";
        result += trans(dayOfWeek.toString());

        return result;
    }

    private static String trans(String week){

        String result = "";

        switch (week){
            case "SUNDAY":
                result = "周日";
                break;
            case "MONDAY":
                result = "周一";
                break;
            case "TUESDAY":
                result = "周二";
                break;
            case "WEDNESDAY":
                result = "周三";
                break;
            case "THURSDAY":
                result = "周四";
                break;
            case "FRIDAY":
                result = "周五";
                break;
            case "SATURDAY":
                result = "周六";
                break;
            default:
                break;
        }

        return result;
    }

}
