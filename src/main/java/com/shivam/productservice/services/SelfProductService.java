package com.shivam.productservice.services;

import com.shivam.productservice.dtos.UserDto;
import com.shivam.productservice.exceptions.ProductNotFoundException;
import com.shivam.productservice.exceptions.UnAuthorizedUserException;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.repositories.ProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service("selfProductService")
@Primary
public class SelfProductService implements ProductService {
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String,Object> redisTemplate;

    public SelfProductService(ProductRepository productRepository,
                              RestTemplate restTemplate,
                              RedisTemplate<String,Object> redisTemplate){
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        redisTemplate.opsForHash().put("PRODUCTS", savedProduct.getId(), savedProduct);
        return savedProduct;
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        productRepository.findById(product.getId())
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Product savedProduct = productRepository.save(product);

        redisTemplate.opsForHash().put("PRODUCTS", savedProduct.getId(), savedProduct);

        return savedProduct;
    }

    @Override
    public Product getProductBasedOnUserRole(Long productId, Long userId) {
        String url = "http://UserService/users/{userId}";
        ResponseEntity<UserDto> response = restTemplate.getForEntity(url, UserDto.class, userId);
        if (response.getBody() == null)
            throw new RuntimeException("Something went wrong while getting user details");

        UserDto userDto = response.getBody();
        if (userDto.getRoles().stream().noneMatch(roleDto -> roleDto.getName().equals("CUSTOMER")))
            throw new UnAuthorizedUserException("user is not authorized to query product details");

        return getProductById(productId);
    }

    @Override
    public Product getProductById(Long productId) {
        Product product = (Product) redisTemplate.opsForHash().get("PRODUCTS", productId);

        if (product != null) return product;

        product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        redisTemplate.opsForHash().put("PRODUCTS", product.getId(), product);

        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Boolean deleteProduct(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        productRepository.deleteById(productId);

        boolean isDeleted = !productRepository.existsById(productId);

        if (isDeleted) {
            // Delete the product from the cache as well
            redisTemplate.opsForHash().delete("PRODUCTS", productId);
        }

        return isDeleted;
    }
}
