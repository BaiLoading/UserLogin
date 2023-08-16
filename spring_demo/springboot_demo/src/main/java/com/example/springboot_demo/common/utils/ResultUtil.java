package com.example.springboot_demo.common.utils;

import com.example.springboot_demo.common.enums.ResultCodeEnum;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ResultUtil<T> implements Serializable {

    private static final long serialVersionUID = 3436477890959388499L;

    /**
     * 返回操作码（默认为正常）
     */
    private Integer code = ResultCodeEnum.SUCCESS.code();

    /**
     * 返回数据信息
     */
    private T data;

    /**
     * 返回正确消息信息
     */
    private String msg = "";

    /**
     * 返回错误的消息信息
     */
    private String error = "";

    public static ResultUtil success = new ResultUtil();
    public static ResultUtil fail = new ResultUtil().code(ResultCodeEnum.FAIL.code());

    // 构造方法
    public static ResultUtil build() {
        return new ResultUtil();
    }

    public static <T> ResultUtil<T> build(T data) {
        return new ResultUtil().data(data);
    }

    public static <T> ResultUtil<T> buildSuccess(T data){
        return new ResultUtil().data(data);
    }

    // 赋值方法
    public ResultUtil data(T data) {
        this.data = data;
        return this;
    }

    public ResultUtil code(Integer code) {
        this.code = code;
        return this;
    }

    public ResultUtil msg(String msg) {
        this.msg = msg;
        return this;
    }

    public ResultUtil error(String error) {
        this.error = error;
        return this;
    }

}
