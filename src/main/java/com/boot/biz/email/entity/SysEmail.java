package com.boot.biz.email.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>
 * 邮件发送
 * </p>
 *
 * @author code-generate-tool
 * @since 2021-12-28
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "sys_email")
public class SysEmail {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String content;
    /**
     * 发件者邮箱
     */
    private String fromAddr;
    /**
     * 接收者地址列表，逗号分隔多个地址
     */
    private String toAddrs;
    /**
     * 0：待发送  1：发送成功  2：发送失败
     */
    private Integer status;
    /**
     * 发送失败，错误信息
     */
    private String errMsg;
    private Date createTime;


}
