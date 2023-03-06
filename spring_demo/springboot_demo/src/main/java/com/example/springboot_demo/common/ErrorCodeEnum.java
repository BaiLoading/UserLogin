package com.example.springboot_demo.common;

public enum ErrorCodeEnum {
    /**
     * 0-SUCCESS 成功
     * 1-Failed 失败
     */
    SUCCESS("0", "success"),
    FAILED("1", "failed");
    private String code;
    private String desc;

    ErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}