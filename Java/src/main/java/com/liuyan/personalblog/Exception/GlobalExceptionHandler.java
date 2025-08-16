//package com.liuyan.personalblog.Exception;
//
//import com.liuyan.personalblog.POJO.Result;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(BusinessException.class)
//    public Result handleBusinessException(BusinessException ex) {
//        return Result.error(ex.getMessage());
//    }
//
//    @ExceptionHandler(Exception.class)
//    public Result handleOtherException(Exception ex) {
//        return Result.error( "系统内部错误: " + ex.getMessage());
//    }
//}
