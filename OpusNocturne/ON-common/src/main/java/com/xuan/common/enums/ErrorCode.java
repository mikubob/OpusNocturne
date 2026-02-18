package com.xuan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author 玄〤
 * @since 2026-02-18
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 通用错误码 1xxx
    SUCCESS(0, "success"),
    SYSTEM_ERROR(1000, "系统异常"),
    PARAM_ERROR(1001, "参数错误"),
    NOT_FOUND(1002, "资源不存在"),
    OPERATION_FAILED(1003, "操作失败"),

    // 认证授权错误码 2xxx
    UNAUTHORIZED(2001, "未登录或Token已失效"),
    FORBIDDEN(2003, "无权限访问"),
    LOGIN_FAILED(2004, "用户名或密码错误"),
    TOKEN_EXPIRED(2005, "Token已过期"),
    TOKEN_INVALID(2006, "Token无效"),

    // 用户相关错误码 3xxx
    USER_NOT_FOUND(3001, "用户不存在"),
    USER_DISABLED(3002, "用户已被禁用"),
    USER_EXISTS(3003, "用户已存在"),
    PASSWORD_ERROR(3004, "密码错误"),

    // 角色权限错误码 4xxx
    ROLE_NOT_FOUND(4001, "角色不存在"),
    ROLE_EXISTS(4002, "角色已存在"),
    PERMISSION_NOT_FOUND(4003, "权限不存在"),

    // 文章相关错误码 5xxx
    ARTICLE_NOT_FOUND(5001, "文章不存在"),
    CATEGORY_NOT_FOUND(5002, "分类不存在"),
    TAG_NOT_FOUND(5003, "标签不存在"),

    // 评论相关错误码 6xxx
    COMMENT_NOT_FOUND(6001, "评论不存在"),
    COMMENT_AUDIT_FAILED(6002, "评论审核失败"),

    // 文件相关错误码 7xxx
    FILE_UPLOAD_FAILED(7001, "文件上传失败"),
    FILE_TYPE_ERROR(7002, "文件类型不支持"),
    FILE_SIZE_EXCEEDED(7003, "文件大小超出限制"),
    FILE_NOT_FOUND(7004, "文件不存在");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;
}
