package com.xuan.common;

import lombok.Data;

/**
 * 统一响应包装类
 * 用于所有接口的统一返回格式
 * 
 * @param <T> 响应数据类型
 * @author 玄〤
 * @since 2026-02-16
 */
@Data
public class Result<T> {
    
    /**
     * 响应码
     * 200: 成功, 其他: 失败
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 成功响应
     * 
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("成功");
        result.setData(data);
        return result;
    }
    
    /**
     * 成功响应（无数据）
     * 
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> Result<T> success() {
        return success(null);
    }
    
    /**
     * 错误响应
     * 
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 错误响应对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
    
    /**
     * 错误响应（默认错误码）
     * 
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 错误响应对象
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}
