package com.boot.biz.schedule.util;

import com.boot.biz.schedule.entity.ScheduleJob;
import com.boot.biz.schedule.jobbean.AllowConcurrentJobBean;
import com.boot.biz.schedule.jobbean.DisAllowConcurrentJobBean;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.List;

@Slf4j
public class ScheduleUtils {

    public final static String JOB_NAME = "TASK_";

    /**
     * 获取触发器key
     */
    public static TriggerKey getTriggerKey(Long jobId) {
        return TriggerKey.triggerKey(JOB_NAME + jobId);
    }

    /**
     * 获取jobKey
     */
    public static JobKey getJobKey(Long jobId) {
        return JobKey.jobKey(JOB_NAME + jobId);
    }

    /**
     * 获取表达式触发器
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, Long jobId) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
        } catch (SchedulerException e) {
            throw new RuntimeException("获取定时任务CronTrigger出现异常", e);
        }
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) {
        try {
            JobDetail jobDetail = null;
            if (ScheduleJob.AllowConcurrent.YES.getValue()==scheduleJob.getAllowConcurrent()){
                //构建job信息
                jobDetail = JobBuilder.newJob(AllowConcurrentJobBean.class).withIdentity(getJobKey(scheduleJob.getId())).build();
            }else {
                //构建job信息
                jobDetail = JobBuilder.newJob(DisAllowConcurrentJobBean.class).withIdentity(getJobKey(scheduleJob.getId())).build();
            }

            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getExpression());

            //配置 Misfire 策略
            scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();

            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(scheduleJob.getId())).withSchedule(scheduleBuilder).build();

            //放入参数，运行时的方法可以获取
            jobDetail.getJobDataMap().put(ScheduleJob.JOB_PARAM_KEY, scheduleJob);

            scheduler.scheduleJob(jobDetail, trigger);

            //暂停任务
            if (scheduleJob.getStatus() == ScheduleJob.Status.PAUSE.getValue()) {
                pauseJob(scheduler, scheduleJob.getId());
            }else {
                run(scheduler,scheduleJob);
            }
        } catch (SchedulerException e) {
            throw new RuntimeException("创建定时任务失败", e);
        }
    }

    /**
     * 更新定时任务
     */
    public static void updateScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) {
        try {
            TriggerKey triggerKey = getTriggerKey(scheduleJob.getId());

            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getExpression());

            //配置 Misfire 策略
            scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();

            CronTrigger trigger = getCronTrigger(scheduler, scheduleJob.getId());

            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            //参数
            trigger.getJobDataMap().put(ScheduleJob.JOB_PARAM_KEY, scheduleJob);

            scheduler.rescheduleJob(triggerKey, trigger);

            //暂停任务
            if (scheduleJob.getStatus() == ScheduleJob.Status.PAUSE.getValue()) {
                pauseJob(scheduler, scheduleJob.getId());
            }

        } catch (SchedulerException e) {
            throw new RuntimeException("更新定时任务失败", e);
        }
    }

    /**
     * 立即执行任务
     */
    public static void run(Scheduler scheduler, ScheduleJob scheduleJob) {

        String jobKey = getJobKey(scheduleJob.getId()).getName();
        try {
            List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
            for(JobExecutionContext context : jobContexts) {
                if (jobKey.equals(context.getTrigger().getJobKey().getName())) {
                    if (scheduleJob.getAllowConcurrent()==ScheduleJob.AllowConcurrent.NO.getValue()){
                        log.warn("{}正在执行，因该任务被设置为不允许并发执行，所以此次手动执行无效！",jobKey);
                        throw new RuntimeException("该任务正在执行，因该任务被设置为不允许并发执行，所以此次手动执行无效！");
                    }
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        try {
            //参数
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(ScheduleJob.JOB_PARAM_KEY, scheduleJob);
            scheduler.triggerJob(getJobKey(scheduleJob.getId()), dataMap);
        } catch (SchedulerException e) {
            throw new RuntimeException("立即执行定时任务失败", e);
        }
    }

    /**
     * 暂停任务
     */
    public static void pauseJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.pauseJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new RuntimeException("暂停定时任务失败", e);
        }
    }

    /**
     * 恢复任务
     */
    public static void resumeJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.resumeJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new RuntimeException("暂停定时任务失败", e);
        }
    }

    /**
     * 删除定时任务
     */
    public static void deleteScheduleJob(Scheduler scheduler, Long jobId) {
        try {
            scheduler.deleteJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new RuntimeException("删除定时任务失败", e);
        }
    }

}
