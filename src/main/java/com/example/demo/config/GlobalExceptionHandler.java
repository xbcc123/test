package com.example.demo.config;

import com.example.demo.model.ApiResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResponse<?> handleException(Exception e) {
        // 这里可根据异常类型自定义code和msg
        return ApiResponse.error(500, e.getMessage());
    }
}

