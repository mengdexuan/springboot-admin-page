package com.boot.biz.attachment.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>
 * 附件
 * </p>
 *
 * @author code-make-tool
 * @since 2020-03-16
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "attachment")
public class Attachment {

    @Id
    @GeneratedValue
    private Long id;
    /**
     * 附件ID
     */
    private String uuid;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件后缀
     */
    private String fileExt;
    /**
     * 文件存储路径
     */
    private String filePath;
    /**
     * 文件大小
     */
    private Long fileSize;
    /**
     * 上传时间
     */
    private Date createTime;


}
