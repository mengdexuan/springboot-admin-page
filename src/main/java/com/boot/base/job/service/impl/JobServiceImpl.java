package com.boot.base.job.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.boot.base.BaseServiceImpl;
import com.boot.base.job.annotation.JobCron;
import com.boot.base.job.entity.Job;
import com.boot.base.job.repository.JobRepository;
import com.boot.base.job.runner.JobRunnable;
import com.boot.base.job.service.JobService;
import com.boot.base.job.util.JobUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 * spring定时任务 服务实现类
 * </p>
 *
 * @author code-make-tool
 * @since 2020-03-15
 */
@Slf4j
@Service
public class JobServiceImpl extends BaseServiceImpl<Job, JobRepository> implements JobService {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    JobUtil jobUtil;
    /***
     * 添加 cron 任务，相同的任务（指的是2条任务的：bean名称和method名称相同）可以重复添加
     * @param job
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addJob(Job job) {
        this.save(job);
        if (job.getStatus().intValue() == Job.Status.PAUSE.getValue()) {

        } else {
            //添加到 job 调度
            try {
                jobUtil.scheduleCronTask(job,this);
            } catch (Exception e) {
                log.error("添加到调度任务失败！", e);
            }
        }
    }


    /**
     * 添加 cron 任务，相同的任务（指的是2条任务的：bean名称和method名称相同）只可以添加1条记录，不会重复添加
     *
     * @param job
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addJobOnce(Job job) {
        Job temp = new Job();

        temp.setBean(job.getBean());
        temp.setMethod(job.getMethod());

        Job entity = this.one(temp);

        if (entity == null) {
            addJob(job);
        }
    }


    /**
     * 删除 cron 任务
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delJob(Long id) {
        jobUtil.cancelCronTask(id);
        this.delete(id);
    }


    /**
     * 暂停 cron 任务
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void parseJob(Long id) {
        Job job = this.get(id);
        job.setStatus(Job.Status.PAUSE.getValue());

        jobUtil.cancelCronTask(id);

        this.save(job);
    }

    /**
     * 重启 cron 任务
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restartJob(Long id) throws Exception{

        Job job = this.get(id);
        job.setStatus(Job.Status.RUN.getValue());

        jobUtil.scheduleCronTask(job,this);

        this.save(job);
    }


    /**
     * 修改任务的 cron 表达式
     *
     * @param id
     * @param newCron
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeJobCron(Long id, String newCron) throws Exception {
        Job job = this.get( id);
		job.setCron(newCron);

        //先取消任务
        jobUtil.cancelCronTask(id);

        if (Job.Status.RUN.getValue() == job.getStatus().intValue()) {
            //再添加任务
            jobUtil.scheduleCronTask(job,this);
        }
        this.save(job);
    }


    /**
     * 手动触发一次任务
     *
     * @param id
     */
    @Override
    public void manualTrigger(Long id) throws Exception {

        Job job = this.get(id);

        JobRunnable runnable = new JobRunnable(applicationContext.getBean(job.getBean()), job.getMethod(), job.getParams(), job.getId(),this);

        runnable.setManualTrigger(true);

        ExecutorService executorService = ThreadUtil.newExecutor(1);

        executorService.execute(runnable);
    }


    /**
     * 解析1个Bean，提取并转换由 CronTask 注解标识的方法，生成 SpringTask
     *
     * @param type spring管理的1个Bean
     * @param <T>
     * @return 返回 List<SpringTask> 任务列表（1个Bean中，可以有多个 CronTask 标注的方法）
     */
    @Override
    public <T> List<Job> getJob(Class<T> type) {

        //获取 spring bean 名称，默认类名首字母小写
        String beanName = StrUtil.lowerFirst(type.getSimpleName());

        Service service = type.getAnnotation(Service.class);
        if (service != null && StrUtil.isNotEmpty(service.value())) {
            beanName = service.value();
        }

        Component component = type.getAnnotation(Component.class);
        if (component != null && StrUtil.isNotEmpty(component.value())) {
            beanName = component.value();
        }

        log.info("{} springBean名称：{}", type.getName(), beanName);

        Method[] methods = ReflectUtil.getMethods(type);

        int count = 0;
        for (Method method : methods) {
			JobCron cronTask = method.getAnnotation(JobCron.class);
            if (cronTask != null) {
                count++;
            }
        }
        if (count == 0) {
            throw new RuntimeException(beanName + " 中不存在 CronTask 标注的方法！");
        }

        List<Job> springTaskList = Lists.newArrayList();

        Date now = new Date();

        for (Method method : methods) {
			JobCron cronTask = method.getAnnotation(JobCron.class);
            if (cronTask != null) {

                Job job = new Job();

                job.setBean(beanName);
                job.setMethod(method.getName());
                job.setCron(cronTask.cron());
                job.setName(cronTask.name());
                job.setRemark(cronTask.remark());

                if (cronTask.delWhenSuccess()) {
                    job.setDelWhenSuccess(Job.DelWhenSuccess.YES.getValue());
                } else {
                    job.setDelWhenSuccess(Job.DelWhenSuccess.NO.getValue());
                }

                job.setStatus(Job.Status.RUN.getValue());
                job.setCreateTime(now);

                springTaskList.add(job);
            }
        }

        return springTaskList;
    }

    /**
     * 获取任务，调用的是 getTask方法，获取第 1 个值
     *
     * @param type
     * @return
     */
    @Override
    public <T> Job getSingleJob(Class<T> type) {
        return getJob(type).get(0);
    }
}
