package com.xuan.opusnocturne.entity.vo.upload;

import lombok.Data;

/**
 * 文件上传响应数据类
 * 对应接口：7.1 上传文件响应
 * 用于返回文件上传后的信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class UploadVO {
    /**
     * 文件ID
     */
    private Long id;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 文件大小
     */
    private Long fileSize;
}