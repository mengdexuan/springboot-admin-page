package com.boot.biz.urllimit.dto;


import lombok.Data;

@Data
public class UrlLimitDto {

    /**
     * req_url摘要加密串
     */
    private String urlKey;
    /**
     * 每秒钟允许的请求数，默认 0 ，表示不限制
     */
    private Integer urlLimit;
    /**
     * 接口功能描述
     */
    private String urlDesc;


}
