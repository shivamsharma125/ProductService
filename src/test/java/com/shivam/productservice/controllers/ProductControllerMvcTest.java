package com.shivam.productservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.productservice.dtos.ProductDto;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "selfProductService")
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void test_GetAllProducts_RunsSuccessfully() throws Exception {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setTitle("IPhone");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setTitle("Samsung");
        product2.setPrice(40000.00);

        List<Product> products = List.of(product1,product2);

        when(productService.getAllProducts()).thenReturn(products);

        ProductDto productDto1 = new ProductDto();
        productDto1.setId(1L);
        productDto1.setTitle("IPhone");

        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2L);
        productDto2.setTitle("Samsung");
        productDto2.setPrice(40000.00);

        List<ProductDto> productDtos = List.of(productDto1,productDto2);

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(productDtos)))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("IPhone"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Samsung"))
                .andExpect(jsonPath("$[1].length()").value(3));
    }

    @Test
    void test_CreateProduct_RunsSuccessfully() throws Exception {
        // Arrange
        ProductDto productDto = new ProductDto();
        productDto.setId(5L);
        productDto.setTitle("IPhone");

        Product product = new Product();
        product.setId(5L);
        product.setTitle("IPhone");

        when(productService.createProduct(any(Product.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(post("/products")
                        .content(objectMapper.writeValueAsString(productDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(productDto)))
                .andExpect(jsonPath("$.id").value(productDto.getId()))
                .andExpect(jsonPath("$.title").value(productDto.getTitle()));
    }
}
