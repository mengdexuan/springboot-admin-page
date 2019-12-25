package com.boot.biz.dict.controller;


import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.biz.dict.entity.Dict;
import com.boot.biz.dict.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 数据字典表 控制器
 * </p>
 *
 * @author adi
 * @since 2019-05-24
 */
@RestController
@RequestMapping("/dict")
public class DictController {

        @Autowired
        private DictService dictService;

        /**
         * 获取数据字典表列表
         */
        @GetMapping(value = "/list")
        public Result<List<Dict>> list() {
            return ResultUtil.buildSuccess(dictService.findAll());
        }

        /**
         * 新增数据字典表
         */
        @PostMapping(value = "/add")
        public Result<String> add(Dict dataCentralDict) {
            dictService.save(dataCentralDict);
            return ResultUtil.buildSuccess();
        }

        /**
         * 删除数据字典表
         */
        @PostMapping(value = "/delete/{id}")
        public Result<String> delete(@PathVariable("id") Long id) {
            dictService.delete(id);
            return ResultUtil.buildSuccess();
        }

        /**
         * 修改数据字典表
         */
        @PostMapping(value = "/update")
        public Result<String> update(Dict dataCentralDict) {
            dictService.update(dataCentralDict);
            return ResultUtil.buildSuccess();
        }

        /**
         * 获取数据字典表
         */
        @GetMapping(value = "/get/{id}")
        public Result<Dict> get(@PathVariable("id") Long id) {
            return ResultUtil.buildSuccess(dictService.get(id));
        }
}

