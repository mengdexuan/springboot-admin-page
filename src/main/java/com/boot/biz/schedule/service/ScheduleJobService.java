package com.boot.biz.schedule.service;


import com.boot.base.BaseService;
import com.boot.biz.schedule.entity.ScheduleJob;

import java.util.List;


public interface ScheduleJobService extends BaseService<ScheduleJob> {

    /**
     * @param type spring管理的1个Bean，只能有1个方法上存在QuartzJob注解
     * @param <T>
     * @return 返回 ScheduleJob 对象
     */
    <T> ScheduleJob getJob(Class<T> type);

    /**
     *
     * @param type spring管理的1个Bean，只能有1个方法上存在QuartzJob注解
     * @param params 任务参数（QuartzJob注解标识的方法的参数，如果没有参数，该字段为空）
     * @param <T>
     */
    <T> void createJob(Class<T> type,String params);

    /**
     * 创建 job：当数据库中存在 ScheduleJob（bean 和 method 与被创建的相同）时，
     * 不再进行创建，即该 job 只允许创建 1 次
     * 参看：createJob(Class<T> type,String params)
     * @param type
     * @param params
     * @param <T>
     */
    <T> void createJobOnce(Class<T> type,String params);

    /**
     * @param type spring管理的1个Bean，只能有1个方法上存在QuartzJob注解
     * @param params 任务参数（QuartzJob注解标识的方法的参数，如果没有参数，该字段为空）
     * @param remark 任务备注，允许为空
     * @param <T>
     */
    <T> void createJob(Class<T> type,String params,String remark);

    /**
     * 创建 job：当数据库中存在 ScheduleJob（bean 和 method 与被创建的相同）时，
     * 不再进行创建，即该 job 只允许创建 1 次
     *
     * 参看：createJob(Class<T> type,String params,String remark)
     * @param type
     * @param params
     * @param remark
     * @param <T>
     */
    <T> void createJobOnce(Class<T> type,String params,String remark);

    void createJob(ScheduleJob scheduleJob);

    /**
     * 创建 job：当数据库中存在 ScheduleJob（bean 和 method 与被创建的相同）时，
     * 不再进行创建，即该 job 只允许创建 1 次
     * @param scheduleJob
     */
    void createJobOnce(ScheduleJob scheduleJob);

    /**
     * 手动触发执行1次任务
     * @param id 任务ID
     */
    void manual(Long id);

    void updateJob(ScheduleJob scheduleJob);

    void deleteJob(Long id);

    void deleteJobs(List<Long> ids);

    void run(Long[] ids);

    void pause(Long[] ids);

    void resume(Long[] ids);

    @Override
    List<ScheduleJob> list(ScheduleJob scheduleJob);

    @Override
    ScheduleJob one(ScheduleJob scheduleJob);

    /**
     * 获取正在执行的任务ID列表
     * @return
     */
    List<Long> runningJobId();
}
