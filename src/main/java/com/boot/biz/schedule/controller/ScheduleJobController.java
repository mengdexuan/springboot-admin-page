package com.boot.biz.schedule.controller;

import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.base.exception.GlobalServiceException;
import com.boot.base.util.CronCheckUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.schedule.entity.ScheduleJob;
import com.boot.biz.schedule.service.ScheduleJobService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/schedule/job")
public class ScheduleJobController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    /**
     * 获取定时任务
     */
    @GetMapping(value = "/get/{id}")
    public Result<ScheduleJob> get(@PathVariable("id") Long id) {
        ScheduleJob scheduleJob = this.scheduleJobService.get(id);
        return ResultUtil.buildSuccess(scheduleJob);
    }

    @GetMapping(value = "/getByJobId/{jobId}")
    public Result<String> getByJobId(@PathVariable("jobId") Long jobId) {

        String result = "";

        ScheduleJob one = scheduleJobService.get(jobId);

        if (HelpMe.isNotNull(one.getLog())){
            result = one.getLog();
        }

        return ResultUtil.buildSuccess("success",result);
    }

    /**
     * 新增定时任务
     */
    @PostMapping(value = "/create")
    public Result<ScheduleJob> create(ScheduleJob scheduleJob) {
        this.scheduleJobService.createJob(scheduleJob);
        return ResultUtil.buildSuccess(scheduleJob);
    }

    /**
     * 手动触发执行1次任务
     */
    @PostMapping(value = "/manual")
    public Result<String> manual(Long id) {

        scheduleJobService.manual(id);

        return ResultUtil.buildSuccess();
    }


    @PostMapping(value = "/update")
    public Result<ScheduleJob> update(ScheduleJob scheduleJob) {

        boolean ok = CronCheckUtil.ok(scheduleJob.getExpression());

        if (!ok){
            throw new GlobalServiceException("Cron 表达式校验失败！");
        }

        ScheduleJob job = scheduleJobService.get(scheduleJob.getId());

        job.setRemark(scheduleJob.getRemark());
        job.setExpression(scheduleJob.getExpression());
        job.setName(scheduleJob.getName());

        this.scheduleJobService.updateJob(job);
        return ResultUtil.buildSuccess(scheduleJob);
    }

    /**
     * 更新任务状态
     * @param id
     * @return
     */
    @PostMapping(value = "/updateStatus")
    public Result<String> updateStatus(Long id) {

        ScheduleJob job = scheduleJobService.get(id);
        Integer status = job.getStatus();
        if (status==0){
            job.setStatus(1);
        }else if (status==1){
            job.setStatus(0);
        }

        scheduleJobService.updateJob(job);

        return ResultUtil.buildSuccess();
    }

    /**
     * 删除定时任务
     */
    @PostMapping(value = "/delete/{id}")
    public Result<ScheduleJob> delete(@PathVariable("id") Long id) {
        this.scheduleJobService.deleteJob(id);
        return ResultUtil.buildSuccess();
    }

    /**
     * 删除定时任务:批量
     */
    @PostMapping(value = "/delete")
    public Result<ScheduleJob> delete(Long[] ids) {
        this.scheduleJobService.deleteJobs(Lists.newArrayList(ids));
        return ResultUtil.buildSuccess();
    }

    /**
     * 立即执行任务
     */
    @PostMapping("/run")
    public Result run(Long[] ids) {
        this.scheduleJobService.run(ids);
        return ResultUtil.buildSuccess();
    }

    /**
     * 暂停定时任务
     */
    @PostMapping("/pause")
    public Result pause(Long[] ids) {
        this.scheduleJobService.pause(ids);
        return ResultUtil.buildSuccess();
    }

    /**
     * 恢复定时任务
     */
    @PostMapping("/resume")
    public Result resume(Long[] ids) {
        this.scheduleJobService.resume(ids);
        return ResultUtil.buildSuccess();
    }

}

