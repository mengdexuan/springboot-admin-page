package com.boot.biz.schedule.entity;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
@Table(name = "quartz_schedule_job")
public class ScheduleJob implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务调度参数key
     */
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

    /**
     * 任务id
     */
    @Id
    @GeneratedValue
    private Long id;

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
    private String expression;

    /**
     * 任务状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

    /**
     * 错误日志
     */
    private String log;

    private Date createTime;

    /**
     * 执行成功是否删除该任务  0：删除    1：不删除
     */
    private Integer delOnSuccess;

    /**
     * 是否允许任务并发执行   0：是     1：否
     */
    private Integer allowConcurrent;

    /**
     * 是否允许任务删除     0：是     1：否
     */
    private Integer allowDel;

    public enum Status {
        // 默认
        DEFAULT(0),

        NORMAL(0),
        // 暂停
        PAUSE(1);

        private int value;

        private Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum DelOnSuccess {
		// 默认
		DEFAULT(0),
        // 删除
        YES(0),
        // 不删除
        NO(1);

        private int value;

        private DelOnSuccess(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum AllowConcurrent {
		// 默认
		DEFAULT(0),
        // 是
        YES(0),

        // 否
        NO(1);

        private int value;

        private AllowConcurrent(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum AllowDel {
		// 默认
		DEFAULT(0),
        // 是
        YES(0),

        // 否
        NO(1);

        private int value;

        private AllowDel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
