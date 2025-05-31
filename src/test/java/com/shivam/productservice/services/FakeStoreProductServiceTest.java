package com.shivam.productservice.services;

import com.shivam.productservice.clients.FakeStoreApiClient;
import com.shivam.productservice.dtos.FakeStoreProductDto;
import com.shivam.productservice.exceptions.ProductNotFoundException;
import com.shivam.productservice.models.Category;
import com.shivam.productservice.models.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FakeStoreProductServiceTest {

    private FakeStoreApiClient fakeStoreApiClient;
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, Object, Object> hashOperations;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        fakeStoreApiClient = mock(FakeStoreApiClient.class);
        redisTemplate = mock(RedisTemplate.class);
        hashOperations = mock(HashOperations.class);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        productService = new FakeStoreProductService(fakeStoreApiClient, redisTemplate);
    }

    @Test
    @DisplayName("Should return product list when products are available")
    void test_GetAllProducts_WhenProductsAvailable_ReturnProductList() {
        FakeStoreProductDto dto = new FakeStoreProductDto();
        dto.setId(1L);
        dto.setTitle("Test Product");
        dto.setPrice(10.0);
        dto.setCategory("Electronics");
        dto.setDescription("Test Description");
        dto.setImage("test.jpg");

        when(fakeStoreApiClient.getAllProducts()).thenReturn(List.of(dto));

        List<Product> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals("Test Product", products.get(0).getTitle());
        verify(fakeStoreApiClient, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Should return empty list when no products available")
    void test_GetAllProducts_WhenProductsNull_ThrowProductNotFoundException() {
        when(fakeStoreApiClient.getAllProducts()).thenReturn(new ArrayList<>());
        List<Product> products = productService.getAllProducts();

        assertEquals(0,products.size());
    }

    @Test
    @DisplayName("Should create product and store in cache")
    void test_CreateProduct_ValidInput_SaveAndReturnProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("New Product");
        product.setPrice(50.0);
        product.setDescription("Desc");
        product.setImageUrl("img.jpg");
        Category category = new Category();
        category.setTitle("Books");
        product.setCategory(category);

        FakeStoreProductDto dto = new FakeStoreProductDto();
        dto.setId(1L);
        dto.setTitle("New Product");
        dto.setPrice(50.0);
        dto.setDescription("Desc");
        dto.setImage("img.jpg");
        dto.setCategory("Books");

        when(fakeStoreApiClient.createProduct(any())).thenReturn(dto);

        Product created = productService.createProduct(product);

        assertEquals("New Product", created.getTitle());
        verify(fakeStoreApiClient, times(1)).createProduct(any());
        verify(hashOperations, times(1)).put("PRODUCTS", created.getId(), created);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(hashOperations).put(eq("PRODUCTS"), eq(1L), captor.capture());
        assertEquals("New Product", captor.getValue().getTitle());
    }

    @Test
    @DisplayName("Should return product from cache if available")
    void test_GetProductById_CacheHit_ReturnCachedProduct() throws ProductNotFoundException {
        Product cachedProduct = new Product();
        cachedProduct.setId(2L);
        cachedProduct.setTitle("Cached Product");

        when(hashOperations.get("PRODUCTS", 2L)).thenReturn(cachedProduct);

        Product result = productService.getProductById(2L);

        assertEquals("Cached Product", result.getTitle());
        verify(hashOperations, times(1)).get("PRODUCTS", 2L);
        verify(fakeStoreApiClient, never()).getProductById(any());
    }

    @Test
    @DisplayName("Should return product from API and store in cache when not found in cache")
    void test_GetProductById_CacheMiss_FetchFromApiAndCacheIt() throws ProductNotFoundException {
        when(hashOperations.get("PRODUCTS", 3L)).thenReturn(null);

        FakeStoreProductDto dto = new FakeStoreProductDto();
        dto.setId(3L);
        dto.setTitle("Fetched Product");
        dto.setPrice(25.0);
        dto.setCategory("Gadgets");
        dto.setDescription("Fetched Desc");
        dto.setImage("img.jpg");

        when(fakeStoreApiClient.getProductById(3L)).thenReturn(dto);

        Product result = productService.getProductById(3L);

        assertEquals("Fetched Product", result.getTitle());
        verify(fakeStoreApiClient, times(1)).getProductById(3L);
        verify(hashOperations, times(1)).put("PRODUCTS", 3L, result);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found in API")
    void test_GetProductById_ApiReturnsNull_ThrowProductNotFoundException() {
        when(hashOperations.get("PRODUCTS", 99L)).thenReturn(null);
        when(fakeStoreApiClient.getProductById(99L)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    @DisplayName("Should update product and store in cache")
    void test_UpdateProduct_ValidInput_ReturnUpdatedProduct() {
        Product updateProduct = new Product();
        updateProduct.setId(10L);
        updateProduct.setTitle("Updated Title");
        updateProduct.setPrice(99.0);
        updateProduct.setDescription("Updated Desc");
        updateProduct.setImageUrl("updated.jpg");
        Category category = new Category();
        category.setTitle("Updated Cat");
        updateProduct.setCategory(category);

        FakeStoreProductDto dto = new FakeStoreProductDto();
        dto.setId(10L);
        dto.setTitle("Updated Title");
        dto.setPrice(99.0);
        dto.setCategory("Updated Cat");
        dto.setDescription("Updated Desc");
        dto.setImage("updated.jpg");

        when(fakeStoreApiClient.updateProduct(eq(10L), any())).thenReturn(dto);

        Product result = productService.updateProduct(10L, updateProduct);

        assertEquals("Updated Title", result.getTitle());
        verify(fakeStoreApiClient, times(1)).updateProduct(eq(10L), any());
        verify(hashOperations, times(1)).put("PRODUCTS", result.getId(), result);
    }

    @Test
    @DisplayName("Should delete product and remove from cache")
    void test_DeleteProduct_ValidId_ProductDeletedFromCache() {
        FakeStoreProductDto responseDto = new FakeStoreProductDto();
        responseDto.setId(11L);

        when(fakeStoreApiClient.deleteProduct(11L)).thenReturn(responseDto);

        Boolean result = productService.deleteProduct(11L);

        assertTrue(result);
        verify(fakeStoreApiClient, times(1)).deleteProduct(11L);
        verify(hashOperations, times(1)).delete("PRODUCTS", 11L);
    }

    @Test
    @DisplayName("Should return false when deleteProduct API returns null")
    void test_DeleteProduct_ApiReturnsNull_ReturnFalse() {
        when(fakeStoreApiClient.deleteProduct(12L)).thenReturn(null);

        Boolean result = productService.deleteProduct(12L);

        assertFalse(result);
        verify(fakeStoreApiClient, times(1)).deleteProduct(12L);
        verify(hashOperations, never()).delete(any(), any());
    }
}
