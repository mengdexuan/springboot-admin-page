package com.boot.biz.requestfollow.controller;


import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.biz.requestfollow.entity.RequestFollow;
import com.boot.biz.requestfollow.service.RequestFollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 请求追踪记录 控制器
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-12-16
 */
@RestController
@RequestMapping("/requestFollow")
@Api(tags = "请求追踪记录 接口")
public class RequestFollowController extends BaseController {

        @Autowired
        private RequestFollowService requestFollowService;

        /**
         * 获取请求追踪记录列表
         */
        @GetMapping(value = "/list")
        @ApiOperation("获取请求追踪记录列表")
        public Result<List<RequestFollow>> list() {
            return this.success(requestFollowService.findAll());
        }

        /**
         * 新增请求追踪记录
         */
        @PostMapping(value = "/add")
        @ApiOperation("新增请求追踪记录")
        public Result<String> add(RequestFollow requestFollow) {
            requestFollowService.save(requestFollow);
            return this.success();
        }

        /**
         * 删除请求追踪记录
         */
        @PostMapping(value = "/delete/{id}")
        @ApiOperation("删除请求追踪记录")
        public Result<String> delete(@PathVariable("id") Long id) {
            requestFollowService.delete(id);
            return this.success();
        }

        /**
         * 修改请求追踪记录
         */
        @PostMapping(value = "/update")
        @ApiOperation("修改请求追踪记录")
        public Result<String> update(RequestFollow requestFollow) {
            requestFollowService.update(requestFollow);
            return this.success();
        }

        /**
         * 获取请求追踪记录
         */
        @GetMapping(value = "/get/{id}")
        @ApiOperation("获取请求追踪记录")
        public Result<RequestFollow> get(@PathVariable("id") Long id) {
            return this.success(requestFollowService.get(id));
        }
}

