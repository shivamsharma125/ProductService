package com.shivam.productservice.services;

import com.shivam.productservice.models.Product;
import com.shivam.productservice.repositories.ProductRepository;
import com.shivam.productservice.utils.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    private final ProductRepository productRepository;

    public SearchServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public Page<Product> searchProducts(String query, String category, Double minPrice,
                                        Double maxPrice, Boolean isPremium, int pageNo,
                                        int pageSize, String sortBy, String direction) {
        Specification<Product> spec = ProductSpecification
                .buildSearchSpecifications(query, category, minPrice, maxPrice, isPremium);

        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);

        return productRepository.findAll(spec,pageable);
    }
}
