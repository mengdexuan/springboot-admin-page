package com.boot.biz.urllimit.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>
 * 接口限流
 * </p>
 *
 * @author adi
 * @since 2019-06-21
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "url_limit")
public class UrlLimit {

    /**
     * 接口请求地址
     */
    private String reqUrl;
    /**
     * req_url摘要加密串
     */
    @Id
    private String urlKey;
    /**
     * 每秒钟允许的请求数，默认 0 ，表示不限制
     */
    private Integer urlLimit;
    /**
     * 接口功能描述
     */
    private String urlDesc;
    private Date createTime;


}
