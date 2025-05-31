package com.shivam.productservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shivam.productservice.models.Category;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.services.SearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Search products - valid query - should return matched products")
    public void test_SearchProducts_ValidQuery_ReturnMatchedProducts() throws Exception {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setTitle("iPhone");

        Product p2 = new Product();
        p2.setId(2L);
        p2.setTitle("iPad");

        Page<Product> productPage = new PageImpl<>(List.of(p1, p2));

        when(searchService.searchProducts("apple", null, null, null, null, 0, 10, "title", "asc"))
                .thenReturn(productPage);

        mockMvc.perform(get("/search")
                        .param("query", "apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is("iPhone")))
                .andExpect(jsonPath("$.content[1].title", is("iPad")));

        verify(searchService, times(1)).searchProducts("apple", null, null, null, null, 0, 10, "title", "asc");
    }

    @Test
    @DisplayName("Search products - missing query - should throw InvalidRequestException")
    public void test_SearchProducts_EmptyQuery_ThrowInvalidRequestException() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("query cannot be empty")));

        verify(searchService, times(0)).searchProducts(any(), any(), any(), any(), any(), anyInt(), anyInt(), any(), any());
    }

    @Test
    @DisplayName("Search products - filter by category and premium - should return filtered products")
    public void test_SearchProducts_FilterByCategoryAndPremium_ReturnFilteredResults() throws Exception {
        Product product = new Product();
        product.setId(3L);
        product.setTitle("MacBook");
        product.setIsPremium(true);
        Category category = new Category();
        category.setTitle("Laptops");
        product.setCategory(category);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(searchService.searchProducts("mac", "Laptops", null, null, true, 0, 10, "title", "asc"))
                .thenReturn(productPage);

        mockMvc.perform(get("/search")
                        .param("query", "mac")
                        .param("category", "Laptops")
                        .param("isPremium", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("MacBook")))
                .andExpect(jsonPath("$.content[0].isPremium", is(true)));

        ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
        verify(searchService, times(1)).searchProducts(queryCaptor.capture(), eq("Laptops"), any(), any(), eq(true), eq(0), eq(10), eq("title"), eq("asc"));
        assertEquals("mac", queryCaptor.getValue());
    }

    @Test
    @DisplayName("Search products - filter by price range - should return matched products")
    public void test_SearchProducts_FilterByPriceRange_ReturnFilteredResults() throws Exception {
        Product product = new Product();
        product.setId(4L);
        product.setTitle("Monitor");
        product.setPrice(250.0);

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(searchService.searchProducts("monitor", null, 200.0, 300.0, null, 0, 10, "title", "asc"))
                .thenReturn(productPage);

        mockMvc.perform(get("/search")
                        .param("query", "monitor")
                        .param("minPrice", "200.0")
                        .param("maxPrice", "300.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("Monitor")))
                .andExpect(jsonPath("$.content[0].price", is(250.0)));

        verify(searchService, times(1)).searchProducts("monitor", null, 200.0, 300.0, null, 0, 10, "title", "asc");
    }

    @Test
    @DisplayName("Search products - no matches - should return empty content list")
    public void test_SearchProducts_NoResults_ReturnEmptyList() throws Exception {
        when(searchService.searchProducts("xyz", null, null, null, null, 0, 10, "title", "asc"))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/search")
                        .param("query", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(searchService, times(1)).searchProducts("xyz", null, null, null, null, 0, 10, "title", "asc");
    }
}
