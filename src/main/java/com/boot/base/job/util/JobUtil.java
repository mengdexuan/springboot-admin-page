package com.boot.base.job.util;

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


    public void scheduleCronTask(Job job, JobService jobService) throws Exception {
        JobRunnable runnable = new JobRunnable(applicationContext.getBean(job.getBean()),
                job.getMethod(), job.getParams(), job.getId(), jobService);

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


}
