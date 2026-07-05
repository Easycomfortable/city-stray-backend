package com.citystray.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 * <p>
 * 所有接口返回数据均使用此类进行包装，
 * 包含状态码、提示信息和业务数据三部分。
 * </p>
 *
 * @param <T> 响应数据的泛型类型
 * @author CityStray Team
 * @since 1.0.0
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 响应状态码，200表示成功 */
    private Integer code;

    /** 响应提示信息 */
    private String message;

    /** 响应业务数据 */
    private T data;

    /**
     * 私有构造方法，防止外部直接实例化
     */
    private Result() {
    }

    /**
     * 返回成功结果（无数据）
     *
     * @param <T> 泛型类型
     * @return 成功响应对象
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 返回成功结果（携带数据）
     *
     * @param data 业务数据
     * @param <T>  泛型类型
     * @return 成功响应对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 返回成功结果（自定义消息和数据）
     *
     * @param message 提示信息
     * @param data    业务数据
     * @param <T>     泛型类型
     * @return 成功响应对象
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 返回失败结果（默认500状态码）
     *
     * @param message 错误提示信息
     * @param <T>     泛型类型
     * @return 失败响应对象
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    /**
     * 返回失败结果（自定义状态码）
     *
     * @param code    错误状态码
     * @param message 错误提示信息
     * @param <T>     泛型类型
     * @return 失败响应对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
