package com.xuan.common.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 基础分页查询DTO
 * 包含所有分页查询共有的字段
 * 
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class BasePageQueryDTO {
    
    /**
     * 当前页码
     * 最小值为1
     */
    @Min(value = 1, message = "页码最小为1")
    private Integer current = 1;
    
    /**
     * 每页条数
     * 范围为1-100
     */
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 100, message = "每页条数最大为100")
    private Integer size = 10;
}
