package com.example.demo.model;

public enum ErrorCode {
    SUCCESS(0, "成功"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数错误"),
    VALIDATION_ERROR(422, "参数校验失败"),
    BUSINESS_ERROR(460, "业务异常"),
    SERVER_ERROR(500, "服务器内部错误");

    private final int code;
    private final String defaultMsg;

    ErrorCode(int code, String defaultMsg) {
        this.code = code;
        this.defaultMsg = defaultMsg;
    }

    public int getCode() { return code; }
    public String getDefaultMsg() { return defaultMsg; }
}
