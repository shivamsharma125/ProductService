package com.shivam.productservice.controllers;

import com.shivam.productservice.commons.AuthenticationCommons;
import com.shivam.productservice.dtos.ProductDto;
import com.shivam.productservice.dtos.UserDto;
import com.shivam.productservice.dtos.ValidateTokenResponseDto;
import com.shivam.productservice.exceptions.InvalidProductIdException;
import com.shivam.productservice.exceptions.ProductNotFoundException;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.services.ProductService;
import com.shivam.productservice.utils.ProductUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.shivam.productservice.utils.ProductUtil.from;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final AuthenticationCommons authenticationCommons;

    public ProductController(@Qualifier("selfProductService") ProductService productService,
                             AuthenticationCommons authenticationCommons){
        this.productService = productService;
        this.authenticationCommons = authenticationCommons;
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<ProductDto>> getAllProducts(@PathVariable("userId") Long userId){
        List<Product> products = productService.getAllProducts(userId);

        if(products == null) throw new ProductNotFoundException("No products found");

        List<ProductDto> productDtos = products.stream()
                .map(ProductUtil::from)
                .toList();

        return new ResponseEntity<>(productDtos,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){
        Product savedProduct = productService.createProduct(ProductUtil.from(productDto));
        return new ResponseEntity<>(from(savedProduct),HttpStatus.CREATED);
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long productId, @PathVariable("token") String token) {
        if(productId <= 0) throw new InvalidProductIdException(productId);

        // Service can be called by authenticated users only
        ValidateTokenResponseDto responseDto = authenticationCommons.validateToken(token);
        UserDto userDto = responseDto.getUserDto();
        if (userDto == null){
            throw new RuntimeException(responseDto.getFailureMessage());
        }
        Product product = productService.getProductById(productId);

        return new ResponseEntity<>(from(product), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") Long productId, @RequestBody ProductDto productDto){
        if(productId <= 0) throw new InvalidProductIdException(productId);
        Product updatedProduct = productService.updateProduct(productId,from(productDto));
        return new ResponseEntity<>(from(updatedProduct), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long productId){
        if(productId <= 0) throw new InvalidProductIdException(productId);
        Boolean isDeleted = productService.deleteProduct(productId);

        if(!isDeleted)
            return new ResponseEntity<>("Something went wrong. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>("product with id " + productId + " is successfully deleted", HttpStatus.OK);
    }
}
