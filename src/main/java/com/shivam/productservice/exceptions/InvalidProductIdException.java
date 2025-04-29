package com.shivam.productservice.exceptions;

public class InvalidProductIdException extends RuntimeException {
    public InvalidProductIdException(Long productId) {
        super("product id " + productId  + " is invalid");
    }

    public InvalidProductIdException(String message) {
        super(message);
    }
}
