package com.shivam.productservice.utils;

import com.shivam.productservice.dtos.CategoryDto;
import com.shivam.productservice.dtos.ProductDto;
import com.shivam.productservice.models.Category;
import com.shivam.productservice.models.Product;

public class ProductUtil {
    public static ProductDto from(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setTitle(product.getTitle());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setIsPremium(product.getIsPremium());
        if(product.getCategory() != null) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setTitle(product.getCategory().getTitle());
            categoryDto.setId(product.getCategory().getId());
            categoryDto.setDescription(product.getCategory().getDescription());
            productDto.setCategory(categoryDto);
        }
        return productDto;
    }

    public static Product from(ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setTitle(productDto.getTitle());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setDescription(productDto.getDescription());
        product.setIsPremium(productDto.getIsPremium());
        if(productDto.getCategory() != null) {
            Category category = new Category();
            category.setTitle(productDto.getCategory().getTitle());
            product.setCategory(category);
        }
        return product;
    }
}
