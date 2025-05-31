package com.shivam.productservice.services;

import com.shivam.productservice.clients.FakeStoreApiClient;
import com.shivam.productservice.dtos.FakeStoreProductDto;
import com.shivam.productservice.exceptions.ProductNotFoundException;
import com.shivam.productservice.models.Category;
import com.shivam.productservice.models.Product;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("fakeStoreProductService")
public class FakeStoreProductService implements ProductService {
    private final FakeStoreApiClient fakeStoreApiClient;
    private final RedisTemplate<String,Object> redisTemplate;

    public FakeStoreProductService(FakeStoreApiClient fakeStoreApiClient,
                                   RedisTemplate<String,Object> redisTemplate){
        this.fakeStoreApiClient = fakeStoreApiClient;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<Product> getAllProducts() {
        List<FakeStoreProductDto> fakeStoreProductDtos = fakeStoreApiClient.getAllProducts();

        if (fakeStoreProductDtos == null) return new ArrayList<>();

        return fakeStoreProductDtos.stream()
                .map(this::from)
                .toList();
    }

    @Override
    public Product createProduct(Product product) {
        FakeStoreProductDto fakeStoreProductDto = from(product);

        fakeStoreProductDto = fakeStoreApiClient.createProduct(fakeStoreProductDto);

        if (fakeStoreProductDto == null) throw new RuntimeException("Something went wrong. Please try again!");

        Product savedProduct = from(fakeStoreProductDto);

        // save product in cache
        redisTemplate.opsForHash().put("PRODUCTS", savedProduct.getId(), savedProduct);

        return savedProduct;
    }

    @Override
    public Product getProductBasedOnUserRole(Long productId, Long userId) {
        return new Product();
    }

    @Override
    public Product getProductById(Long productId) throws ProductNotFoundException {
//      first check this product in the cache
        Product product = (Product) redisTemplate.opsForHash().get("PRODUCTS", productId);

        if (product != null) // CACHE HIT
            return product;

        // product doesn't found in cache (CACHE MISS), fetch it from FakeStore
        FakeStoreProductDto fakeStoreProductDto = fakeStoreApiClient.getProductById(productId);

        if(fakeStoreProductDto == null) throw new ProductNotFoundException(productId);

        product = from(fakeStoreProductDto);

        // save the result in the cache
        redisTemplate.opsForHash().put("PRODUCTS", productId, product);

        return product;
    }

    @Override
    public Product updateProduct(Long productId, Product product) {
        FakeStoreProductDto fakeStoreProductDto = from(product);

        fakeStoreProductDto = fakeStoreApiClient.updateProduct(productId,fakeStoreProductDto);

        if (fakeStoreProductDto == null)
            throw new RuntimeException("Something went wrong while updating the product. Please try again");

        Product savedProduct = from(fakeStoreProductDto);

        // save the product in cache
        redisTemplate.opsForHash().put("PRODUCTS", savedProduct.getId(), savedProduct);

        return savedProduct;
    }

    @Override
    public Boolean deleteProduct(Long productId) {
        FakeStoreProductDto responseProductDto = fakeStoreApiClient.deleteProduct(productId);

        if(responseProductDto != null) {
            // product is deleted from db, delete the product from cache as well
            redisTemplate.opsForHash().delete("PRODUCTS", productId);
        }

        return responseProductDto != null;
    }

    private FakeStoreProductDto from(Product product) {
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setId(product.getId());
        fakeStoreProductDto.setTitle(product.getTitle());
        fakeStoreProductDto.setPrice(product.getPrice());
        fakeStoreProductDto.setDescription(product.getDescription());
        fakeStoreProductDto.setImage(product.getImageUrl());
        if(product.getCategory() != null) {
            fakeStoreProductDto.setCategory(product.getCategory().getTitle());
        }
        return fakeStoreProductDto;
    }

    private Product from(FakeStoreProductDto fakeStoreProductDto){
        Product product = new Product();
        product.setId(fakeStoreProductDto.getId());
        product.setTitle(fakeStoreProductDto.getTitle());
        product.setPrice(fakeStoreProductDto.getPrice());

        Category category = new Category();
        category.setTitle(fakeStoreProductDto.getCategory());

        product.setCategory(category);
        product.setDescription(fakeStoreProductDto.getDescription());
        product.setImageUrl(fakeStoreProductDto.getImage());

        return product;
    }
}
