package com.boot.biz.menu.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "sys_menu")
public class Menu {

    @Id
    @GeneratedValue
    private Long id;
    /**
     * 菜单名称
     */
    private String title;
    /**
     * 菜单访问url
     */
    private String url;
    /**
     * 状态: 0隐藏   1显示
     */
    private Integer status;
    /**
     * 父级ID
     */
    private Long pid;
    /**
     * 图标
     */
    private String icon;
    /**
     * 排序
     */
    private Integer sort;

    //'1.前台菜单   2.后台菜单',
    private Integer type;


}
