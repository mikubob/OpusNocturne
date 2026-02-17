package com.xuan.entity.dto.upload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传请求参数类
 * 对应接口：7.1 上传文件
 * 用于接收前端文件上传的参数
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
@Schema(description = "文件上传请求参数类")
public class UploadDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件对象，必填
     */
    @NotNull(message = "文件不能为空")
    @Schema(description = "文件对象", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile file;
    
    /**
     * 业务类型
     */
    @Schema(description = "业务类型，如 article, avatar", example = "article")
    private String bizType;

    /**
     * 业务ID，如文章ID
     */
    @Schema(description = "业务ID，如文章ID", example = "100")
    private Long bizId;
}