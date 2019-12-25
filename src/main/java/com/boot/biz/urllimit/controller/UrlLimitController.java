package com.boot.biz.urllimit.controller;

import com.boot.base.Result;
import com.boot.base.ResultUtil;
import com.boot.biz.urllimit.UrlRateLimiterCache;
import com.boot.biz.urllimit.dto.UrlLimitDto;
import com.boot.biz.urllimit.entity.UrlLimit;
import com.boot.biz.urllimit.service.UrlLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 接口限流 控制器
 * </p>
 *
 * @author adi
 * @since 2019-06-21
 */
@Slf4j
@RestController
@RequestMapping("/urllimit/urlLimit")
public class UrlLimitController {

        @Autowired
        private UrlLimitService urlLimitService;

        /**
         * 获取接口限流列表
         */
        @GetMapping(value = "/list")
        public Result<List<UrlLimit>> list() {
            return ResultUtil.buildSuccess(urlLimitService.findAll());
        }

        /**
         * 新增接口限流
         */
        @PostMapping(value = "/add")
        public Result<String> add(UrlLimit urlLimit) {
            urlLimitService.save(urlLimit);
            return ResultUtil.buildSuccess();
        }

        /**
         * 删除接口限流
         */
        @PostMapping(value = "/delete/{id}")
        public Result<String> delete(@PathVariable("id") Long id) {
            urlLimitService.delete(id);
            return ResultUtil.buildSuccess();
        }

        /**
         * 修改接口限流
         */
        @PostMapping(value = "/update")
        public Result<String> update(UrlLimitDto urlLimitDto) {

            UrlLimit urlLimitTemp = new UrlLimit();
            urlLimitTemp.setUrlKey(urlLimitDto.getUrlKey());
            UrlLimit urlLimit = urlLimitService.one(urlLimitTemp);

            urlLimit.setUrlLimit(urlLimitDto.getUrlLimit());
            if (urlLimitDto.getUrlLimit().intValue()<0){
                urlLimit.setUrlLimit(0);
            }
            urlLimit.setUrlDesc(urlLimitDto.getUrlDesc());

            urlLimitService.save(urlLimit);

            //删除缓存中的数据
            UrlRateLimiterCache.remove(urlLimit.getUrlKey());
            if (urlLimit.getUrlLimit().intValue()>0){
                //重新加入缓存
                UrlRateLimiterCache.set(urlLimit.getUrlKey(),urlLimit.getUrlLimit());
            }

            return ResultUtil.buildSuccess();
        }



        /**
         * 获取接口限流
         */
        @GetMapping(value = "/get/{id}")
        public Result<UrlLimit> get(@PathVariable("id") Long id) {
            return ResultUtil.buildSuccess(urlLimitService.get(id));
        }
}

