package com.javahunter.BatchProcessing.expection.custom;

public class ProductAlreadyExistException extends RuntimeException {

    public ProductAlreadyExistException(String message){
        super(message);
    }
    public ProductAlreadyExistException(String message,Throwable cause){
        super(message, cause);
    }
}
