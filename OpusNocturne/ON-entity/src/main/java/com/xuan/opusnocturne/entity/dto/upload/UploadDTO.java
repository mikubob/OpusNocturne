package com.xuan.opusnocturne.entity.dto.upload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传请求参数类
 * 对应接口：7.1 上传文件
 * 用于接收前端文件上传的参数
 */
@Data
public class UploadDTO {
    /**
     * 文件对象，必填
     */
    @NotNull(message = "文件不能为空")
    private MultipartFile file;
    
    /**
     * 业务类型
     */
    private String bizType;
}