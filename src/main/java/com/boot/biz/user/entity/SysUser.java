package com.boot.biz.user.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "sys_user")
public class SysUser {

    @Id
    @GeneratedValue
    private Long id;
    /**
     * 登陆账号
     */
    private String loginname;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 状态:0 不可用    1可用
     */
    private Integer status;
    private Date createTime;
    private Date lastLoginTime;

    @Transient
    private String key;

}
