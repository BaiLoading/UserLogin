package com.example.springboot_demo.exception;


import com.example.springboot_demo.common.R;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;



@RestControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(ParamIllegalException.class)
    public R hasParseException(ParamIllegalException e) {
        return R.error("开始日期应当早于截止日期");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R hasValidException(MethodArgumentNotValidException e) {
        return R.error(e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining()));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public R hasParseException(DateTimeParseException e) {
        return R.error("日期格式错误，正确日期格式应为yyyy-MM-dd，例如2021-03-23");
    }
}