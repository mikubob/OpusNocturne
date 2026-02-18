package com.xuan.common.handle;

import com.xuan.common.domain.Result;
import com.xuan.common.enums.ErrorCode;
import com.xuan.common.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 全局异常处理器 (Global Exception Handler)
 * <p>
 * 本类通过 Spring 的 @RestControllerAdvice 注解实现对 Controller 层的环绕通知。
 * 它能够捕获整个系统中抛出的各种异常，并将其包装为统一的响应格式 (Result)，
 * 确保前端在任何错误情况下都能接收到结构一致的 JSON 数据，而不是原始的错误页面。
 *
 * @author 玄〤
 * @since 2026-02-18
 */
@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获自定义业务异常 (BusinessException)
     * <p>
     * 业务异常通常是我们手动在 Service 层抛出的已知错误（如：用户不存在、余额不足）。
     * 此处理器会直接提取异常中的错误码和描述信息返回给前端。
     * 
     * @param e 抛出的业务异常对象
     * @return 统一格式的失败响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 捕获参数校验异常 (使用 @RequestBody 接收数据时)
     * <p>
     * 当 Controller 方法参数标记了 @Valid 或 @Validated，且前端传参不符合注解规则（如 @NotBlank）时触发。
     * 本方法会提取所有字段的校验错误信息，并用分号拼接后统一返回。
     * 
     * @param e 校验框架抛出的异常
     * @return 状态码为参数错误的响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 利用 Stream 流处理，提取所有属性校验失败的 defaultMessage 并拼接
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)// 过滤 null
                .collect(Collectors.joining("; "));
        log.error("参数校验异常：{}", message);
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 捕获参数绑定异常 (使用非 JSON 方式传参时)
     * <p>
     * 常见于 GET 请求或表单提交时的参数类型不匹配或校验不通过。
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("; "));
        log.error("参数绑定异常：{}", message);
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 捕获约束校验异常 (@Validated 标记在类上时的单个参数校验)
     * <p>
     * 常见于方法参数中直接写 Long id 且标记了 @Min(1) 的情况。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("; "));
        log.error("约束校验异常：{}", message);
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 捕获非法参数异常 (JDK 自带异常)
     * <p>
     * 通常由断言（Assert）或逻辑判断手动抛出。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常：{}", e.getMessage());
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 处理空指针异常 (NullPointerException)
     * <p>
     * 为了用户体验和安全性，不向前端吐露详细的堆栈信息，统一提示“系统异常”。
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e) {
        // 打印详细堆栈到服务器日志，方便开发者排查
        log.error("空指针异常", e);
        return Result.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统异常，请联系管理员");
    }

    /**
     * 兜底异常处理 (Exception)
     * <p>
     * 捕获所有上述未明确处理的运行时异常或编译器异常。
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统异常，请联系管理员");
    }
}
