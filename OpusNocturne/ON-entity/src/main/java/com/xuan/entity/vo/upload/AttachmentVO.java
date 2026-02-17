package com.xuan.entity.vo.upload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 附件管理响应数据类
 * 对应接口：7.2 附件管理
 * 用于返回附件的详细信息
 * @author 玄〤
 * @since 2026-02-17
 */
@Data
@Schema(description = "附件管理响应数据类")
public class AttachmentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 附件ID
     */
    @Schema(description = "附件ID", example = "1")
    private Long id;

    /**
     * 原文件名
     */
    @Schema(description = "原文件名", example = "cover.png")
    private String fileName;

    /**
     * 访问URL
     */
    @Schema(description = "访问URL", example = "https://cdn.jsdelivr.net/gh/xuan-xuan/blog-images/2026-02-16/cover.png")
    private String fileUrl;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型", example = "image/png")
    private String fileType;

    /**
     * 文件大小(字节)
     */
    @Schema(description = "文件大小(字节)", example = "102400")
    private Long fileSize;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型", example = "article")
    private String bizType;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID", example = "100")
    private Long bizId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2026-02-17T10:00:00")
    private LocalDateTime createTime;
}
