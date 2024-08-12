package com.shivam.productservice.services;

import com.shivam.productservice.dtos.ResponseDto;
import com.shivam.productservice.models.Product;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product replaceProduct(Product product);
    Product updateProduct(Product product);
    Product getProductById(Long id);
    List<Product> getAllProducts();
    ResponseEntity<ResponseDto> deleteProduct(Long id);
}
