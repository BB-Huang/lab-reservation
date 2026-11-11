package asia.bbtlry.lab.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 4xxxx：客户端错误
    INVALID_PARAM(40001, "参数错误"),
    USERNAME_EXISTS(40002, "用户名已存在"),
    UNAUTHORIZED(40101, "未登录或 token 无效"),
    FORBIDDEN(40301, "无权访问该资源"),
    NOT_FOUND(40401, "资源不存在"),
    CONFLICT(40901, "该时段已被预约"),

    // 5xxxx：服务端错误
    INTERNAL_ERROR(50001, "服务器内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}