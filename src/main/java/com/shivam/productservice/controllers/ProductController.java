package com.shivam.productservice.controllers;

import com.shivam.productservice.commons.AuthenticationCommons;
import com.shivam.productservice.dtos.ResponseDto;
import com.shivam.productservice.dtos.UserDto;
import com.shivam.productservice.dtos.ValidateTokenResponseDto;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private ProductService productService;
    private AuthenticationCommons authenticationCommons;

    public ProductController(ProductService productService,
                             AuthenticationCommons authenticationCommons){
        this.productService = productService;
        this.authenticationCommons = authenticationCommons;
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product){
        return productService.createProduct(product);
    }

    @PutMapping()
    public Product replaceProduct(@RequestBody Product product){
        return productService.replaceProduct(product);
    }

    @PatchMapping
    public Product updateProduct(@RequestBody Product product){
        return productService.updateProduct(product);
    }

    @GetMapping("/{id}/{token}")
    public Product getProductById(@PathVariable("id") Long id, @PathVariable("token") String token){
        // Service can be called by authenticated users only
        ValidateTokenResponseDto responseDto = authenticationCommons.validateToken(token);
        UserDto userDto = responseDto.getUserDto();
        if (userDto == null){
            throw new RuntimeException(responseDto.getFailureMessage());
        }
        return productService.getProductById(id);
    }

    @GetMapping
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ResponseDto> deleteProduct(@PathVariable("id") Long id){
        return productService.deleteProduct(id);
    }
}
