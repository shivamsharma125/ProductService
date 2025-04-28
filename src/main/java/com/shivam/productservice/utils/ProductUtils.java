package com.shivam.productservice.utils;

import com.shivam.productservice.dtos.FakeStoreProductDto;
import com.shivam.productservice.models.Category;
import com.shivam.productservice.models.Product;

public class ProductUtils {
    public static Product convert(FakeStoreProductDto productDto){
        Product product = new Product();
        product.setId(productDto.getId());
        product.setTitle(productDto.getTitle());
        product.setPrice(productDto.getPrice());

        Category category = new Category();
        category.setTitle(productDto.getCategory());

        product.setCategory(category);
        product.setDescription(productDto.getDescription());
        product.setImageUrl(productDto.getImage());

        return product;
    }

    public static FakeStoreProductDto convert(Product product){
        FakeStoreProductDto fakeStoreProductDto = new FakeStoreProductDto();
        fakeStoreProductDto.setId(product.getId());
        fakeStoreProductDto.setTitle(product.getTitle());
        fakeStoreProductDto.setPrice(product.getPrice());
        fakeStoreProductDto.setCategory(product.getCategory().getTitle());
        fakeStoreProductDto.setDescription(product.getDescription());
        fakeStoreProductDto.setImage(product.getImageUrl());

        return fakeStoreProductDto;
    }
}
