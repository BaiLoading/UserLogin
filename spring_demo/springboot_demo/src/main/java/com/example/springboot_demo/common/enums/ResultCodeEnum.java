package com.example.springboot_demo.common.enums;

public enum ResultCodeEnum {

    SUCCESS(0, "登录成功"),

    /**
     * 调用失败
     */
    FAIL(-1, "登录失败");


    /**
     * 返回编码
     */
    private Integer code;

    /**
     * 编码对应的消息
     */
    private String msg;

    ResultCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取枚举类型的编码值
     */
    public Integer code() {
        return this.code;
    }

    /**
     * 获取枚举类型的编码含义
     */
    public String msg() {
        return this.msg;
    }

    /**
     * 根据枚举名称--获取枚举编码
     */
    public static Integer getCode(String name) {
        for (ResultCodeEnum resultCode : ResultCodeEnum.values()) {
            if (resultCode.name().equals(name)) {
                return resultCode.code;
            }
        }
        return null;
    }

}
