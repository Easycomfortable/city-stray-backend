package com.citystray.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 当业务逻辑出现预期内的错误时抛出此异常，
 * 例如：用户不存在、参数不合法、权限不足等。
 * 全局异常处理器会捕获并转换为统一响应格式。
 * </p>
 *
 * @author CityStray Team
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误状态码 */
    private final Integer code;

    /**
     * 构造业务异常（默认500状态码）
     *
     * @param message 错误提示信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造业务异常（自定义状态码）
     *
     * @param code    错误状态码
     * @param message 错误提示信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
