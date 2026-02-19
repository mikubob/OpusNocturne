package com.xuan.service.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 接口日志切面
 * 自动记录所有 Controller 请求的方法名、参数、耗时和响应
 *
 * @author 玄〤
 * @since 2026-02-20
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * 切入点：所有 Controller 包下的方法
     */
    @Pointcut("execution(* com.xuan.service.controller..*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String method = "";
        String uri = "";
        String ip = "";
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            method = request.getMethod();
            uri = request.getRequestURI();
            ip = request.getRemoteAddr();
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("====> 请求: {} {} | IP: {} | 方法: {}.{}",
                method, uri, ip, className, methodName);

        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("<==== 响应: {}.{} | 耗时: {}ms", className, methodName, costTime);
        } catch (Throwable e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("<==== 异常: {}.{} | 耗时: {}ms | 错误: {}",
                    className, methodName, costTime, e.getMessage());
            throw e;
        }
        return result;
    }
}
