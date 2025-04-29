package com.shivam.productservice.services;

import com.shivam.productservice.dtos.ResponseDto;
import com.shivam.productservice.exceptions.ProductNotFoundException;
import com.shivam.productservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts(Long userId);
    Product createProduct(Product product);
    Product getProductById(Long productId) throws ProductNotFoundException;
    Product updateProduct(Long productId, Product product);
    Boolean deleteProduct(Long productId);
    Page<Product> searchProduct(int pageNumber, int pageSize);
    Page<Product> searchProduct(int pageNumber, int pageSize, String sortingParam);
}
