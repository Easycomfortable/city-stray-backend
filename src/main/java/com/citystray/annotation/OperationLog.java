package com.citystray.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解 - 标注在 Controller 方法上，AOP 切面自动记录操作日志到 sys_log
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /** 操作模块，如 "动物管理"、"领养管理" */
    String module() default "";

    /** 操作类型：CREATE / UPDATE / DELETE / QUERY / LOGIN / EXPORT */
    String type() default "QUERY";

    /** 操作描述，如 "新增动物档案" */
    String content() default "";
}
