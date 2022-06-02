package com.eventu.exception;

public class BusinessException extends Exception{
    public BusinessException(String message, Throwable err){
        super(message, err);
    }
}
