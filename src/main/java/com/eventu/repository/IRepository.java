package com.eventu.repository;

import com.mongodb.MongoWriteException;

public interface IRepository {
    default  String createErrorMessage(Throwable ex, String message){
        if(((MongoWriteException)ex).getCode() == 11000){
            return message;
        }
        return "Unknown error";
    }
}
