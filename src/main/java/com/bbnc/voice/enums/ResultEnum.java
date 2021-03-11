package com.bbnc.voice.enums;

public enum ResultEnum {

    SUCCESS(0, "请求成功"),

    FAIL(1, "请求失败"),

    LOGIN_FAIL(2, "用户名或密码错误"),

    TOKEN_INVALID(3, "Token不存在或者过期"),

    FILE_UPLOAD_ERR(4, "文件处理错误"),

    PERMISSION_ERR(5, "你没有该权限");

    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
