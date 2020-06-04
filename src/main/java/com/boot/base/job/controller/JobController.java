package com.boot.base.job.controller;


import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.base.exception.GlobalServiceException;
import com.boot.base.job.entity.Job;
import com.boot.base.job.service.JobService;
import com.boot.base.util.CronCheckUtil;
import com.boot.base.util.HelpMe;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * spring定时任务 控制器
 * </p>
 *
 * @author code-make-tool
 * @since 2020-03-15
 */
@RestController
@RequestMapping("/job")
@Api(tags = "定时任务 接口")
public class JobController extends BaseController {

	@Autowired
	private JobService jobService;



	@GetMapping(value = "/list")
	@ApiOperation("获取定时任务列表")
	public Result list() {
		return this.success(jobService.findAll());
	}



	@GetMapping(value = "/getByJobId/{jobId}")
	@ApiOperation("获取定时任务错误信息")
	public Result<String> getByJobId(@PathVariable("jobId") Long jobId) {

		String result = "";

		Job one = jobService.get(jobId);

		if (HelpMe.isNotNull(one.getErrLog())){
			result = one.getErrLog();
		}

		return this.success(result);
	}

	/**
	 * 删除定时任务
	 */
	@DeleteMapping(value = "/delete/{id}")
	@ApiOperation("删除定时任务")
	public Result<String> delete(@PathVariable("id") Long id) {

		Job job = jobService.get(id);

		jobService.delJob(job.getId());

		return this.success();
	}

	/**
	 * 修改定时任务
	 */
	@ApiOperation("修改定时任务")
	@PostMapping(value = "/update")
	public Result<String> update(Job job) {

		Job task = jobService.get(job.getId());

		if (job.getCron().equals(task.getCron())){
			jobService.update(job);
		}else {
			//修改了 cron 表达式
			boolean ok = CronCheckUtil.ok(job.getCron());
			if (ok){
				try {
					jobService.update(job);
					jobService.changeJobCron(task.getId(),job.getCron());
				} catch (Exception e) {
				}
			}else {
				throw new GlobalServiceException("Cron 表达式校验失败！");
			}
		}

		return this.success();
	}




	/**
	 * 更新任务状态
	 * @param id
	 * @return
	 */
	@PostMapping(value = "/updateStatus")
	@ApiOperation("修改定时任务状态")
	public Result<String> updateStatus(Long id) {

		Job job = jobService.get(id);

		if (job.getStatus().intValue() == Job.Status.RUN.getValue()){
			jobService.parseJob(job.getId());
		}else {
			try {
				jobService.restartJob(job.getId());
			} catch (Exception e) {
			}
		}

		return this.success();
	}



	/**
	 * 手动触发定时任务
	 */
	@PostMapping(value = "/manual")
	@ApiOperation("手动触发定时任务")
	public Result<String> manual(Long id) {

		Job job = jobService.get(id);

		try {
			jobService.manualTrigger(job.getId());
		} catch (Exception e) {
		}

		return this.success();
	}



	/**
	 * 获取定时任务
	 */
	@GetMapping(value = "/get/{id}")
	@ApiOperation("获取定时任务详情")
	public Result<Job> get(@PathVariable("id") Long id) {
		return this.success(jobService.get(id));
	}


}

