package com.xuan.entity.po.interact;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuan.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 附件/资源表实体类
 * 对应数据库表：attachment
 * 用于存储上传的文件信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attachment")
public class Attachment extends BaseEntity {
    /**
     * 原文件名
     */
    private String fileName;

    /**
     * 访问URL
     */
    private String fileUrl;

    /**
     * 储存路径
     */
    private String filePath;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 业务类型(article/avatar)
     */
    private String bizType;

    /**
     * 业务id
     */
    private Long bizId;
}
