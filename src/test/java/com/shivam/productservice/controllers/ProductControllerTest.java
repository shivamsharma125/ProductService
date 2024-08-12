package com.shivam.productservice.controllers;

import com.shivam.productservice.models.Product;
import com.shivam.productservice.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductControllerTest {
    @Autowired
    private ProductController productController;
    @MockBean
    private ProductService productService;
    @Test
    void testGetProductByIdValidCase() {
        // Arrange
        Product product = new Product();
        when(productService.getProductById(1L))
                .thenReturn(product);
        // Act
        Product returnedProduct = productController.getProductById(1L);
        // Assert
        assertEquals(product, returnedProduct);
    }
}