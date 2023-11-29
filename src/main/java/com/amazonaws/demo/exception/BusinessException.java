package com.amazonaws.demo.exception;

import lombok.Data;

/**
 * 自定义全局异常类
 *
 */
@Data
public class BusinessException extends RuntimeException {
    private Integer code;
    private String message;

    /**
     * 统一异常消息处理
     *
     * @param exceptionEnum 异常枚举值
     */
    public BusinessException(ExceptionEnum exceptionEnum) {
        this.message = exceptionEnum.getValue();
    }

    public BusinessException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

