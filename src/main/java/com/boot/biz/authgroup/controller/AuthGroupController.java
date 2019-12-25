package com.boot.biz.authgroup.controller;


import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.biz.authgroup.entity.AuthGroup;
import com.boot.biz.authgroup.service.AuthGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 权限组表 控制器
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@RestController
@RequestMapping("/authgroup")
@Api(tags = "权限组表 接口")
public class AuthGroupController extends BaseController {

        @Autowired
        private AuthGroupService authGroupService;

        /**
         * 获取权限组表列表
         */
        @GetMapping(value = "/list")
        @ApiOperation("获取权限组表列表")
        public Result<List<AuthGroup>> list() {
            return this.success(authGroupService.findAll());
        }

        /**
         * 新增权限组表
         */
        @PostMapping(value = "/add")
        @ApiOperation("新增权限组表")
        public Result<String> add(AuthGroup authGroup) {
            authGroup.setId(null);
            authGroup.setStatus(1);
            authGroupService.save(authGroup);
            return this.success();
        }

        /**
         * 删除权限组表
         */
        @PostMapping(value = "/delete/{id}")
        @ApiOperation("删除权限组表")
        public Result<String> delete(@PathVariable("id") Long id) {
            authGroupService.delete(id);
            return this.success();
        }

        /**
         * 修改权限组表
         */
        @PostMapping(value = "/update")
        @ApiOperation("修改权限组表")
        public Result<String> update(AuthGroup authGroup) {
            authGroupService.update(authGroup);
            return this.success();
        }

        /**
         * 获取权限组表
         */
        @GetMapping(value = "/get/{id}")
        @ApiOperation("获取权限组表")
        public Result<AuthGroup> get(@PathVariable("id") Long id) {
            return this.success(authGroupService.get(id));
        }
}

