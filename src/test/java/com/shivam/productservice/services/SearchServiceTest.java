package com.shivam.productservice.services;

import com.shivam.productservice.models.Category;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Captor
    private ArgumentCaptor<Specification<Product>> specCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private Product product1;
    private Product product2;
    private Page<Product> mockPage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Category category = new Category();
        category.setTitle("Electronics");

        product1 = new Product();
        product1.setId(1L);
        product1.setTitle("Smartphone");
        product1.setDescription("Latest 5G phone");
        product1.setPrice(699.0);
        product1.setImageUrl("image1.jpg");
        product1.setCategory(category);
        product1.setIsPremium(true);

        product2 = new Product();
        product2.setId(2L);
        product2.setTitle("Smartwatch");
        product2.setDescription("Fitness Watch");
        product2.setPrice(199.0);
        product2.setImageUrl("image2.jpg");
        product2.setCategory(category);
        product2.setIsPremium(false);

        mockPage = new PageImpl<>(List.of(product1, product2));
    }

    @Test
    @DisplayName("Search products with all filters applied should return matching results with correct pagination and sorting")
    void test_SearchProducts_CalledWithAllFilters_ReturnMatchingProducts() {
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(),
                ArgumentMatchers.<Pageable>any())).thenReturn(new PageImpl<>(List.of(product1)));

        Page<Product> result = searchService.searchProducts(
                "Smart", "Electronics", 100.0, 700.0,
                true, 0, 10, "price", "asc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Smartphone", result.getContent().get(0).getTitle());

        verify(productRepository, times(1)).findAll(specCaptor.capture(), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(10, capturedPageable.getPageSize());
        assertEquals(Sort.Direction.ASC, capturedPageable.getSort().getOrderFor("price").getDirection());
    }

    @Test
    @DisplayName("Search products with null filters should return all products")
    void test_SearchProducts_CalledWithNoFilters_ReturnAllProducts() {
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(),
                ArgumentMatchers.<Pageable>any())).thenReturn(mockPage);

        Page<Product> result = searchService.searchProducts(
                null, null, null, null,
                null, 0, 5, "title", "desc");

        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        verify(productRepository, times(1)).findAll(ArgumentMatchers.<Specification<Product>>any(),
                ArgumentMatchers.<Pageable>any());
    }

    @Test
    @DisplayName("Search products with invalid direction should fallback to descending")
    void test_SearchProducts_CalledWithInvalidSortDirection_UsesDescendingSort() {
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(),
                ArgumentMatchers.<Pageable>any())).thenReturn(mockPage);

        Page<Product> result = searchService.searchProducts(
                "Smart", null, null, null,
                null, 0, 5, "price", "INVALID");

        assertEquals(2, result.getContent().size());

        verify(productRepository, times(1)).findAll(specCaptor.capture(), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();

        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("price").getDirection());
    }

    @Test
    @DisplayName("Search products with category filter should return matching category products")
    void test_SearchProducts_CalledWithCategoryFilter_ReturnFilteredProducts() {
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(),
                ArgumentMatchers.<Pageable>any())).thenReturn(mockPage);

        Page<Product> result = searchService.searchProducts(
                "", "Electronics", null, null,
                null, 1, 3, "title", "asc");

        assertEquals(2, result.getContent().size());
        verify(productRepository).findAll(specCaptor.capture(), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(1, pageable.getPageNumber());
        assertEquals(3, pageable.getPageSize());
    }

    @Test
    @DisplayName("Search products should return empty page when no result matches")
    void test_SearchProducts_CalledWithNoMatchingData_ReturnEmptyPage() {
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(),
                ArgumentMatchers.<Pageable>any())).thenReturn(emptyPage);

        Page<Product> result = searchService.searchProducts(
                "NonExistent", "Fashion", 2000.0, 3000.0,
                false, 0, 10, "price", "asc");

        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll(ArgumentMatchers.<Specification<Product>>any(),
                ArgumentMatchers.<Pageable>any());
    }

    @Test
    @DisplayName("Search products with premium filter should return only premium products")
    void test_SearchProducts_CalledWithIsPremiumTrue_ReturnOnlyPremium() {
        when(productRepository.findAll(ArgumentMatchers.<Specification<Product>>any(),
                ArgumentMatchers.<Pageable>any())).thenReturn(new PageImpl<>(List.of(product1)));

        Page<Product> result = searchService.searchProducts(
                null, null, null, null,
                true, 0, 10, "price", "asc");

        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().get(0).getIsPremium());

        verify(productRepository).findAll(ArgumentMatchers.<Specification<Product>>any(),
                ArgumentMatchers.<Pageable>any());
    }
}

