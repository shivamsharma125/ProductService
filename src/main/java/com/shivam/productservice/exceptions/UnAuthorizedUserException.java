package com.shivam.productservice.exceptions;

public class UnAuthorizedUserException extends RuntimeException {
    public UnAuthorizedUserException(String message){
        super(message);
    }
}
