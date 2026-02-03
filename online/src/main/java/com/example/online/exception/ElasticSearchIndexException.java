package com.example.online.exception;

public class ElasticSearchIndexException extends RuntimeException{
    public ElasticSearchIndexException(String message){ super(message);}
    public ElasticSearchIndexException(String message, Throwable cause){
        super(message, cause);
    }
}
