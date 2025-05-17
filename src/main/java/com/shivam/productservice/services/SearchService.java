package com.shivam.productservice.services;

import com.shivam.productservice.models.Product;
import org.springframework.data.domain.Page;

public interface SearchService {
    Page<Product> searchProducts(String query, String category, Double minPrice,
                                 Double maxPrice, Boolean isPremium, int pageNo,
                                 int pageSize, String sortBy, String direction);
}
