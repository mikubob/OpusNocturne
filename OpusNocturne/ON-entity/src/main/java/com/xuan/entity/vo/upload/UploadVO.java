package com.xuan.entity.vo.upload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传响应数据类
 * 对应接口：7.1 上传文件响应
 * 用于返回文件上传成功后的信息
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@Schema(description = "文件上传响应数据类")
public class UploadVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 文件ID
     */
    @Schema(description = "文件ID", example = "1")
    private Long id;
    
    /**
     * 文件名
     */
    @Schema(description = "文件名", example = "cover.png")
    private String fileName;
    
    /**
     * 文件URL
     */
    @Schema(description = "文件URL", example = "https://cdn.jsdelivr.net/gh/xuan-xuan/blog-images/2026-02-16/cover.png")
    private String fileUrl;
    
    /**
     * 文件大小
     */
    @Schema(description = "文件大小", example = "102400")
    private Long fileSize;
}