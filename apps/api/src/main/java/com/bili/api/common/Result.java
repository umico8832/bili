package com.bili.api.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一 API 响应包装
 *
 * <pre>
 * {
 *   "code": 200,
 *   "msg":  "ok",
 *   "data": { ... }
 * }
 * </pre>
 */
@Data
public class Result<T> implements Serializable {

    private int code;
    private String msg;
    private T data;

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 成功（含数据） */
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "ok", data);
    }

    /** 成功（无数据） */
    public static <T> Result<T> ok() {
        return new Result<>(200, "ok", null);
    }

    /** 客户端错误 */
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /** 服务端错误（500） */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}
