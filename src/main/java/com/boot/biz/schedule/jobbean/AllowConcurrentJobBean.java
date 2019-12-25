package com.boot.biz.schedule.jobbean;

import cn.hutool.json.JSONUtil;
import com.boot.base.util.SpringContextUtil;
import com.boot.biz.schedule.entity.ScheduleJob;
import com.boot.biz.schedule.service.ScheduleJobService;
import com.boot.biz.schedule.util.ScheduleRunnable;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * 并发定时任务
 */
@Slf4j
public class AllowConcurrentJobBean extends QuartzJobBean {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    ScheduleJobService scheduleJobService = SpringContextUtil.getBean(ScheduleJobService.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get(ScheduleJob.JOB_PARAM_KEY);

        //任务开始时间
        long startTime = System.currentTimeMillis();

        String errLog = "";

        try {
            //执行任务
//            log.info("任务准备执行，任务ID：" + scheduleJob.getId());
            ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getBean(), scheduleJob.getMethod(), scheduleJob.getParams());
            Future<?> future = executorService.submit(task);
            future.get();

            if (scheduleJob.getDelOnSuccess()==ScheduleJob.DelOnSuccess.YES.getValue()){
                scheduleJobService.deleteJob(scheduleJob.getId());
                log.info("任务执行成功，删除任务-->"+JSONUtil.toJsonStr(scheduleJob));
            }
        } catch (Exception e) {

            //任务执行总时长
            long times = System.currentTimeMillis() - startTime;

            log.error("任务执行失败，时间：{} ID：{}" ,times,scheduleJob.getId(), e);

            errLog = e.getMessage();

            scheduleJob.setLog(errLog);

            scheduleJobService.save(scheduleJob);
        }
    }
    
}
