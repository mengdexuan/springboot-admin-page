package com.boot.biz.camel.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>
 * camel路由
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-06-27
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "camel_route")
public class CamelRoute {

    /**
     * 路由id
     */
    @Id
    private String routeId;
    /**
     * 路由状态
Starting：正在启动
Started：已启动
Stopping：正在停止
Stopped：已停止
Suspending：正在暂停
Suspended：已暂停
     */
    private String status;
    /**
     * 路由描述
     */
    private String description;
    private Date createTime;


}
