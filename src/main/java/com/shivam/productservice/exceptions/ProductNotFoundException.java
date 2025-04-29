package com.shivam.productservice.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(long productId) {
        super("Product with id " + productId + " does not exist.");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
