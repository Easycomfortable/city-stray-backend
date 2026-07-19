package com.citystray.aspect;

import com.citystray.annotation.OperationLog;
import com.citystray.entity.SysLog;
import com.citystray.service.LogService;
import com.citystray.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面 - 拦截 @OperationLog 注解，自动记录操作日志到 sys_log
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {

    private final LogService logService;

    @Around("@annotation(com.citystray.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog opLog = method.getAnnotation(OperationLog.class);

        // 获取 HTTP 请求信息
        HttpServletRequest request = getRequest();

        SysLog sysLog = new SysLog();
        sysLog.setUserId(UserContext.getUserId());
        sysLog.setUsername(UserContext.getUsername());
        sysLog.setModule(opLog.module());
        sysLog.setType(opLog.type());
        sysLog.setContent(opLog.content());
        sysLog.setMethod(request != null ? request.getMethod() : signature.getDeclaringTypeName() + "." + method.getName());
        sysLog.setUrl(request != null ? request.getRequestURI() : "");
        sysLog.setIp(request != null ? getClientIp(request) : "");
        sysLog.setCreateTime(LocalDateTime.now());

        Object result;
        try {
            result = joinPoint.proceed();
            sysLog.setSuccess(1);
        } catch (Throwable ex) {
            sysLog.setSuccess(0);
            sysLog.setErrorMsg(ex.getMessage() != null ? ex.getMessage().substring(0, Math.min(ex.getMessage().length(), 500)) : "未知错误");
            throw ex;
        } finally {
            sysLog.setDuration((int) (System.currentTimeMillis() - startTime));
            try {
                logService.save(sysLog);
            } catch (Exception e) {
                log.warn("操作日志保存失败: {}", e.getMessage());
            }
        }

        return result;
    }

    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 取第一个 IP（多级代理场景）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
