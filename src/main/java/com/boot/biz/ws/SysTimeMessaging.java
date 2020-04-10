package com.boot.biz.ws;

import com.boot.config.WebSocketConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class SysTimeMessaging {


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    //每 1 秒发送 1 次
    @Scheduled(cron = "0/1 * * * * ?")
    public void send(){
        simpMessagingTemplate.convertAndSend(WebSocketConfig.sysTime, sysTime());
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
