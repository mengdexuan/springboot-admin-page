package com.boot.biz.authgroup.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * 权限组表
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "auth_group")
public class AuthGroup {

    @Id
    @GeneratedValue
    private Long id;
    /**
     * 权限组名称
     */
    private String title;
    /**
     * 状态：0不可用    1可用
     */
    private Integer status;
    /**
     * 菜单ID列表，多个用逗号隔开
     */
    private String menus;

    /**
     *1.前台权限组   2.后台权限组
     */
    private Integer type;

}
