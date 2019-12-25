package com.boot.biz.userauthgroup.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * 用户权限组关系表
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "user_auth_group")
public class UserAuthGroup {

    @Id
    @GeneratedValue
    private Long id;
    /**
     * 用户ID
     */
    private Long uid;
    /**
     * 权限组ID
     */
    private Long groupId;


}
