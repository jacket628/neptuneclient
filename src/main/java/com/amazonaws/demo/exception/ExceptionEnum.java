package com.amazonaws.demo.exception;

public enum ExceptionEnum implements IEnum{
    SUCCESS(0,"成功"),

    SERVER_ERROR(10000,"服务器异常"),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED_ERROR(10001,"请求方法不支持异常"),
    SERVER_ERROR_PRARM(10002,"服务器参数异常"),
    PARAM_MISSING(10003,"参数丢失"),
    PARAM_TYPE_MISMATCH(10004,"参数类型不匹配"),
    EMAIL_ERROR(10005,"邮箱错误或重复"),

    DB_UPDATE_ERROR(20001,"数据库更新失败"),
    DB_DELETE_ERROR(20002,"数据库删除失败"),
    DB_ADD_ERROR(20003,"数据库添加失败"),
    DB_GET_ERROR(20004,"数据库获取失败"),

    LOGIN_ERROR(30001,"登录失败"),
    REGISTER_ERROR(30002,"注册失败"),
    TOKEN_ERROR(30003,"Token验证失败，请重新登录");


    private int code;
    private String msg;

    ExceptionEnum( int code,String msg) {
        this.msg = msg;
        this.code = code;
    }

    @Override
    public Integer getKey() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.msg;
    }

}
