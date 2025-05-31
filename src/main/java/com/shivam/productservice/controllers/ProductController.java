package com.shivam.productservice.controllers;

import com.shivam.productservice.dtos.ProductDto;
import com.shivam.productservice.exceptions.InvalidRequestException;
import com.shivam.productservice.exceptions.ProductNotFoundException;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.services.ProductService;
import com.shivam.productservice.utils.ProductUtil;
import com.shivam.productservice.utils.RequestUtils;
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

    public ProductController(@Qualifier("selfProductService") ProductService productService){
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        List<Product> products = productService.getAllProducts();

        if(products == null) throw new ProductNotFoundException("No products found");

        List<ProductDto> productDtos = products.stream()
                .map(ProductUtil::from)
                .toList();

        return new ResponseEntity<>(productDtos,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto){
        if (RequestUtils.isEmptyParam(productDto.getTitle())) throw new InvalidRequestException("Product title cannot be empty");
        if (RequestUtils.isNotNull(productDto.getCategory()) && RequestUtils.isEmptyParam(productDto.getCategory().getTitle()))
            throw new InvalidRequestException("Category title cannot be empty");
        
        Product savedProduct = productService.createProduct(ProductUtil.from(productDto));
        return new ResponseEntity<>(from(savedProduct),HttpStatus.CREATED);
    }

    @GetMapping("{productId}/{userId}")
    public ResponseEntity<ProductDto> getProductDetailsBasedOnUserRole(@PathVariable Long productId,
                                                                       @PathVariable Long userId) {
        if (RequestUtils.isInvalidId(productId)) throw new InvalidRequestException("Invalid product id");
        if (RequestUtils.isInvalidId(userId)) throw new InvalidRequestException("Invalid user id");

        Product savedProduct = productService.getProductBasedOnUserRole(productId,userId);
        return ResponseEntity.ok(from(savedProduct));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long productId) {
        if(RequestUtils.isInvalidId(productId)) throw new InvalidRequestException("Invalid product id");
        Product product = productService.getProductById(productId);
        return new ResponseEntity<>(from(product), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") Long productId, @RequestBody ProductDto productDto){
        if(RequestUtils.isInvalidId(productId)) throw new InvalidRequestException("Invalid product id");
        if (RequestUtils.isEmptyParam(productDto.getTitle())) throw new InvalidRequestException("Product title cannot be empty");
        if (RequestUtils.isNotNull(productDto.getCategory()) && RequestUtils.isEmptyParam(productDto.getCategory().getTitle()))
            throw new InvalidRequestException("Category title cannot be empty");

        Product updatedProduct = productService.updateProduct(productId,from(productDto));
        return new ResponseEntity<>(from(updatedProduct), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long productId){
        if(RequestUtils.isInvalidId(productId)) throw new InvalidRequestException("Invalid product id");
        Boolean isDeleted = productService.deleteProduct(productId);

        if(!isDeleted)
            return new ResponseEntity<>("Something went wrong. Please try again", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>("product with id " + productId + " is successfully deleted", HttpStatus.OK);
    }
}
