package com.amazonaws.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class DefaultExceptionHandler {

    /**缺少必要的参数*/
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public Result missingParameterHandler(HttpServletRequest request, MissingServletRequestParameterException e) {
        this.logError(request,e);
        return Result.fail(ExceptionEnum.PARAM_MISSING);
    }

    /**参数类型不匹配*/
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public Result methodArgumentTypeMismatchException(HttpServletRequest request,MethodArgumentTypeMismatchException e) {
        this.logError(request,e);
        return Result.fail(ExceptionEnum.PARAM_TYPE_MISMATCH);
    }

    /**不支持的请求方法*/
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        this.logError(request,e);
        return Result.fail(ExceptionEnum.HTTP_REQUEST_METHOD_NOT_SUPPORTED_ERROR);
    }

    /**参数错误*/
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result illegalArgumentException(HttpServletRequest request,IllegalArgumentException e) {
        this.logError(request,e);
        return Result.fail(ExceptionEnum.SERVER_ERROR_PRARM);
    }

    /**业务异常处理*/
    @ExceptionHandler(value = BusinessException.class)
    public Result businessException(HttpServletRequest request, BusinessException e) {
        log.error("path:{}, queryParam:{},errorCode:{} message:{}", request.getRequestURI(), request.getQueryString(),
                e.getCode(),e.getMessage(), e);
        Result res = new Result();
        res.setCode(e.getCode());
        res.setMsg(e.getMessage());
        return res;
    }

    /**其他异常统一处理*/
    @ExceptionHandler(value = Exception.class)
    public Result exception(HttpServletRequest request, Exception e) {
        this.logError(request,e);
        return Result.fail(ExceptionEnum.SERVER_ERROR);
    }

    /**
     * 记录错误日志
     */
    private void logError(HttpServletRequest request, Exception e){
        log.error("path:{}, queryParam:{}, errorMessage:{}", request.getRequestURI(), request.getQueryString(), e.getMessage(), e);
    }
}