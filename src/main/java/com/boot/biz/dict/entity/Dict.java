package com.boot.biz.dict.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>
 * 数据字典
 * </p>
 *
 * @author adi
 * @since 2019-05-24
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "sys_dict")
public class Dict {

    @Id
    @GeneratedValue
    private Long id;
    /**
     * 字典名称
     */
    private String dictName;
    /**
     * 父字典key
     */
    private String pKey;
    /**
     * 字典key
     */
    private String dictKey;
    /**
     * 字典值
     */
    private String dictValue;
    /**
     * 备注
     */
    private String dictNote;
    /**
     * 是否可用：0：不可用  1：可用
     */
    private Integer enable;
    /**
     * 排序字段
     */
    private Integer orderNo;
    /**
     * 更新时间
     */
    private Date createTime;

}
