package com.shivam.productservice.exceptions;

public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException(String message){
        super(message);
    }
}
