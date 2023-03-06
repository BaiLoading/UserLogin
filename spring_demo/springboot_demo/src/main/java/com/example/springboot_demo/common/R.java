package com.example.springboot_demo.common;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;


@Data
public class R {
    private String code;
    private String message;
    private HashMap<String, Object> data;


    public R(String code, String message) {
        this.code = code;
        this.message = message;
        data = new HashMap<>();
    }


    public static R error() {
        return new R(ErrorCodeEnum.FAILED.getCode(), ErrorCodeEnum.FAILED.getDesc());
    }
    public static R error(String message) {
        return new R(ErrorCodeEnum.FAILED.getCode(), message);
    }

    public static R ok() {
        return new R(ErrorCodeEnum.SUCCESS.getCode(), ErrorCodeEnum.SUCCESS.getDesc());
    }

    public R put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}