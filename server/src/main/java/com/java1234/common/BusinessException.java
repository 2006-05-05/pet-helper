package com.java1234.common;

/**
 * 自定义业务异常类
 *
 * <p>用于抛出可预期的业务错误，由全局异常处理器统一转换为 Result 响应。</p>
 *
 * @author Java1234
 */
public class BusinessException extends RuntimeException {

    /**
     * 构造业务异常
     *
     * @param message 异常提示信息
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * 构造业务异常（自定义状态码）
     *
     * @param code    状态码
     * @param message 异常提示信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
    }
}
