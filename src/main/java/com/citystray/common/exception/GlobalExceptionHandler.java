package com.citystray.common.exception;

import com.citystray.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 统一拦截并处理系统中抛出的各类异常，
 * 将异常转换为规范的JSON响应格式，避免暴露系统内部信息。
 * </p>
 *
 * @author CityStray Team
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常对象
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@RequestBody 绑定）
     *
     * @param e 参数校验异常对象
     * @return 统一错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理参数绑定异常（表单提交等）
     *
     * @param e 参数绑定异常对象
     * @return 统一错误响应
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getAllErrors().get(0).getDefaultMessage();
        log.warn("参数绑定失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理运行时异常（业务校验类）
     *
     * @param e 运行时异常对象
     * @return 统一错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理其他未知异常
     *
     * @param e 异常对象
     * @return 统一错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error("系统异常，请联系管理员");
    }
}
