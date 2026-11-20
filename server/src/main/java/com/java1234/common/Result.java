package com.java1234.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 *
 * <p>所有 Controller 接口均返回该结构，前端只需判断 code 是否为 200。</p>
 *
 * @author Java1234
 */
@Data
public class Result<T> implements Serializable {

    /** 状态码：200 成功，其余为失败 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 业务数据 */
    private T data;

    /**
     * 构造默认空结果
     */
    public Result() {
    }

    /**
     * 构造带状态码、提示信息的结果
     *
     * @param code 状态码
     * @param msg  提示信息
     */
    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造带数据的结果
     *
     * @param data 业务数据
     */
    public Result(T data) {
        this.code = 200;
        this.msg = "操作成功";
        this.data = data;
    }

    /**
     * 构造完整结果
     *
     * @param code 状态码
     * @param msg  提示信息
     * @param data 业务数据
     */
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功（带数据）
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 结果对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功（无数据）
     *
     * @param <T> 数据类型
     * @return 结果对象
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功（自定义提示）
     *
     * @param msg 提示信息
     * @param <T> 数据类型
     * @return 结果对象
     */
    public static <T> Result<T> success(String msg) {
        return new Result<>(200, msg, null);
    }

    /**
     * 成功（自定义提示并携带数据）
     *
     * @param msg  提示信息
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 结果对象
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    /**
     * 失败
     *
     * @param msg 错误提示
     * @param <T> 数据类型
     * @return 结果对象
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    /**
     * 失败（自定义状态码）
     *
     * @param code 状态码
     * @param msg  错误提示
     * @param <T> 数据类型
     * @return 结果对象
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}
