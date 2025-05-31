package com.shivam.productservice.services;

import com.shivam.productservice.dtos.RoleDto;
import com.shivam.productservice.dtos.UserDto;
import com.shivam.productservice.exceptions.ProductNotFoundException;
import com.shivam.productservice.exceptions.UnAuthorizedUserException;
import com.shivam.productservice.models.Category;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SelfProductServiceTest {

    private ProductRepository productRepository;
    private RestTemplate restTemplate;
    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, Object, Object> hashOperations;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        restTemplate = mock(RestTemplate.class);
        redisTemplate = mock(RedisTemplate.class);
        hashOperations = mock(HashOperations.class);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        productService = new SelfProductService(productRepository, restTemplate, redisTemplate);
    }

    @Test
    @DisplayName("Should return all products")
    void test_GetAllProducts_ProductsExist_ReturnsList() {
        Product product = createSampleProduct();
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Product");

        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should save product and store in cache")
    void test_CreateProduct_ValidProduct_SavesAndCaches() {
        Product product = createSampleProduct();
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);

        assertThat(result).isEqualTo(product);
        verify(productRepository, times(1)).save(product);
        verify(hashOperations, times(1)).put("PRODUCTS", product.getId(), product);
    }

    @Test
    @DisplayName("Should update product if exists")
    void test_UpdateProduct_ProductExists_UpdatesSuccessfully() {
        Product product = createSampleProduct();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.updateProduct(product.getId(), product);

        assertThat(result).isEqualTo(product);
        verify(productRepository).save(product);
        verify(hashOperations).put("PRODUCTS", product.getId(), product);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating non-existent product")
    void test_UpdateProduct_ProductNotExists_ThrowsException() {
        Product product = createSampleProduct();
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,() -> productService.updateProduct(product.getId(), product));
    }

    @Test
    @DisplayName("Should fetch product from cache if present")
    void test_GetProductById_ProductInCache_ReturnsProduct() {
        Product product = createSampleProduct();
        when(hashOperations.get("PRODUCTS", product.getId())).thenReturn(product);

        Product result = productService.getProductById(product.getId());

        assertThat(result).isEqualTo(product);
        verify(hashOperations).get("PRODUCTS", product.getId());
        verify(productRepository, times(0)).findById(any());
    }

    @Test
    @DisplayName("Should fetch from DB and cache if not in Redis")
    void test_GetProductById_NotInCache_FetchesFromDbAndCaches() {
        Product product = createSampleProduct();
        when(hashOperations.get("PRODUCTS", product.getId())).thenReturn(null);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Product result = productService.getProductById(product.getId());

        assertThat(result).isEqualTo(product);
        verify(productRepository).findById(product.getId());
        verify(hashOperations).put("PRODUCTS", product.getId(), product);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException if not in DB or cache")
    void test_GetProductById_NotFoundAnywhere_ThrowsException() {
        when(hashOperations.get("PRODUCTS", 1L)).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,() -> productService.getProductById(1L));
    }

    @Test
    @DisplayName("Should delete product from DB and cache")
    void test_DeleteProduct_Exists_DeletesSuccessfully() {
        Product product = createSampleProduct();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.existsById(product.getId())).thenReturn(false);

        Boolean result = productService.deleteProduct(product.getId());

        assertTrue(result);
        verify(productRepository).deleteById(product.getId());
        verify(hashOperations).delete("PRODUCTS", product.getId());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException on delete if product doesn't exist")
    void test_DeleteProduct_NotFound_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,() -> productService.deleteProduct(1L));
    }

    @Test
    @DisplayName("Should return product for authorized customer")
    void test_GetProductBasedOnUserRole_CustomerRole_ReturnsProduct() {
        Product product = createSampleProduct();
        RoleDto roleDto = new RoleDto();
        roleDto.setName("CUSTOMER");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setRoles(List.of(roleDto));

        when(restTemplate.getForEntity("http://UserService/users/{userId}", UserDto.class, 1L))
                .thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));
        when(hashOperations.get("PRODUCTS", product.getId())).thenReturn(product);

        Product result = productService.getProductBasedOnUserRole(product.getId(), 1L);

        assertThat(result).isEqualTo(product);
        verify(restTemplate).getForEntity("http://UserService/users/{userId}", UserDto.class, 1L);
        verify(productRepository,never()).findById(1L);
    }

    @Test
    @DisplayName("Should throw UnAuthorizedUserException for non-customer role")
    void test_GetProductBasedOnUserRole_NonCustomer_ThrowsException() {
        RoleDto roleDto = new RoleDto();
        roleDto.setName("SUPPORT_AGENT");
        UserDto userDto = new UserDto();
        userDto.setRoles(List.of(roleDto));

        when(restTemplate.getForEntity("http://UserService/users/{userId}", UserDto.class, 1L))
                .thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));

        assertThrows(UnAuthorizedUserException.class,() -> productService.getProductBasedOnUserRole(1L, 1L));
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");
        product.setDescription("Sample Description");
        product.setPrice(100.0);
        product.setImageUrl("http://image.url");

        Category category = new Category();
        category.setId(1L);
        category.setTitle("Electronics");
        product.setCategory(category);

        return product;
    }
}
