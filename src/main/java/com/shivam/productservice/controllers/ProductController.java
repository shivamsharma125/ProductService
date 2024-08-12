package com.shivam.productservice.controllers;

import com.shivam.productservice.dtos.ResponseDto;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
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

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id") Long id){
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
