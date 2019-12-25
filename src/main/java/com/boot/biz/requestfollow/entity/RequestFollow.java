package com.boot.biz.requestfollow.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>
 * 请求追踪记录
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-12-16
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "request_follow")
public class RequestFollow {

    @Id
    @GeneratedValue
    private Long id;
    /**
     * 请求路径
     */
    private String path;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 请求参数
     */
    private String requestParam;
    /**
     * 响应参数
     */
    private String responseMsg;
    /**
     * 响应时间（ms）
     */
    private Integer spendTime;
    /**
     * http 状态码
     */
    private Integer status;
    /**
     * 创建时间（请求时间）
     */
    private Date createTime;


}
