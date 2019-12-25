package com.boot.biz.menu.controller;


import com.boot.base.BaseController;
import com.boot.base.Result;
import com.boot.biz.menu.entity.Menu;
import com.boot.biz.menu.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 控制器
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@RestController
@RequestMapping("/menu")
@Api(tags = "菜单表 接口")
public class MenuController extends BaseController {

        @Autowired
        private MenuService menuService;

        /**
         * 获取菜单表列表
         */
        @GetMapping(value = "/list")
        @ApiOperation("获取菜单表列表")
        public Result<List<Menu>> list(Long pid) {

            List<Menu> list;

            if (pid==null){
                list = menuService.findAll();
            }else {
                list = menuService.list("pid",(Object) pid,true);
            }

            return this.success(list);
        }

        /**
         * 新增菜单表
         */
        @PostMapping(value = "/add")
        @ApiOperation("新增菜单表")
        public Result<String> add(Menu menu) {
            menu.setId(null);
            menuService.save(menu);
            return this.success();
        }

        /**
         * 删除菜单表
         */
        @PostMapping(value = "/delete/{id}")
        @ApiOperation("删除菜单表")
        public Result<String> delete(@PathVariable("id") Long id) {

            Menu one = menuService.get(id);

            List<Menu> list = menuService.list("pid", (Object) id, true);

            list.add(one);

            menuService.delete(list);

            return this.success();
        }

        /**
         * 修改菜单表
         */
        @PostMapping(value = "/update")
        @ApiOperation("修改菜单表")
        public Result<String> update(Menu menu) {
            menuService.update(menu);
            return this.success();
        }

        /**
         * 获取菜单表
         */
        @GetMapping(value = "/get/{id}")
        @ApiOperation("获取菜单表")
        public Result<Menu> get(@PathVariable("id") Long id) {
            return this.success(menuService.get(id));
        }
}

