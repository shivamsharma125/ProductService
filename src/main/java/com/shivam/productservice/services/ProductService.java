package com.shivam.productservice.services;

import com.shivam.productservice.exceptions.ProductNotFoundException;
import com.shivam.productservice.models.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product createProduct(Product product);
    Product getProductById(Long productId) throws ProductNotFoundException;
    Product getProductBasedOnUserRole(Long productId, Long userId);
    Product updateProduct(Long productId, Product product);
    Boolean deleteProduct(Long productId);
}
