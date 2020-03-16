package com.boot.biz.springtask.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>
 * spring定时任务
 * </p>
 *
 * @author code-make-tool
 * @since 2020-03-15
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "spring_task")
public class SpringTask {

    @Id
    @GeneratedValue
    private Long id;
    /**
     * 任务ID,随机字符串
     */
    private String taskId;
    /**
     * 任务名称
     */
    private String name;
    /**
     * spring bean名称
     */
    private String bean;
    /**
     * 方法名
     */
    private String method;
    /**
     * 参数
     */
    private String params;
    /**
     * cron表达式
     */
    private String cron;
    /**
     * 任务状态  1.运行  2.暂停
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 错误日志
     */
    private String errLog;
    /**
     * 执行成功是否删除该任务 1：不删除    2：删除
     */
    private Integer delWhenSuccess;
    /**
     * 创建时间
     */
    private Date createTime;


    public enum Status {
        // 默认 运行
        DEFAULT(1),

        //运行
        RUN(1),

        // 暂停
        PAUSE(2);

        private int value;

        private Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum DelWhenSuccess {

        // 默认不删除
        DEFAULT(1),
        // 删除
        YES(2),
        // 不删除
        NO(1);

        private int value;

        private DelWhenSuccess(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
