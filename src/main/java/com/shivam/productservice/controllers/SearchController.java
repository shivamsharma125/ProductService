package com.shivam.productservice.controllers;

import com.shivam.productservice.dtos.ProductDto;
import com.shivam.productservice.exceptions.InvalidRequestException;
import com.shivam.productservice.models.Product;
import com.shivam.productservice.services.SearchService;
import com.shivam.productservice.utils.ProductUtil;
import com.shivam.productservice.utils.RequestUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService){
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean isPremium,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        if (RequestUtils.isEmptyParam(query)) throw new InvalidRequestException("query cannot be empty");

        Page<Product> page = searchService.searchProducts(
                query, category, minPrice, maxPrice,
                isPremium, pageNo, pageSize, sortBy, direction
        );
        Page<ProductDto> dtoPage = page.map(ProductUtil::from);
        return ResponseEntity.ok(dtoPage);
    }
}
