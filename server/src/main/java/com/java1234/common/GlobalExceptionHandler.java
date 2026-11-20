package com.java1234.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * <p>统一捕获控制器层抛出的异常，并转换为规范的 Result 响应结构。</p>
 *
 * @author Java1234
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     *
     * @param e 业务异常
     * @return 统一结果
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 参数校验异常处理（RequestBody 校验失败）
     *
     * @param e 校验异常
     * @return 统一结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("；"));
        return Result.error(msg);
    }

    /**
     * 参数绑定异常处理
     *
     * @param e 绑定异常
     * @return 统一结果
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("；"));
        return Result.error(msg);
    }

    /**
     * 上传文件超限异常处理
     *
     * @param e 上传异常
     * @return 统一结果
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadException(MaxUploadSizeExceededException e) {
        return Result.error("上传文件过大，单个文件不能超过 10MB");
    }

    /**
     * 上传缺少文件部件异常处理
     *
     * @param e 上传异常
     * @return 统一结果
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public Result<Void> handleMissingPartException(MissingServletRequestPartException e) {
        log.warn("缺少上传文件：{}", e.getRequestPartName());
        return Result.error("请选择要上传的文件");
    }

    /**
     * 非 multipart 请求异常处理
     *
     * @param e 上传异常
     * @return 统一结果
     */
    @ExceptionHandler(MultipartException.class)
    public Result<Void> handleMultipartException(MultipartException e) {
        log.warn("文件上传请求格式错误：{}", e.getMessage());
        return Result.error("文件上传请求格式不正确，请使用 multipart/form-data 提交");
    }

    /**
     * 参数类型不匹配异常处理（如路径变量传入非数字 ID）
     *
     * @param e 类型不匹配异常
     * @return 统一结果
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配：{}={}", e.getName(), e.getValue());
        return Result.error("参数格式不正确：" + e.getName());
    }

    /**
     * 请求内容类型不支持异常处理
     *
     * @param e 异常
     * @return 统一结果
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result<Void> handleMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.warn("不支持的请求内容类型：{}", e.getContentType());
        return Result.error("请求内容格式不正确，请检查请求头与请求体");
    }

    /**
     * 缺少必填请求参数异常处理
     *
     * @param e 异常
     * @return 统一结果
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParamException(MissingServletRequestParameterException e) {
        log.warn("缺少必填参数：{}", e.getParameterName());
        return Result.error("缺少必填参数：" + e.getParameterName());
    }

    /**
     * 权限不足异常处理
     *
     * @param e 权限异常
     * @return 统一结果
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(403, "没有操作权限");
    }

    /**
     * 认证异常处理
     *
     * @param e 认证异常
     * @return 统一结果
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        return Result.error(401, "登录状态已失效，请重新登录");
    }

    /**
     * 兜底异常处理
     *
     * @param e 异常对象
     * @return 统一结果
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统繁忙，请稍后重试");
    }
}
