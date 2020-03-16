package com.boot.biz.springtask.controller;


import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.biz.springtask.entity.SpringTask;
import com.boot.biz.springtask.service.SpringTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	/**
	 * 获取spring定时任务列表
	 */
	@GetMapping(value = "/list")
	@ApiOperation("获取spring定时任务列表")
	public Result<List<SpringTask>> list() {
		return this.success(springTaskService.findAll());
	}

	/**
	 * 新增spring定时任务
	 */
	@PostMapping(value = "/add")
	@ApiOperation("新增spring定时任务")
	public Result<String> add(SpringTask springTask) {
		springTaskService.save(springTask);
		return this.success();
	}

	/**
	 * 删除spring定时任务
	 */
	@PostMapping(value = "/delete/{id}")
	@ApiOperation("删除spring定时任务")
	public Result<String> delete(@PathVariable("id") Long id) {
		springTaskService.delete(id);
		return this.success();
	}

	/**
	 * 修改spring定时任务
	 */
	@PostMapping(value = "/update")
	@ApiOperation("修改spring定时任务")
	public Result<String> update(SpringTask springTask) {
		springTaskService.update(springTask);
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

