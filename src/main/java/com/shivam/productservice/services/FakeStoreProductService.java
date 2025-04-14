package com.shivam.productservice.services;

import com.shivam.productservice.dtos.FakeStoreProductDto;
import com.shivam.productservice.dtos.ResponseDto;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.utils.ProductUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service("fakeStoreProductService")
public class FakeStoreProductService implements ProductService {
    private RestTemplate restTemplate;
    private RedisTemplate<String,Object> redisTemplate;

    public FakeStoreProductService(RestTemplate restTemplate,
                                   RedisTemplate<String,Object> redisTemplate){
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Product createProduct(Product product) {
        FakeStoreProductDto fakeStoreProductDto = ProductUtils.convert(product);
        FakeStoreProductDto responseProductDto = restTemplate.postForObject("https://fakestoreapi.com/products",
                fakeStoreProductDto, FakeStoreProductDto.class);
        if (responseProductDto == null) throw new RuntimeException("Something went wrong. Please try again!");
        Product savedProduct = ProductUtils.convert(responseProductDto);

        // save product in cache
        redisTemplate.opsForHash().put("PRODUCTS", "product_" + savedProduct.getId(), savedProduct);

        return savedProduct;
    }

    @Override
    public Product replaceProduct(Product product) {
        FakeStoreProductDto fakeStoreProductDto = ProductUtils.convert(product);
        RequestCallback requestCallback = restTemplate.httpEntityCallback(fakeStoreProductDto,FakeStoreProductDto.class);
        HttpMessageConverterExtractor<FakeStoreProductDto> responseExtractor =
                new HttpMessageConverterExtractor<>(FakeStoreProductDto.class, restTemplate.getMessageConverters());
        FakeStoreProductDto responseProductDto = restTemplate.execute("https://fakestoreapi.com/products/" + product.getId(),
                HttpMethod.PUT, requestCallback, responseExtractor);

        if (responseProductDto == null) throw new RuntimeException("Something went wrong while updating the product");

        Product savedProduct = ProductUtils.convert(responseProductDto);

        // save the product in cache
        redisTemplate.opsForHash().put("PRODUCTS", "product_" + savedProduct.getId(), savedProduct);

        return savedProduct;
    }

    @Override
    public Product updateProduct(Product product) {
        return replaceProduct(product);
    }

    @Override
    public Product getProductById(Long id) {
        // first check this product in the cache
        Product product = (Product) redisTemplate.opsForHash().get("PRODUCTS", "product_" + id);

        if (product != null) { // CACHE HIT
            return product;
        }

        // product doesn't found in cache (CACHE MISS), fetch it from FakeStore
        String fakeStoreURL = "https://fakestoreapi.com/products/" + id;
        FakeStoreProductDto productDto = restTemplate.getForObject(fakeStoreURL, FakeStoreProductDto.class);

        if (productDto == null) throw new RuntimeException("Product not available!");

        product = ProductUtils.convert(productDto);

        // save the result in the cache
        redisTemplate.opsForHash().put("PRODUCTS", "product_" + id, product);

        return product;
    }

    @Override
    public List<Product> getAllProducts(Long id) {
        String fakeStoreURL = "https://fakestoreapi.com/products";
        FakeStoreProductDto[] productDtos = restTemplate.getForObject(fakeStoreURL, FakeStoreProductDto[].class);

        if (productDtos == null) throw new RuntimeException("No products available!");

        List<Product> productList = new ArrayList<>();
        for(FakeStoreProductDto productDto : productDtos){
            Product product = ProductUtils.convert(productDto);
            productList.add(product);
        }

        return productList;
    }

    @Override
    public ResponseEntity<ResponseDto> deleteProduct(Long id) {
        RequestCallback requestCallback = restTemplate.httpEntityCallback(null,FakeStoreProductDto.class);
        HttpMessageConverterExtractor<FakeStoreProductDto> responseExtractor =
                new HttpMessageConverterExtractor<>(FakeStoreProductDto.class, restTemplate.getMessageConverters());
        FakeStoreProductDto responseProductDto = restTemplate.execute("https://fakestoreapi.com/products/" + id,
                HttpMethod.DELETE, requestCallback, responseExtractor);

        ResponseDto responseDto = new ResponseDto();
        if (responseProductDto == null){
            responseDto.setMessage("Something went wrong. Please try again.");
        } else {
            responseDto.setMessage("product with id " + id + " is successfully deleted.");
            // delete the product from cache as well
            redisTemplate.opsForHash().delete("PRODUCTS", "product_" + id);
        }

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Override
    public Page<Product> searchProduct(int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public Page<Product> searchProduct(int pageNumber, int pageSize, String sortingParam) {
        return null;
    }
}
