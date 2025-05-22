package com.shivam.productservice.controllers;

import com.shivam.productservice.dtos.ProductDto;
import com.shivam.productservice.exceptions.InvalidProductIdException;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductControllerTest {
    @Autowired
    private ProductController productController;

    @MockBean
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Test
    @DisplayName("getProductById() with id=4 will run successfully")
    void test_GetProductById_RunsSuccessfully() {
        // Arrange
        Product product = new Product();
        product.setId(4L);
        product.setTitle("IPhone");
        when(productService.getProductById(4L)).thenReturn(product);

        // Act
        ResponseEntity<ProductDto> response = productController.getProductById(4L);

        // Assert
        assert(response != null);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4L, response.getBody().getId());
        assertEquals("IPhone", response.getBody().getTitle());
        verify(productService, times(1)).getProductById(4L);
    }

    @Test
    void test_GetProductsById_CalledWithInvalidId_ResultsInInvalidProductIdException() {
        // Act & Assert
        Exception exception = assertThrows(InvalidProductIdException.class,
                () -> productController.getProductById(-1L));

        assertEquals("product id -1 is invalid", exception.getMessage());
    }

    @Test
    void test_GetProductById_CalledWithCorrectArguments_RunsSuccessfully() {
        // Arrange
        Product product = new Product();
        product.setId(4L);
        product.setTitle("IPhone");
        when(productService.getProductById(4L)).thenReturn(product);

        // Act
        productController.getProductById(4L);

        // Assert
        verify(productService).getProductById(idCaptor.capture());
        assertEquals(4L,idCaptor.getValue());
    }
}