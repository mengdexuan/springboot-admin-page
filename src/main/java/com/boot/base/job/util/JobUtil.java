package com.boot.base.job.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.boot.base.job.entity.Job;
import com.boot.base.job.runner.JobRunnable;
import com.boot.base.job.service.JobService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author mengdexuan on 2020/3/15 17:50.
 */
@Slf4j
@Component
public class JobUtil {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    JobService jobService;

    private TaskScheduler taskScheduler = null;

    private Map<Long, ScheduledFuture> cache = Maps.newConcurrentMap();


    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * 执行 cron 调度
     *
     * @param jobId
     * @param runnable
     * @param cron
     */
    public void scheduleCronTask(Long jobId, Runnable runnable, String cron) {

        ScheduledFuture future = cache.get(jobId);

        if (future != null) {
            return;
        }

        CronTrigger cronTrigger = new CronTrigger(cron);
        if (taskScheduler != null) {
            ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(runnable, cronTrigger);
            cache.put(jobId, scheduledFuture);
        }
    }


    public void scheduleCronTask(Job job) {
        JobRunnable runnable = null;
        try {
            runnable = new JobRunnable(applicationContext.getBean(job.getBean()),
                    job.getMethod(), job.getParams(), job.getId(), jobService);
        } catch (NoSuchMethodException e) {
            log.error("添加调度任务失败！",e);
        }

        scheduleCronTask(job.getId(), runnable, job.getCron());
    }


    /**
     * 取消 cron 调度
     */
    public void cancelCronTask(Long id) {
        ScheduledFuture future = cache.get(id);
        if (future != null) {
            future.cancel(true);
            cache.remove(id);
        }
    }


    /**
     * 以当前时间为依据，判断给定的 date 是否过了 seconds 秒，如果过了 返回 true，否则返回 false
     * @param date
     * @param seconds
     */
    public static boolean isPassSeconds(Date date, int seconds){

        Date now = new Date();
        DateTime date2 = DateUtil.offsetSecond(date, seconds);

//        比较结果，如果date1 &lt; date2，返回数小于0，date1==date2返回0，date1 &gt; date2 大于0
        int result = DateUtil.compare(now,date2);
        if (result>0){
            //当前时间大
            return true;
        }

        return false;
    }


    /**
     * 获得指定范围内的随机的秒级别的cron表达式，
     * @param min 最小数（包含） >0
     * @param max 最大数（不包含）<60
     * @return
     */
    public static String getSecondCron(int min, int max) {
        String cron = "0/{second} * * * * *";
        Map<String,Integer> map = Maps.newHashMap();
        map.put("second",RandomUtil.randomInt(min,max));
        cron = StrUtil.format(cron,map);
        return cron;
    }


    /**
     * 获得指定范围内的随机的分钟级别的cron表达式，
     * @param min 最小数（包含） >0
     * @param max 最大数（不包含）<60
     * @return
     */
    public static String getMinuteCron(int min, int max) {
        String cron = "0 */{minute} * * * ?";
        Map<String,Integer> map = Maps.newHashMap();
        map.put("minute",RandomUtil.randomInt(min,max));
        cron = StrUtil.format(cron,map);
        return cron;
    }



}
