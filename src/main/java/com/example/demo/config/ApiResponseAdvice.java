package com.example.demo.config;

import com.example.demo.model.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 排除 ResponseEntity 和 byte[] 返回值，避免包装视频流等二进制数据
        Class<?> paramType = returnType.getParameterType();
        if (org.springframework.http.ResponseEntity.class.isAssignableFrom(paramType)) return false;
        if (byte[].class.isAssignableFrom(paramType)) return false;
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  org.springframework.http.server.ServerHttpRequest request,
                                  org.springframework.http.server.ServerHttpResponse response) {
        // 已经是ApiResponse则不再包装
        if (body instanceof ApiResponse) {
            return body;
        }
        // 这里可根据实际情况决定是否包装
        return ApiResponse.success(body);
    }
}
