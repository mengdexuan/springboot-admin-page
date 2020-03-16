package com.boot.biz.springtask.controller;


import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.base.exception.GlobalServiceException;
import com.boot.base.util.CronCheckUtil;
import com.boot.base.util.HelpMe;
import com.boot.biz.springtask.entity.SpringTask;
import com.boot.biz.springtask.service.SpringTaskService;
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
@RequestMapping("/springTask")
@Api(tags = "spring定时任务 接口")
public class SpringTaskController extends BaseController {

	@Autowired
	private SpringTaskService springTaskService;




	@GetMapping(value = "/getByJobId/{jobId}")
	public Result<String> getByJobId(@PathVariable("jobId") Long jobId) {

		String result = "";

		SpringTask one = springTaskService.get(jobId);

		if (HelpMe.isNotNull(one.getErrLog())){
			result = one.getErrLog();
		}

		return ResultUtil.buildSuccess("success",result);
	}

	/**
	 * 删除spring定时任务
	 */
	@PostMapping(value = "/delete/{id}")
	@ApiOperation("删除spring定时任务")
	public Result<String> delete(@PathVariable("id") Long id) {

		SpringTask task = springTaskService.get(id);

		springTaskService.delSpringTask(task.getTaskId());

		return this.success();
	}

	/**
	 * 修改spring定时任务
	 */
	@PostMapping(value = "/update")
	@ApiOperation("修改spring定时任务")
	public Result<String> update(SpringTask springTask) {

		SpringTask task = springTaskService.get(springTask.getId());

		if (springTask.getCron().equals(task.getCron())){
			springTaskService.update(springTask);
		}else {
			//修改了 cron 表达式
			boolean ok = CronCheckUtil.ok(springTask.getCron());
			if (ok){
				try {
					springTaskService.update(springTask);
					springTaskService.changeSpringTaskCron(task.getTaskId(),springTask.getCron());
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
	public Result<String> updateStatus(Long id) {

		SpringTask task = springTaskService.get(id);

		if (task.getStatus().intValue()==SpringTask.Status.RUN.getValue()){
			springTaskService.parseSpringTask(task.getTaskId());
		}else {
			try {
				springTaskService.restartSpringTask(task.getTaskId());
			} catch (Exception e) {
			}
		}

		return ResultUtil.buildSuccess();
	}



	/**
	 * 手动触发spring定时任务
	 */
	@PostMapping(value = "/manual")
	@ApiOperation("手动触发spring定时任务")
	public Result<String> manual(Long id) {

		SpringTask task = springTaskService.get(id);

		try {
			springTaskService.manualTrigger(task.getTaskId());
		} catch (Exception e) {
		}

		return this.success();
	}



	/**
	 * 获取spring定时任务
	 */
	@GetMapping(value = "/get/{id}")
	@ApiOperation("获取spring定时任务")
	public Result<SpringTask> get(@PathVariable("id") Long id) {
		return this.success(springTaskService.get(id));
	}


}

