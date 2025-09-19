package com.example.demo.aspect;

import com.example.demo.model.OperationLog;
import com.example.demo.repository.OperationLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class OperationLogAspect {
    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("within(@org.springframework.web.bind.annotation.RestController *) && execution(* *(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return joinPoint.proceed();
        HttpServletRequest request = attrs.getRequest();
        String method = request.getMethod();
        // 只记录POST/PUT/DELETE等操作
        if (!method.equalsIgnoreCase("POST") && !method.equalsIgnoreCase("PUT") && !method.equalsIgnoreCase("DELETE")) {
            return joinPoint.proceed();
        }
        OperationLog log = new OperationLog();
        log.setMethod(method);
        log.setUri(request.getRequestURI());
        // 参数序列化为JSON
        try {
            log.setParams(objectMapper.writeValueAsString(joinPoint.getArgs()));
        } catch (Exception e) {
            log.setParams("[参数序列化失败]");
        }
        // 优先获取真实IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        log.setIp(ip);
        log.setTime(LocalDateTime.now());
        // 获取用户名（假设已通过Spring Security设置）
        java.security.Principal principal = request.getUserPrincipal();
        log.setUsername(principal != null ? principal.getName() : "anonymous");
        Object result = null;
        try {
            result = joinPoint.proceed();
            log.setResult("success");
        } catch (Throwable ex) {
            log.setResult("fail: " + ex.getMessage());
            operationLogRepository.save(log);
            throw ex;
        }
        operationLogRepository.save(log);
        return result;
    }
}
