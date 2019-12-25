package com.boot.biz.keyval.entity;

import cn.hutool.json.JSONUtil;
import com.boot.biz.db.dto.DbConnDto;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>
 * 键值记录表
 * </p>
 *
 * @author code-generate-tool
 * @since 2019-10-23
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "key_val")
public class KeyVal {

    @Id
    @GeneratedValue
    private Long id;
    private String dataId;
    private String dataKey;
    private String dataVal;
    private Date createTime;


    /**
     * 将 dataVal 值转化为需要的数据类型
     * @param beanClass
     * @param <T>
     * @return
     */
    public <T> T trans(Class<T> beanClass) {
        T t = JSONUtil.toBean(dataVal,beanClass);
        return t;
    }

}
