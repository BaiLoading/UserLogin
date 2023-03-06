package com.example.springboot_demo.exception;

public class ParamIllegalException extends Exception{
    private String message;
    public ParamIllegalException(String message){
        super(message);
        this.message = message;
    }

    public ParamIllegalException() {
        super();
    }
}
