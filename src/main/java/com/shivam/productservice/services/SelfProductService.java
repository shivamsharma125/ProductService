package com.shivam.productservice.services;

import com.shivam.productservice.dtos.Role;
import com.shivam.productservice.dtos.UserDetailsResponseDto;
import com.shivam.productservice.dtos.UserDto;
import com.shivam.productservice.exceptions.ProductNotFoundException;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.repositories.ProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service("selfProductService")
@Primary
public class SelfProductService implements ProductService {
    private ProductRepository productRepository;
    private RestTemplate restTemplate;
    private RedisTemplate<String,Object> redisTemplate;

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
        redisTemplate.opsForHash().put("PRODUCTS", "product_" + savedProduct.getId(), savedProduct);
        return savedProduct;
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        productRepository.findById(product.getId())
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Product savedProduct = productRepository.save(product);

        redisTemplate.opsForHash().put("PRODUCTS", "product_" + savedProduct.getId(), savedProduct);

        return savedProduct;
    }

    @Override
    public Product getProductById(Long productId) {
        Product product = (Product) redisTemplate.opsForHash().get("PRODUCTS", "product_" + productId);

        if (product != null) return product;

        product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        redisTemplate.opsForHash().put("PRODUCTS", "product_" + product.getId(), product);

        return product;
    }

    @Override
    public List<Product> getAllProducts(Long userId) {
        // This API is restricted
        // Can be called by ADMIN only

        UserDetailsResponseDto responseDto = restTemplate.getForObject(
                "http://UserService/users/" + userId,
                UserDetailsResponseDto.class
        );

        if (responseDto == null){
            throw new RuntimeException("Something went wrong");
        }

        UserDto userDto = responseDto.getUser();

        boolean isMentorOrAdmin = false;

        for (Role role : userDto.getRoles()){
            if ("MENTOR".equals(role.getName()) || "ADMIN".equals(role.getName())){
                isMentorOrAdmin = true;
                break;
            }
        }

        if (!isMentorOrAdmin){
            throw new RuntimeException("Not MENTOR or a ADMIN user");
        }

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
            redisTemplate.opsForHash().delete("PRODUCTS", "product_" + productId);
        }

        return isDeleted;
    }
}
