package com.boot.biz.userauthgroup.controller;


import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.base.util.HelpMe;
import com.boot.biz.userauthgroup.entity.UserAuthGroup;
import com.boot.biz.userauthgroup.service.UserAuthGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户权限组关系表 控制器
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@RestController
@RequestMapping("/userauthgroup")
@Api(tags = "用户权限组关系表 接口")
public class UserAuthGroupController extends BaseController {

        @Autowired
        private UserAuthGroupService userAuthGroupService;

        /**
         * 获取用户权限组关系表列表
         */
        @GetMapping(value = "/list")
        @ApiOperation("获取用户权限组关系表列表")
        public Result<List<UserAuthGroup>> list() {
            return this.success(userAuthGroupService.findAll());
        }


    /**
     * 新增用户权限组关系表
     */
    @PostMapping(value = "/save")
    @ApiOperation("新增用户权限组关系表")
    public Result<String> save(Long uid,String groupIdStr) {

        List<String> list = HelpMe.easySplit(groupIdStr);


        UserAuthGroup temp = new UserAuthGroup();
        temp.setUid(uid);

        List<UserAuthGroup> tempList = userAuthGroupService.list(temp);

        userAuthGroupService.delete(tempList);

        if (HelpMe.isNotNull(list)){

            List<UserAuthGroup> userAuthGroupList = list.stream().map(item -> {
                UserAuthGroup userAuthGroup = new UserAuthGroup();
                userAuthGroup.setUid(uid);
                userAuthGroup.setGroupId(Long.parseLong(item));
                return userAuthGroup;
            }).collect(Collectors.toList());

            userAuthGroupService.save(userAuthGroupList);
        }


        return this.success();
    }


        /**
         * 新增用户权限组关系表
         */
        @PostMapping(value = "/add")
        @ApiOperation("新增用户权限组关系表")
        public Result<String> add(UserAuthGroup userAuthGroup) {
            userAuthGroupService.save(userAuthGroup);
            return this.success();
        }

        /**
         * 删除用户权限组关系表
         */
        @PostMapping(value = "/delete/{id}")
        @ApiOperation("删除用户权限组关系表")
        public Result<String> delete(@PathVariable("id") Long id) {
            userAuthGroupService.delete(id);
            return this.success();
        }

        /**
         * 修改用户权限组关系表
         */
        @PostMapping(value = "/update")
        @ApiOperation("修改用户权限组关系表")
        public Result<String> update(UserAuthGroup userAuthGroup) {
            userAuthGroupService.update(userAuthGroup);
            return this.success();
        }

        /**
         * 获取用户权限组关系表
         */
        @GetMapping(value = "/get/{id}")
        @ApiOperation("获取用户权限组关系表")
        public Result<UserAuthGroup> get(@PathVariable("id") Long id) {
            return this.success(userAuthGroupService.get(id));
        }
}

