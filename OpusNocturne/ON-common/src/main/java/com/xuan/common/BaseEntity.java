package com.xuan.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础实体类
 * 包含所有实体共有的字段
 * 
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public abstract class BaseEntity {
    
    /**
     * 主键ID
     * 使用自增策略
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 逻辑删除标识
     * 1: 删除, 0: 未删除
     */
    @TableLogic
    private Integer isDelete;
    
    /**
     * 创建时间
     * 插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     * 插入和更新时自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
