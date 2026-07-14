package com.citystray.aspect;

import com.citystray.annotation.RequireRole;
import com.citystray.common.Result;
import com.citystray.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 角色权限校验切面
 * <p>
 * 拦截所有标注了 @RequireRole 的类或方法，
 * 校验当前登录用户是否拥有要求的角色。
 * admin 角色默认放行所有接口。
 * </p>
 */
@Slf4j
@Aspect
@Component
public class RoleCheckAspect {

    @Around("@annotation(com.citystray.annotation.RequireRole) || @within(com.citystray.annotation.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        String role = UserContext.getRole();

        // 未登录（不应发生，JwtInterceptor已拦截）
        if (role == null) {
            return Result.error(401, "请先登录");
        }

        // 优先取方法级注解，没有则取类级注解
        RequireRole annotation = getMethodAnnotation(joinPoint);
        if (annotation == null) {
            annotation = joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
        }

        if (annotation == null) {
            // 没有注解，直接放行
            return joinPoint.proceed();
        }

        // admin 角色默认拥有所有权限
        if ("ADMIN".equalsIgnoreCase(role)) {
            return joinPoint.proceed();
        }

        // 校验角色是否匹配
        String[] allowedRoles = annotation.value();
        boolean matched = Arrays.stream(allowedRoles)
                .anyMatch(r -> r.equalsIgnoreCase(role));

        if (matched) {
            return joinPoint.proceed();
        }

        // 权限不足
        log.warn("权限不足: 用户角色={}, 需要角色={}, 接口={}.{}",
                role,
                Arrays.toString(allowedRoles),
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
        return Result.error(403, "权限不足，当前角色无权访问此功能");
    }

    /**
     * 获取方法上的 @RequireRole 注解
     */
    private RequireRole getMethodAnnotation(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            return method.getAnnotation(RequireRole.class);
        } catch (Exception e) {
            return null;
        }
    }
}
