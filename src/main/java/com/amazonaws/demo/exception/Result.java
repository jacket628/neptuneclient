package com.amazonaws.demo.exception;

import lombok.Data;

/**
 * Standard Result
 */
@Data
public class Result {
    private int code;
    private String msg;
    private Object obj;

    public Result() {
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result success(Object obj) {
        ExceptionEnum exceptionEnum = ExceptionEnum.SUCCESS;
        Result result = new Result();
        result.setCode(exceptionEnum.getKey());
        result.setMsg(exceptionEnum.getValue());
        result.setObj(obj);
        return result;
    }

    public static Result fail(ExceptionEnum exceptionEnum) {
        Result result = new Result();
        result.setCode(exceptionEnum.getKey());
        result.setMsg(exceptionEnum.getValue());
        result.setObj(null);
        return result;
    }

    public Result(int code, String msg, Object obj) {
        this.code = code;
        this.msg = msg;
        this.obj = obj;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
