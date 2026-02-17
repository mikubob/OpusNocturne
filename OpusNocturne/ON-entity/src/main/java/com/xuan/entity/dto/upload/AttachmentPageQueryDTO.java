package com.xuan.entity.dto.upload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 附件分页查询请求参数类
 * 对应接口：7.2.1 分页获取附件列表
 * 用于接收前端分页查询附件的参数
 * @author 玄〤
 * @since 2026-02-17
 */
@Data
@Schema(description = "附件分页查询请求参数类")
public class AttachmentPageQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码，默认 1
     */
    @Schema(description = "当前页码", example = "1")
    private Integer current;

    /**
     * 每页条数，默认 10
     */
    @Schema(description = "每页条数", example = "10")
    private Integer size;

    /**
     * 文件名搜索
     */
    @Schema(description = "文件名搜索", example = "demo")
    private String fileName;

    /**
     * 按业务类型筛选
     */
    @Schema(description = "按业务类型筛选", example = "article")
    private String bizType;
}
