package com.boot.biz.schedule.service.impl;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.boot.base.BaseServiceImpl;
import com.boot.base.exception.GlobalServiceException;
import com.boot.base.util.HelpMe;
import com.boot.biz.schedule.annotation.QuartzJob;
import com.boot.biz.schedule.entity.ScheduleJob;
import com.boot.biz.schedule.repository.ScheduleJobRepository;
import com.boot.biz.schedule.service.ScheduleJobService;
import com.boot.biz.schedule.util.ScheduleRunnable;
import com.boot.biz.schedule.util.ScheduleUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScheduleJobServiceImpl extends BaseServiceImpl<ScheduleJob, ScheduleJobRepository> implements ScheduleJobService {

    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

    @Qualifier("schedulerFactoryBean")
    @Autowired
    private Scheduler scheduler;

    @Autowired
	ScheduleJobRepository scheduleJobRepository;


    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() {
        List<ScheduleJob> scheduleJobList = this.findAll();
        for (ScheduleJob scheduleJob : scheduleJobList) {
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getId());
            // 如果不存在，则创建
            if (cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }


    /**
     * @param type spring管理的1个Bean，只能有1个方法上存在QuartzJob注解
     * @param <T>
     * @return 返回 ScheduleJob 对象
     */
    @Override
    public <T> ScheduleJob getJob(Class<T> type) {

        ScheduleJob scheduleJob = new ScheduleJob();

        //获取 spring bean 名称，默认类名首字母小写
        String beanName = StrUtil.lowerFirst(type.getSimpleName());

        Service service = type.getAnnotation(Service.class);
        if (service!=null && HelpMe.isNotNull(service.value())){
            beanName = service.value();
        }

        Component component = type.getAnnotation(Component.class);
        if (component!=null && HelpMe.isNotNull(component.value())){
            beanName = component.value();
        }

        log.info("{} springBean名称：{}",type.getName(),beanName);

        Method[] methods = ReflectUtil.getMethods(type);

        int count = 0 ;
        for (Method method:methods){
            QuartzJob quartzJob = method.getAnnotation(QuartzJob.class);
            if (quartzJob!=null){
                count++;
            }
        }
        if (count==0){
            throw new RuntimeException("此Bean中不存在QuartzJob标注的方法！");
        }
        if (count>1){
            throw new RuntimeException("1个Bean中，只允许存在1个QuartzJob标注的方法！");
        }

        for (Method method:methods){
            QuartzJob quartzJob = method.getAnnotation(QuartzJob.class);
            if (quartzJob!=null){

                scheduleJob.setBean(beanName);
                scheduleJob.setMethod(method.getName());
                scheduleJob.setExpression(quartzJob.cron());
                scheduleJob.setName(quartzJob.jobName());

                if (quartzJob.delOnSuccess()){
                    scheduleJob.setDelOnSuccess(ScheduleJob.DelOnSuccess.YES.getValue());
                }else {
                    scheduleJob.setDelOnSuccess(ScheduleJob.DelOnSuccess.NO.getValue());
                }
                if (quartzJob.allowConcurrent()){
                    scheduleJob.setAllowConcurrent(ScheduleJob.AllowConcurrent.YES.getValue());
                }else {
                    scheduleJob.setAllowConcurrent(ScheduleJob.AllowConcurrent.NO.getValue());
                }

                break;
            }
        }

        return scheduleJob;
    }


    /**
     * 创建 job：当数据库中存在 ScheduleJob（bean 和 method 与被创建的相同）时，
     * 不再进行创建，即该 job 只允许创建 1 次
     * 参看：createJob(Class<T> type,String params)
     *
     * @param type
     * @param params
     */
    @Override
    public <T> void createJobOnce(Class<T> type, String params) {
        ScheduleJob job = this.getJob(type);
        if (HelpMe.isNotNull(params)){
            job.setParams(params);
        }
        this.createJobOnce(job);
    }

    /**
     * 创建 job：当数据库中存在 ScheduleJob（bean 和 method 与被创建的相同）时，
     * 不再进行创建，即该 job 只允许创建 1 次
     * <p>
     * 参看：createJob(Class<T> type,String params,String remark)
     *
     * @param type
     * @param params
     * @param remark
     */
    @Override
    public <T> void createJobOnce(Class<T> type, String params, String remark) {

        ScheduleJob job = this.getJob(type);
        if (HelpMe.isNotNull(params)){
            job.setParams(params);
        }
        job.setRemark(remark);
        this.createJobOnce(job);
    }

    /**
     * 创建 job：当数据库中存在 ScheduleJob（bean 和 method 与被创建的相同）时，
     * 不再进行创建，即该 job 只允许创建 1 次
     *
     * @param scheduleJob
     */
    @Override
    public void createJobOnce(ScheduleJob scheduleJob) {
        ScheduleJob job = new ScheduleJob();
        job.setBean(scheduleJob.getBean());
        job.setMethod(scheduleJob.getMethod());

        boolean exists = this.exists(job);

        if (exists){
//            log.error("该 job 已存在，不再进行创建，任务 bean:{},任务 method:{}",job.getBean(),job.getMethod());
        }else {
            this.createJob(scheduleJob);
        }
    }

    /**
     *
     * @param type spring管理的1个Bean，只能有1个方法上存在QuartzJob注解
     * @param params 任务参数（QuartzJob注解标识的方法的参数，如果没有参数，该字段为空）
     * @param <T>
     */
    @Override
    public <T> void createJob(Class<T> type,String params) {
        ScheduleJob job = this.getJob(type);
        if (HelpMe.isNotNull(params)){
            job.setParams(params);
        }
        this.createJob(job);
    }

    /**
     * @param type spring管理的1个Bean，只能有1个方法上存在QuartzJob注解
     * @param params 任务参数（QuartzJob注解标识的方法的参数，如果没有参数，该字段为空）
     * @param remark 任务备注，允许为空
     * @param <T>
     */
    @Override
    public <T> void createJob(Class<T> type,String params,String remark) {
        ScheduleJob job = this.getJob(type);
        if (HelpMe.isNotNull(params)){
            job.setParams(params);
        }
        job.setRemark(remark);
        this.createJob(job);
    }





    /**
     * 手动触发执行1次任务
     *
     * @param id 任务ID
     */
    @Override
    public void manual(Long id) {

        log.info("手动触发任务执行，任务ID:{}",id);

        ScheduleJob scheduleJob = get(id);
        if (scheduleJob==null){
            throw new GlobalServiceException("任务不存在！");
        }

        fixedThreadPool.execute(()->{
            long startTime = System.currentTimeMillis();
            try {
                ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getBean(),
                        scheduleJob.getMethod(), scheduleJob.getParams());
                Future<?> future = fixedThreadPool.submit(task);
                future.get();
                long times = System.currentTimeMillis() - startTime;

                log.info("手动触发任务执行完毕，任务ID：" + scheduleJob.getId() + "，  总共耗时：" + times + "毫秒");

            }catch (Exception e){
                log.error("手动触发任务执行失败！",e);
            }
        });

    }

    @Override
    @Transactional
    public void createJob(ScheduleJob scheduleJob) {
        if (scheduleJob.getStatus()==null){
            scheduleJob.setStatus(ScheduleJob.Status.NORMAL.getValue());
        }
        if (scheduleJob.getAllowDel()==null){
            scheduleJob.setAllowDel(ScheduleJob.AllowDel.DEFAULT.getValue());
        }
        if (scheduleJob.getAllowConcurrent()==null){
            scheduleJob.setAllowConcurrent(ScheduleJob.AllowConcurrent.DEFAULT.getValue());
        }
        if (scheduleJob.getDelOnSuccess()==null){
            scheduleJob.setDelOnSuccess(ScheduleJob.DelOnSuccess.DEFAULT.getValue());
        }
        scheduleJob.setCreateTime(new Date());
        this.save(scheduleJob);
        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
    }

    @Override
    @Transactional
    public void updateJob(ScheduleJob scheduleJob) {
        ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
        this.update(scheduleJob);
    }


    @Override
    @Transactional
    public void deleteJob(Long id) {
        ScheduleJob one = this.get(id);
        if (one.getAllowDel()!=null && one.getAllowDel()==ScheduleJob.AllowDel.NO.getValue()){
            throw new GlobalServiceException("["+one.getName()+"] 被设置为不允许删除！");
        }

        //如果任务是运行状态，删除之前先暂停任务
        if (one.getStatus() == ScheduleJob.Status.NORMAL.getValue()) {
            ScheduleUtils.pauseJob(scheduler, id);
        }

        ScheduleUtils.deleteScheduleJob(scheduler, id);
        this.delete(id);

    }

    @Override
    @Transactional
    public void deleteJobs(List<Long> ids) {
        for (Long id : ids) {
            this.deleteJob(id);
        }
    }


    @Override
    @Transactional
    public void run(Long[] ids) {
        for (Long id : ids) {
            ScheduleJob scheduleJob = this.get(id);
            if (scheduleJob != null) {
                ScheduleUtils.run(scheduler, scheduleJob);
            }
        }
    }

    @Override
    @Transactional
    public void pause(Long[] ids) {
        for (Long id : ids) {
            ScheduleJob scheduleJob = this.get(id);
            if (scheduleJob != null) {
                ScheduleUtils.pauseJob(scheduler, id);
                scheduleJob.setStatus(ScheduleJob.Status.PAUSE.getValue());
                this.update(scheduleJob);
            }
        }
    }

    @Override
    @Transactional
    public void resume(Long[] ids) {
        for (Long id : ids) {
            ScheduleJob scheduleJob = this.get(id);
            if (scheduleJob != null) {
                ScheduleUtils.resumeJob(scheduler, id);
                scheduleJob.setStatus(ScheduleJob.Status.NORMAL.getValue());
                this.update(scheduleJob);
            }
        }
    }

    @Transactional
    public void updateBatch(Long[] ids, int status) {

        for (Long id : ids) {
            ScheduleJob scheduleJob = this.get(id);
            scheduleJob.setStatus(status);
            this.update(scheduleJob);
        }
    }

    @Override
    public List<ScheduleJob> list(ScheduleJob scheduleJob) {
        Example<ScheduleJob> example = Example.of(scheduleJob);
        return scheduleJobRepository.findAll(example);
    }

    @Override
    public ScheduleJob one(ScheduleJob scheduleJob) {
        List<ScheduleJob> list = list(scheduleJob);
        if (HelpMe.isNotNull(list)){
            return list.get(list.size()-1);
        }
        return null;
    }

    /**
     * 获取正在执行的任务ID列表
     *
     * @return
     */
    @Override
    public List<Long> runningJobId() {

        List<String> jobNameList = Lists.newArrayList();
        try {
            List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
            for(JobExecutionContext context : jobContexts) {
                String name = context.getTrigger().getJobKey().getName();
                jobNameList.add(name);
            }
            List<Long> result = jobNameList.stream().map(name -> {
                return Long.parseLong(StrUtil.removePrefix(name,ScheduleUtils.JOB_NAME));
            }).collect(Collectors.toList());

            return result;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


}
