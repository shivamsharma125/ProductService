package com.shivam.productservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.productservice.dtos.ProductDto;
import com.shivam.productservice.exceptions.UnAuthorizedUserException;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.services.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "selfProductService")
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Get all products - should return empty list")
    public void test_GetAllProducts_EmptyList_ReturnEmptyArray() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Get all products - valid request - should return 2 products")
    public void test_GetAllProducts_TwoProducts_ReturnList() throws Exception {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setTitle("Product 1");
        p1.setPrice(100.0);

        Product p2 = new Product();
        p2.setId(2L);
        p2.setTitle("Product 2");
        p2.setPrice(200.0);

        when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Product 1")))
                .andExpect(jsonPath("$[1].title", is("Product 2")));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Create product - valid request - should return created product")
    public void test_CreateProduct_ValidRequest_ReturnCreatedProduct() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setTitle("Laptop");
        productDto.setPrice(1000.0);
        productDto.setIsPremium(true);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setTitle("Laptop");
        savedProduct.setPrice(1000.0);
        savedProduct.setIsPremium(true);

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Laptop")))
                .andExpect(jsonPath("$.price", is(1000.0)))
                .andExpect(jsonPath("$.isPremium", is(true)));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService, times(1)).createProduct(productCaptor.capture());
        assertEquals("Laptop", productCaptor.getValue().getTitle());
    }

    @Test
    @DisplayName("Create product - missing title - should throw InvalidRequestException")
    public void test_CreateProduct_MissingTitle_ThrowsInvalidRequestException() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setPrice(500.0);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).createProduct(any());
    }

    @Test
    @DisplayName("Get product by id - valid id - should return product")
    public void test_GetProductById_ValidId_ReturnProduct() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Mobile");
        product.setPrice(200.0);

        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Mobile")))
                .andExpect(jsonPath("$.price", is(200.0)));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("Get product by id - invalid id - should return bad request")
    public void test_GetProductById_InvalidId_ReturnBadRequest() throws Exception {
        mockMvc.perform(get("/products/-1"))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).getProductById(anyLong());
    }

    @Test
    @DisplayName("Update product - valid request - should return updated product")
    public void test_UpdateProduct_ValidRequest_ReturnUpdatedProduct() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setTitle("Updated TV");
        productDto.setPrice(999.99);
        productDto.setIsPremium(false);

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setTitle("Updated TV");
        updatedProduct.setPrice(999.99);
        updatedProduct.setIsPremium(false);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated TV")))
                .andExpect(jsonPath("$.price", is(999.99)))
                .andExpect(jsonPath("$.isPremium", is(false)));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Delete product - valid id - should return success message")
    public void test_DeleteProduct_ValidId_ReturnSuccessMessage() throws Exception {
        when(productService.deleteProduct(1L)).thenReturn(true);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("product with id 1 is successfully deleted"));

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Delete product - deletion failed - should return internal server error")
    public void test_DeleteProduct_Failure_ReturnServerError() throws Exception {
        when(productService.deleteProduct(1L)).thenReturn(false);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Something went wrong. Please try again"));

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Get product details based on role - valid role")
    public void test_GetProductDetailsBasedOnRole_ValidRole() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Smartwatch");

        when(productService.getProductBasedOnUserRole(1L, 3L)).thenReturn(product);

        mockMvc.perform(get("/products/1/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Smartwatch")));
    }

    @Test
    @DisplayName("Get product details based on role - invalid id")
    public void test_GetProductDetailsBasedOnRole_InvalidId() throws Exception {
        mockMvc.perform(get("/products/-1/3L"))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).getProductBasedOnUserRole(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Get product details based on role - invalid role (user does not have access)")
    public void test_GetProductDetailsBasedOnRole_InvalidRole() throws Exception {
        when(productService.getProductBasedOnUserRole(1L, 3L))
                .thenThrow(new UnAuthorizedUserException("user is not authorized to query this product"));

        mockMvc.perform(get("/products/1/3"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("user is not authorized to query this product")));

        verify(productService, times(1)).getProductBasedOnUserRole(1L, 3L);
    }
}
