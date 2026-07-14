package com.citystray.annotation;

import java.lang.annotation.*;

/**
 * 角色权限校验注解
 * <p>
 * 标注在 Controller 类或方法上，限制只有指定角色才能访问。
 * 方法级注解优先于类级注解。
 * admin 角色默认拥有所有权限。
 * </p>
 *
 * 用法示例：
 * <pre>
 *   &#64;RequireRole({"admin", "rescue_admin"})
 *   public Result<?> someEndpoint() { ... }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * 允许访问的角色列表（不区分大小写）
     * 当前用户角色匹配其中任意一个即可通过
     */
    String[] value();
}
